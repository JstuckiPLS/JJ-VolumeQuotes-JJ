package com.pls.ltlrating.batch.migration;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.extint.shared.DataModuleVO;
import com.pls.ltlrating.batch.migration.model.enums.TariffGeoType;
import com.pls.smc3.service.SMC3Service;

/**
 * Class which match CTSI tariff name to SMC3 tariff name.
 *
 * @author Alex Kyrychenko.
 */
public class TariffMatcher implements JobExecutionListener {
    private static final Logger LOG = LoggerFactory.getLogger(TariffMatcher.class);

    private static final String TARIFF_NAME_SEP = "_";

    @Autowired
    private SMC3Service smc3Service;

    private List<DataModuleVO> availableTariffs;

    /**
     * FIXME Map to SMC3 Tariff Name.
     *
     * @param ctsiTariffName
     *            ctsiTariffName
     * @param tariffGeoType
     *            tariffGeoType
     * @return value
     * @throws ImportException
     *             exception
     */
    public String mapToSmc3TariffName(final String ctsiTariffName, final TariffGeoType tariffGeoType) throws ImportException {
        if (StringUtils.isBlank(ctsiTariffName)) {
            throw new ImportException("Tariff name is blank");
        }
        if (tariffGeoType == null) {
            throw new ImportException("Tariff geo type is null");
        }
        if (!ctsiTariffName.matches("[A-Z,\\d]+_\\d{8}(_\\d+-[A-Z]+)?")) {
            throw new ImportException(String.format("Bad tariff name format[%s]", ctsiTariffName));
        }
        if (CollectionUtils.isNotEmpty(availableTariffs)) {
            String tariffName = getCtsiTariffName(ctsiTariffName);
            String productNumber = getCtsiProductNumber(ctsiTariffName);
            List<DataModuleVO> filteredTariffs = availableTariffs.stream().filter(tariff -> tariff.getTariffName().contains(tariffName))
                    .sorted((t1, t2) -> tariffToRank(t1.getDescription()).compareTo(tariffToRank(t2.getDescription())))
                    .collect(Collectors.toList());
            String errorMsg = String.format("Couldn't find match for tariff[%s] with tariff geo type[%s]", ctsiTariffName, tariffGeoType.name());
            if (CollectionUtils.isEmpty(filteredTariffs)) {
                LOG.info("filteredTariffs is empty");
                throw new ImportException(errorMsg);
            }
            return filteredTariffs.stream()
                    .filter(tariff -> StringUtils.isBlank(productNumber) || tariff.getProductNumber().equals(productNumber))
                    .filter(tariff -> fiterByTariffGeoType(tariff.getDescription(), tariffGeoType))
                    .map(DataModuleVO::getTariffName)
                    .findFirst()
                    .orElseGet(() -> filteredTariffs.get(0).getTariffName());
        }
        throw new ImportException("List of available SMC3 tariffs was not received from RateWareXL.");
    }

    private boolean fiterByTariffGeoType(final String tariffDescription, final TariffGeoType tariffGeoType) {
        return tariffDescription.contains(tariffGeoType.getCode());
    }

    private String getCtsiTariffName(final String ctsiTariffName) {
        return StringUtils.countMatches(ctsiTariffName, TARIFF_NAME_SEP) == 2 ? StringUtils.substringBeforeLast(ctsiTariffName, TARIFF_NAME_SEP)
                : ctsiTariffName;
    }

    private String getCtsiProductNumber(final String ctsiTariffName) {
        return StringUtils.countMatches(ctsiTariffName, TARIFF_NAME_SEP) == 2 ? StringUtils.substringAfterLast(ctsiTariffName, TARIFF_NAME_SEP)
                : null;
    }


    @Override
    public void beforeJob(final JobExecution jobExecution) {
        try {
            availableTariffs = smc3Service.getAvailableTariffs();
        } catch (Exception e) {
            LOG.warn("Can't get all avaialable tariffs", e);
        }
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        availableTariffs = null;
    }


    /**
     * Method calculates rank for tariff based on origin/destination.
     * Universal tariff (US/CN and US/US) has highest rank - 0.
     * Tariff (US/CN) has rank 1.
     * Tariff (US/US) has rank 2.
     * Tariff (CN/CN) has rank 3.
     *
     * @param tariffDescription - SMC3 tariff description
     * @return tariff rank.
     */
    private Integer tariffToRank(String tariffDescription) {
        if (StringUtils.contains(tariffDescription, "US/CN")) {
            return StringUtils.contains(tariffDescription, "US/US") ? 0 : 1;
        }
        if (StringUtils.contains(tariffDescription, "US/US")) {
            return 2;
        }
        return 3;
    }
}

package com.pls.ltlrating.batch.analysis;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.ltlrating.batch.analysis.model.AnalysisItem;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;
import com.pls.ltlrating.domain.analysis.FAOutputDetailsEntity;
import com.pls.ltlrating.domain.analysis.FATariffsEntity;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Implementation of {@link SkipListener} for batch job.
 *
 * @author Aleksandr Leshchenko
 */
public class AnalysisItemSkipListener implements SkipListener<AnalysisItem, List<FAOutputDetailsEntity>> {
    private static final Logger LOG = LoggerFactory.getLogger(AnalysisItemSkipListener.class);

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void onSkipInRead(Throwable t) {
        LOG.error("skip item read", t);
    }

    @Override
    public void onSkipInWrite(List<FAOutputDetailsEntity> item, Throwable t) {
        LOG.error("skip item write " + ToStringBuilder.reflectionToString(item), t);
    }

    @Override
    public void onSkipInProcess(AnalysisItem item, Throwable t) {
        LOG.error("skip item in process " + ToStringBuilder.reflectionToString(item), t);
        Session currentSession = sessionFactory.getCurrentSession();
        FAInputDetailsEntity input = currentSession.get(FAInputDetailsEntity.class, item.getRowId());
        if (item.getTariffId() != null) {
            input.getAnalysis().getTariffs().stream()
                    .filter(tariff -> ObjectUtils.equals(tariff.getId(), item.getTariffId()))
                    .map(tariff -> getOutputDetailsForTariff(input, tariff, t.getMessage()))
                    .forEach(currentSession::save);
        } else {
            List<FATariffsEntity> matchedTariffs = input.getAnalysis().getTariffs().stream()
                    .filter(tariff -> ObjectUtils.equals(tariff.getCustomerId(), item.getCustomerId())
                            && tariff.getPricingProfileId() != null && tariff.getTariffType() != PricingType.SMC3)
                    .collect(Collectors.toList());
            if (!matchedTariffs.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("No pricing was built for following selected customer tariff");
                if (matchedTariffs.size() > 1) {
                    errorMessage.append('s');
                }
                errorMessage.append(": ");
                errorMessage.append(matchedTariffs.stream().map(FATariffsEntity::getTariffName).collect(Collectors.joining(", ")));
                matchedTariffs.forEach(tariff -> {
                    FAOutputDetailsEntity result = new FAOutputDetailsEntity();
                    result.setInputDetails(input);
                    result.setTariff(tariff);
                    result.setErrorMessage(errorMessage.toString());
                    currentSession.save(result);
                });
            }
        }
    }

    private FAOutputDetailsEntity getOutputDetailsForTariff(FAInputDetailsEntity input, FATariffsEntity tariff, String message) {
        FAOutputDetailsEntity result = new FAOutputDetailsEntity();
        result.setInputDetails(input);
        result.setTariff(tariff);
        result.setErrorMessage(message);
        return result;
    }

}

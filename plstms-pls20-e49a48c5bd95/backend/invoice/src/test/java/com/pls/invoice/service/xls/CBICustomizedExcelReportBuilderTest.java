package com.pls.invoice.service.xls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillingInvoiceNodeEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.shared.Status;
import com.pls.invoice.service.xsl.CBICustomizedExcelReportBuilder;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Test for {@link CBICustomizedExcelReportBuilder}.
 * 
 * @author Alexander Nalapko
 */
@RunWith(MockitoJUnitRunner.class)
public class CBICustomizedExcelReportBuilderTest {

    private static final String LINEHAUL_TYPE = "SRA";
    private static final String TRANSACTION_TYPE = "TX";
    private static final String FUEL_TYPE = "FS";
    private static final String BENCHMARK_TYPE = "SBR";

    private ClassPathResource standardCommonReport;
    private File testData;

    @Before
    public void setUp() {
        testData = new File(FileUtils.getTempDirectory(), getClass().getSimpleName() + System.nanoTime() + ".xlsx");
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(testData);
    }

    @Test
    public void test() throws IOException, ParseException {
        standardCommonReport = new ClassPathResource("templates/CBI_customized.xlsx");
        CBICustomizedExcelReportBuilder builder = new CBICustomizedExcelReportBuilder(standardCommonReport);
        BillToEntity billto = new BillToEntity();
        BillingInvoiceNodeEntity node = new BillingInvoiceNodeEntity();
        AddressEntity address = new AddressEntity();
        address.setAddress1("setAddress1");
        address.setCity("city");
        address.setStateCode("stateCode");
        address.setZip("1111");
        node.setAddress(address);
        billto.setName("bill to name");
        billto.setBillingInvoiceNode(node);
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>();
        for (int i = 0; i < 10; i++) {
            LoadAdjustmentBO bo = new LoadAdjustmentBO(getLoad(ShipmentDirection.INBOUND));
            invoices.add(bo);
        }
        for (int i = 0; i < 10; i++) {
            LoadAdjustmentBO bo = new LoadAdjustmentBO(getLoad(ShipmentDirection.OUTBOUND));
            invoices.add(bo);
        }
        builder.generateReport(billto, invoices, new Date(), "INVOICE NUMBER", new FileOutputStream(testData));
        Assert.assertNotNull(testData);
    }

    private LoadEntity getLoad(ShipmentDirection direction) throws ParseException {
        LoadEntity load = new LoadEntity();
        load.setId(1L);
        load.getNumbers().setBolNumber("bol-132420");
        load.getNumbers().setProNumber("pro-12341");
        load.getNumbers().setPoNumber("po-12342");
        load.getNumbers().setSoNumber("so-12342");
        load.getNumbers().setRefNumber("ref-1232");

        load.setWeight(10000);
        load.setMileage(10000);
        load.setStatus(ShipmentStatus.BOOKED);
        load.setCarrier(getCarrier());
        load.setShipmentDirection(direction);

        OrganizationLocationEntity location = new OrganizationLocationEntity();
        location.setLocationName("locationName");
        load.setLocation(location);

        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        origin.setDeparture(toDate("25/02/13"));
        origin.setAddress(new AddressEntity());
        origin.getAddress().setCity("city");
        origin.getAddress().setStateCode("stateCode");
        origin.getAddress().setZip("1111");
        load.addLoadDetails(origin);

        HashSet<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        LoadCostDetailsEntity loadCostDetailsEntity = new LoadCostDetailsEntity();
        loadCostDetailsEntity.setStatus(Status.ACTIVE);
        loadCostDetailsEntity.setGuaranteedNameForBOL("nameForBOL");
        loadCostDetailsEntity.setGuaranteedBy(1L);
        loadCostDetailsEntity.setTotalRevenue(BigDecimal.TEN);

        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();

        costDetailItems.add(addCostDetailItem(LINEHAUL_TYPE, CostDetailOwner.S, BigDecimal.TEN));
        costDetailItems.add(addCostDetailItem(TRANSACTION_TYPE, CostDetailOwner.S, BigDecimal.TEN));
        costDetailItems.add(addCostDetailItem(FUEL_TYPE, CostDetailOwner.S, BigDecimal.TEN));
        costDetailItems.add(addCostDetailItem(BENCHMARK_TYPE, CostDetailOwner.S, BigDecimal.TEN));

        loadCostDetailsEntity.setCostDetailItems(costDetailItems);
        costDetails.add(loadCostDetailsEntity);
        load.setCostDetails(costDetails);

        LoadDetailsEntity dest = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        dest.setDeparture(toDate("25/02/13"));
        dest.setAddress(new AddressEntity());
        dest.getAddress().setCity("cityd");
        dest.getAddress().setStateCode("stateCoded");
        dest.getAddress().setZip("1111d");
        load.addLoadDetails(dest);

        load.getOrigin().setLoadMaterials(new HashSet<LoadMaterialEntity>());
        LoadMaterialEntity loadMaterialEntity = new LoadMaterialEntity();
        loadMaterialEntity.setCommodityClass(CommodityClass.CLASS_100);
        load.getOrigin().getLoadMaterials().add(loadMaterialEntity);
        HashSet<LoadMaterialEntity> loadMaterials = new HashSet<LoadMaterialEntity>();
        loadMaterials.add(loadMaterialEntity);
        load.getOrigin().setLoadMaterials(loadMaterials);

        BillToEntity billTo = new BillToEntity();
        billTo.setId(123124L);
        load.setBillTo(billTo);
        return load;
    }

    private CarrierEntity getCarrier() {
        CarrierEntity carrier = new CarrierEntity();
        carrier.setName("carrier");
        carrier.setScac("SCAC");
        return carrier;
    }

    private CostDetailItemEntity addCostDetailItem(String accType, CostDetailOwner owner, BigDecimal subTotal) {
        CostDetailItemEntity costDetailItem = new CostDetailItemEntity();
        costDetailItem.setAccessorialType(accType);
        costDetailItem.setOwner(owner);
        costDetailItem.setSubtotal(subTotal);
        return costDetailItem;
    }

    private Date toDate(String date) throws ParseException {
        return new SimpleDateFormat("dd/MM/yy").parse(date);
    }

}

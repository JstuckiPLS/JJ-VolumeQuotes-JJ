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
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.shared.Status;
import com.pls.invoice.service.xsl.CBIGroupedExcelReportBuilder;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Test Generates CBI grouped excel report as .xlsx file.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class CBIGroupedExcelReportBuilderTest {

    private ClassPathResource cbiGroupedReport;
    private File testReport;

    @Before
    public void setUp() {
        cbiGroupedReport = new ClassPathResource("templates/CBI_grouped.xlsx");
        testReport = new File(FileUtils.getTempDirectory(), getClass().getSimpleName() + System.nanoTime() + ".xlsx");
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(testReport);
    }

    @Test
    public void generateCBIGroupedExcelReportTest() throws IOException, ParseException {
        CBIGroupedExcelReportBuilder builder = new CBIGroupedExcelReportBuilder(cbiGroupedReport);
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>();
        invoices.add(new LoadAdjustmentBO(getLoad()));

        long fileSizeBefore = testReport.length();
        builder.generateReport(invoices, String.valueOf(new Random().nextInt(10000)), new FileOutputStream(testReport));
        long fileSizeAfter = testReport.getTotalSpace();
        Assert.assertNotSame(fileSizeBefore, fileSizeAfter);
        Assert.assertTrue(fileSizeAfter > fileSizeBefore);
    }

    private LoadEntity getLoad() throws IOException, ParseException {
        LoadEntity load = new LoadEntity();
        load.setId(1L);
        load.getNumbers().setBolNumber("test_bol");
        load.getNumbers().setProNumber("test_pro");
        load.getNumbers().setPoNumber("test_po");
        load.setPieces(10);
        load.setWeight(1000);
        load.setStatus(ShipmentStatus.DELIVERED);

        CarrierEntity carrier = new CarrierEntity();
        carrier.setName("test_carrier");
        load.setCarrier(carrier);

        CustomerEntity customer = new CustomerEntity();
        load.setOrganization(customer);
        OrganizationLocationEntity location = new OrganizationLocationEntity();
        location.setLocationName("Test location");
        location.setOrganization(customer);
        UserEntity accUser = new UserEntity();
        accUser.setFirstName("Gordon");
        accUser.setLastName("Freeman");
        load.setLocation(location);

        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        origin.setDeparture(new SimpleDateFormat("dd/MM/yy").parse("25/02/13"));
        origin.setAddress(new AddressEntity());
        origin.getAddress().setAddress1("origin address1");
        origin.getAddress().setCity("origin city");
        origin.getAddress().setStateCode("origin stateCode");
        origin.getAddress().setZip("origin 111");
        origin.setContactName("origin contactName");
        origin.setContactPhone("origin 222");
        origin.setEarlyScheduledArrival(new Date());
        origin.setContact("origin test_contact");
        load.addLoadDetails(origin);

        HashSet<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        load.setCostDetails(costDetails);
        LoadCostDetailsEntity loadCostDetailsEntity = new LoadCostDetailsEntity();
        loadCostDetailsEntity.setStatus(Status.ACTIVE);
        loadCostDetailsEntity.setGuaranteedBy(1L);
        loadCostDetailsEntity.setTotalRevenue(BigDecimal.TEN);
        costDetails.add(loadCostDetailsEntity);
        load.setCostDetails(costDetails);

        LoadDetailsEntity destination = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        destination.setDeparture(new SimpleDateFormat("dd/MM/yy").parse("27/02/13"));
        destination.setArrivalWindowStart(new SimpleDateFormat("dd/MM/yy HH:mm:ss").parse("27/02/13 12:00:00"));
        destination.setArrivalWindowEnd(new SimpleDateFormat("dd/MM/yy HH:mm:ss").parse("27/02/13 14:00:00"));
        destination.setAddress(new AddressEntity());
        destination.getAddress().setAddress1("dest address");
        destination.getAddress().setCity("dest city");
        destination.getAddress().setStateCode("dest state code");
        destination.getAddress().setZip("dest zip");
        destination.setContactName("dest contact name");
        destination.setContactPhone("dest 123");
        destination.setContact("dest contact");
        load.addLoadDetails(destination);

        FreightBillPayToEntity freightBillPayTo = new FreightBillPayToEntity();
        AddressEntity address = new AddressEntity();
        address.setAddress1("address1");
        address.setAddress2("address2");
        address.setCity("city");
        address.setZip("65000");
        address.setStateCode("test_code");
        freightBillPayTo.setCompany("test_company");
        freightBillPayTo.setContactName("test_contact_name");
        freightBillPayTo.setAddress(address);
        load.setFreightBillPayTo(freightBillPayTo);

        load.getOrigin().setLoadMaterials(new HashSet<LoadMaterialEntity>());
        LoadMaterialEntity loadMaterialEntity = new LoadMaterialEntity();
        loadMaterialEntity.setCommodityClass(CommodityClass.CLASS_150);
        load.getOrigin().getLoadMaterials().add(loadMaterialEntity);
        HashSet<LoadMaterialEntity> loadMaterials = new HashSet<LoadMaterialEntity>();
        loadMaterials.add(loadMaterialEntity);
        load.getOrigin().setLoadMaterials(loadMaterials);

        return load;
    }
}

package com.pls.invoice.service.impl.processing;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.NetworkEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.shared.Status;
import com.pls.email.EmailTemplateRenderer;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.service.ShipmentUtils;

import freemarker.template.TemplateException;

/**
 * Test for {@link EmailTemplateRenderer}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailTemplateRendererTest {
    private EmailTemplateRenderer emailTemplateRenderer = new EmailTemplateRenderer();

    @Test
    public void shouldRenderBOLEmailContent() throws IOException, TemplateException, ParseException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("load", getLoad2());
        data.put("isBlindBol", false);
        data.put("totalQuantity", 1);
        data.put("totalPieces", 2);
        data.put("totalWeight", 3);
        data.put("isCanceled", false);
        data.put("clientUrl", "http://pls.com");
        data.put("carrier", getCarrier());
        data.put("estimatedTransitDay", ShipmentUtils.getEstimatedTransitDaysLabel(1050L));
        data.put("showCustomsBroker", true);
        data.put("contact", getContactInfo());

        String content = emailTemplateRenderer.renderEmailTemplate("BolEmailTemplate.ftl", data);

        Assert.assertFalse(StringUtils.isBlank(content));
    }

    private UserAdditionalContactInfoBO getContactInfo() {
        UserAdditionalContactInfoBO info = new UserAdditionalContactInfoBO();
        info.setContactName("name");
        info.setEmail("email@pls.com");
        PhoneBO phone = new PhoneBO();
        phone.setAreaCode("012");
        phone.setNumber("7654321");
        phone.setCountryCode("1");
        info.setPhone(phone);
        return info;
    }

    private LoadEntity getLoad2() throws ParseException {
        LoadEntity load = new LoadEntity();
        load.setId(1L);
        load.getNumbers().setBolNumber("2012-bol-10");
        load.getNumbers().setProNumber("2012-pro-11");
        load.getNumbers().setPoNumber("2012-po-12");
        load.setPieces(10);
        load.setWeight(10000);
        load.setStatus(ShipmentStatus.BOOKED);

        load.setCarrier(getCarrier());

        CustomerEntity customer = new CustomerEntity();
        NetworkEntity network = new NetworkEntity();
        customer.setNetwork(network);
        customer.setName("customer");
        load.setOrganization(customer);
        OrganizationLocationEntity location = new OrganizationLocationEntity();
        location.setOrganization(customer);
        UserEntity accUser = new UserEntity();
        accUser.setFirstName("first");
        accUser.setLastName("last");
        AccountExecutiveEntity accountExecutive = new AccountExecutiveEntity(location, accUser);
        location.getAccountExecutives().add(accountExecutive);
        load.setLocation(location);

        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        origin.setDeparture(toDate("25/02/13"));
        // origin.setArrivalWindowStart(toTime("25/02/13 12:00:00"));
        // origin.setArrivalWindowEnd(toTime("25/02/13 14:00:00"));
        origin.setAddress(new AddressEntity());
        origin.getAddress().setAddress1("address1");
        origin.getAddress().setCity("city");
        origin.getAddress().setStateCode("stateCode");
        origin.getAddress().setZip("1111");
        origin.setContactName("contactName");
        origin.setContactPhone("111");
        origin.setEarlyScheduledArrival(new Date());

        origin.setContact("contactO");
        load.addLoadDetails(origin);
        HashSet<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        load.setCostDetails(costDetails);
        LoadCostDetailsEntity loadCostDetailsEntity = new LoadCostDetailsEntity();
        loadCostDetailsEntity.setStatus(Status.ACTIVE);
        loadCostDetailsEntity.setGuaranteedNameForBOL("nameForBOL");
        loadCostDetailsEntity.setGuaranteedBy(1L);
        costDetails.add(loadCostDetailsEntity);

        TimeZoneEntity timezone = new TimeZoneEntity();
        timezone.setCode("PDT");
        origin.setScheduledArrivalTimeZone(timezone);

        LoadDetailsEntity dest = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        dest.setDeparture(toDate("27/02/13"));
        dest.setArrivalWindowStart(toTime("27/02/13 12:00:00"));
        dest.setArrivalWindowEnd(toTime("27/02/13 14:00:00"));
        dest.setAddress(new AddressEntity());
        dest.getAddress().setAddress1("address1");
        dest.getAddress().setCity("cityd");
        dest.getAddress().setStateCode("stateCoded");
        dest.getAddress().setZip("1111d");
        dest.setContactName("contactNamed");
        dest.setContactPhone("123");
        dest.setContact("contactD");
        load.addLoadDetails(dest);
        // load.setBillingAuditReasons(addBillingAuditReasonsEntity());
        load.setCostDetails(costDetails);
        CarrierInvoiceDetailsEntity vendorBill = new CarrierInvoiceDetailsEntity();
        vendorBill.setTotalCharges(BigDecimal.TEN);
        ArrayList<CarrierInvoiceDetailsEntity> listVendorBill = new ArrayList<CarrierInvoiceDetailsEntity>();
        listVendorBill.add(vendorBill);
        load.getVendorBillDetails().setCarrierInvoiceDetails(listVendorBill);
        load.setVolumeQuoteId("qid");
        FreightBillPayToEntity freightBillPayTo = new FreightBillPayToEntity();
        AddressEntity address = new AddressEntity();
        address.setAddress1("address1");
        address.setAddress2("address2");
        address.setCity("city");
        address.setZip("65000");
        address.setStateCode("sC");
        freightBillPayTo.setCompany("company");
        freightBillPayTo.setContactName("contactName");
        freightBillPayTo.setAddress(address);
        load.setFreightBillPayTo(freightBillPayTo);
        load.getOrigin().setLoadMaterials(new HashSet<LoadMaterialEntity>());
        LoadMaterialEntity loadMaterialEntity = new LoadMaterialEntity();
        loadMaterialEntity.setWeight(BigDecimal.ONE);
        loadMaterialEntity.setWidth(BigDecimal.ZERO);
        loadMaterialEntity.setHeight(BigDecimal.ZERO);
        loadMaterialEntity.setLength(BigDecimal.ONE);
        loadMaterialEntity.setCommodityClass(CommodityClass.CLASS_100);
        loadMaterialEntity.setQuantity("11");
        loadMaterialEntity.setPieces(17);
        loadMaterialEntity.setNmfc("nmfc");
        loadMaterialEntity.setProductDescription("productDescription");
        PackageTypeEntity packageType = new PackageTypeEntity();
        packageType.setDescription("description");
        loadMaterialEntity.setPackageType(packageType);
        load.getOrigin().getLoadMaterials().add(loadMaterialEntity);
        HashSet<LoadMaterialEntity> loadMaterials = new HashSet<LoadMaterialEntity>();
        loadMaterials.add(loadMaterialEntity);
        load.getOrigin().setLoadMaterials(loadMaterials);

        load.setCustomsBroker("broker");
        load.setCustomsBrokerPhone("+7150325948");

        return load;
    }

    private CarrierEntity getCarrier() {
        CarrierEntity carrier = new CarrierEntity();
        carrier.setName("carrier");
        return carrier;
    }

    private Date toDate(String date) throws ParseException {
        return new SimpleDateFormat("dd/MM/yy").parse(date);
    }

    private Date toTime(String time) throws ParseException {
        return new SimpleDateFormat("dd/MM/yy HH:mm:ss").parse(time);
    }
}

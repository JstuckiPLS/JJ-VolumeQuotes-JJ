package com.pls.dtobuilder.shipment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;
import com.pls.dto.shipment.ShipmentDocumentDTO;

/**
 * Test cases for {@link ShipmentDocumentDTOBuilder} class.
 * 
 * @author Maxim Medvedev
 */
public class ShipmentDocumentDTOBuilderTest {

    private ShipmentDocumentDTOBuilder sut = new ShipmentDocumentDTOBuilder();

    @Test
    public void shouldBuildDTOWithNormalCase() {
        ShipmentDocumentInfoBO data = prepareBo();

        ShipmentDocumentDTO result = sut.buildDTO(data);

        Assert.assertNotNull(result);
        Assert.assertSame(data.getModifiedDate(), result.getDate());
        Assert.assertSame(data.getCreatedDate(), result.getCreatedDate());
        Assert.assertSame(data.getId(), result.getId());
        Assert.assertSame(data.getDocName(), result.getName());
        Assert.assertSame(data.getShipmentId(), result.getShipmentId());
        Assert.assertEquals(data.getDocName() + ".ico", result.getFileName());
        Assert.assertEquals(data.getCreatedByFirstName() + " " + data.getCreatedByLastName(), result.getCreatedByName());
        Assert.assertSame(data.getDocFileType(), result.getDocFileType());
    }

    @Test
    public void shouldBuildDTOForBOL() {
        ShipmentDocumentInfoBO data = prepareBo();
        data.setDocName("BOL");
        data.setFileName(data.getFileName() + ".xlsx");

        ShipmentDocumentDTO result = sut.buildDTO(data);

        Assert.assertNotNull(result);
        Assert.assertSame(data.getModifiedDate(), result.getDate());
        Assert.assertSame(data.getCreatedDate(), result.getCreatedDate());
        Assert.assertSame(data.getId(), result.getId());
        Assert.assertSame(data.getDocName(), result.getName());
        Assert.assertSame(data.getShipmentId(), result.getShipmentId());
        Assert.assertEquals(data.getDocName() + ".xlsx", result.getFileName());
        Assert.assertEquals("Auto-generated", result.getCreatedByName());
        Assert.assertSame(data.getDocFileType(), result.getDocFileType());
    }

    @Test
    public void shouldBuildDTOForShippingLabels() {
        ShipmentDocumentInfoBO data = prepareBo();
        data.setDocName("Shipping label");
        data.setFileName(data.getFileName() + ".xlsx");

        ShipmentDocumentDTO result = sut.buildDTO(data);

        Assert.assertNotNull(result);
        Assert.assertSame(data.getModifiedDate(), result.getDate());
        Assert.assertSame(data.getCreatedDate(), result.getCreatedDate());
        Assert.assertSame(data.getId(), result.getId());
        Assert.assertSame(data.getDocName(), result.getName());
        Assert.assertSame(data.getShipmentId(), result.getShipmentId());
        Assert.assertEquals(data.getDocName() + ".xlsx", result.getFileName());
        Assert.assertEquals("Auto-generated", result.getCreatedByName());
        Assert.assertSame(data.getDocFileType(), result.getDocFileType());
    }

    @Test
    public void shouldBuildListWithNormalCase() {
        List<ShipmentDocumentInfoBO> data = new ArrayList<ShipmentDocumentInfoBO>();
        data.add(prepareBo());

        List<ShipmentDocumentDTO> result = sut.buildList(data);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    private ShipmentDocumentInfoBO prepareBo() {
        ShipmentDocumentInfoBO result = new ShipmentDocumentInfoBO();
        result.setModifiedDate(new Date());
        result.setCreatedDate(new Date(1000L));
        result.setId(1L);
        result.setDocName("TestDocument" + Math.random());
        result.setFileName("fileName" + Math.random() + ".ico");
        result.setCreatedByFirstName("createdByFirstName" + Math.random());
        result.setCreatedByLastName("createdByLastName" + Math.random());
        result.setDocFileType("docFileType" + Math.random());
        result.setShipmentId(3L);
        return result;
    }
}

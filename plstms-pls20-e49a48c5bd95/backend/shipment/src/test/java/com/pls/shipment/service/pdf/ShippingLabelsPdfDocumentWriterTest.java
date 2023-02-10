package com.pls.shipment.service.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.pls.core.common.utils.DateUtility;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.shared.Status;
import com.pls.core.util.TestUtils;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Test for ShippingLabelsPdfDocumentWriter. Verifies generation shipping labels document.
 * 
 * @author Dmitry Nikolaenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingLabelsPdfDocumentWriterTest {
    private String[] content = {
            "SHIPPER", "contact123", "ADDRESS123", "CITY123, ZIP123",
            "contactName123, phone123", "CONSIGNEE", "contact123",
            "ADDRESS123", "CITY123, ZIP123", "contactNamed123, 123",
            "test123@mail.com", "IDENTIFIERS", "Load ID#: 123", "BOL#: BOL123",
            "PRO#: PRO123", "PO#: PO123", "SO#: SO123", "PU#: PU123",
            "Ship Ref#: REF123", "Trailer#: TRAILER123",
            "GL#: GL123", "Service Type: ", "Guaranteed By: 12:01 AM",
            "", "Notes: ", "test123"
    };

    @InjectMocks
    private ShippingLabelsPdfDocumentWriter shippingLabelsPdfDocumentWriter;

    @Mock
    private ClassPathResource logo;

    @Test
    public void shouldGeneratePdf() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ShippingLabelsDocumentParameter documentParameter =
                new ShippingLabelsDocumentParameter(getLoad(), getLoad().getId(),
                        new ClassPathResource("/images/test-logo.png").getInputStream(), Printable.TEMPLATE1);
        shippingLabelsPdfDocumentWriter.write(documentParameter, outputStream);
        List<String> pdfContent = getPdfContent(outputStream);
        Date currentDate = Calendar.getInstance().getTime();
        Date dueDate = DateUtility.addDays(currentDate, 15);
        TestUtils.checkPdfContent(content, pdfContent, currentDate, "MM/dd/yy", dueDate);
        // outputStream.writeTo(new FileOutputStream(new File("c://temp/ShippingLabelsDocTest" + new Date().getTime() + ".pdf")));
    }

    private List<String> getPdfContent(ByteArrayOutputStream outputStream) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        PdfReader reader = new PdfReader(inputStream);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        StringArrayRenderListener stringArrayRenderListener = new StringArrayRenderListener();
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            stringArrayRenderListener = parser.processContent(i, stringArrayRenderListener);
        }
        return stringArrayRenderListener.getTextBlocks();
    }

    private LoadEntity getLoad() throws ParseException {
        LoadEntity load = new LoadEntity();
        load.setId(123L);
        load.getNumbers().setBolNumber("BOL123");
        load.getNumbers().setProNumber("PRO123");
        load.getNumbers().setPoNumber("PO123");
        load.getNumbers().setPuNumber("PU123");
        load.getNumbers().setSoNumber("SO123");
        load.getNumbers().setRefNumber("REF123");
        load.getNumbers().setTrailerNumber("TRAILER123");
        load.getNumbers().setGlNumber("GL123");
        load.setBolInstructions("test123");
        load.setPieces(10);
        load.setWeight(10000);
        load.setStatus(ShipmentStatus.BOOKED);
        load.setMileage(10);

        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        origin.setAddress(new AddressEntity());
        origin.getAddress().setAddress1("address123");
        origin.getAddress().setCity("city123");
        origin.getAddress().setStateCode("stateCode123");
        origin.getAddress().setZip("zip123");
        origin.setContactName("contactName123");
        origin.setContactPhone("phone123");
        origin.setContactEmail("test123@mail.com");
        origin.setEarlyScheduledArrival(new Date());
        origin.setContact("contact123");
        origin.setLoad(load);
        load.addLoadDetails(origin);

        HashSet<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        load.setCostDetails(costDetails);
        LoadCostDetailsEntity loadCostDetailsEntity = new LoadCostDetailsEntity();
        loadCostDetailsEntity.setStatus(Status.ACTIVE);
        loadCostDetailsEntity.setGuaranteedNameForBOL("nameForBOL");
        loadCostDetailsEntity.setGuaranteedBy(1L);
        loadCostDetailsEntity.setTotalRevenue(BigDecimal.TEN);
        costDetails.add(loadCostDetailsEntity);

        LoadDetailsEntity dest = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        dest.setAddress(new AddressEntity());
        dest.getAddress().setAddress1("address123");
        dest.getAddress().setCity("city123");
        dest.getAddress().setStateCode("stateCoded123");
        dest.getAddress().setZip("zip123");
        dest.setContactName("contactNamed123");
        dest.setContactPhone("123");
        dest.setContact("contact123");
        dest.setContactEmail("test123@mail.com");
        dest.setLoad(load);
        load.addLoadDetails(dest);

        return load;
    }
}

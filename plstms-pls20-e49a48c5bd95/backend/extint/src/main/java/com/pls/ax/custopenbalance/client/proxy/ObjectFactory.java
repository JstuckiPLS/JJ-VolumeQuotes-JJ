package com.pls.ax.custopenbalance.client.proxy;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * com.pls.ax.custopenbalance.client.proxy package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 *
 * @author Thomas Clancy
 */
@XmlRegistry
@SuppressWarnings("PMD")
public class ObjectFactory {

    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/",
        "unsignedLong");
    private final static QName _XppObjectBase_QNAME = new QName(
        "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.Ax.Xpp", "XppObjectBase");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/",
        "unsignedByte");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/",
        "unsignedShort");
    private final static QName _AifFault_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "AifFault");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/",
        "duration");
    private final static QName _CustOpenBalance_QNAME = new QName(
        "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", "CustOpenBalance");
    private final static QName _ArrayOfFaultMessage_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "ArrayOfFaultMessage");
    private final static QName _ArrayOfFaultMessageList_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "ArrayOfFaultMessageList");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/",
        "dateTime");
    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/",
        "anyType");
    private final static QName _String_QNAME
        = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _FaultMessage_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "FaultMessage");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/",
        "unsignedInt");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/",
        "decimal");
    private final static QName _ArrayOfInfologMessage_QNAME = new QName(
        "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", "ArrayOfInfologMessage");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/",
        "boolean");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/",
        "base64Binary");
    private final static QName _ArrayOfCustOpenBalance_QNAME = new QName(
        "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", "ArrayOfCustOpenBalance");
    private final static QName _InfologMessage_QNAME = new QName(
        "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", "InfologMessage");
    private final static QName _CallContext_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", "CallContext");
    private final static QName _FaultMessageList_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "FaultMessageList");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _AnyURI_QNAME
        = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _TriCustTransListDC_QNAME = new QName(
        "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", "TriCustTransListDC");
    private final static QName _ArrayOfKeyValueOfstringstring_QNAME = new QName(
        "http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfKeyValueOfstringstring");
    private final static QName _InfologMessageType_QNAME = new QName(
        "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", "InfologMessageType");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _Double_QNAME
        = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _CallContextLogonAsUser_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", "LogonAsUser");
    private final static QName _CallContextMessageId_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", "MessageId");
    private final static QName _CallContextPartitionKey_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", "PartitionKey");
    private final static QName _CallContextLanguage_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", "Language");
    private final static QName _CallContextPropertyBag_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", "PropertyBag");
    private final static QName _CallContextCompany_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", "Company");
    private final static QName _TriCustOpenBalanceServiceGetAllCustOpenBalancesResponseResponse_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2011/01/services", "response");
    private final static QName _CustOpenBalanceAccountNum_QNAME = new QName(
        "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", "AccountNum");
    private final static QName _TriCustTransListDCParmCustTransList_QNAME = new QName(
        "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", "parmCustTransList");
    private final static QName _TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequestAccountNum_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2011/01/services", "_accountNum");
    private final static QName _FaultMessageListDocumentOperation_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "DocumentOperation");
    private final static QName _FaultMessageListField_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "Field");
    private final static QName _FaultMessageListXmlLine_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "XmlLine");
    private final static QName _FaultMessageListXPath_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "XPath");
    private final static QName _FaultMessageListServiceOperationParameter_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "ServiceOperationParameter");
    private final static QName _FaultMessageListServiceOperation_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "ServiceOperation");
    private final static QName _FaultMessageListService_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "Service");
    private final static QName _FaultMessageListFaultMessageArray_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "FaultMessageArray");
    private final static QName _FaultMessageListXmlPosition_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "XmlPosition");
    private final static QName _FaultMessageListDocument_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "Document");
    private final static QName _InfologMessageMessage_QNAME = new QName(
        "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", "Message");
    private final static QName _AifFaultInfologMessageList_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "InfologMessageList");
    private final static QName _AifFaultStackTrace_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "StackTrace");
    private final static QName _AifFaultCustomDetailXml_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "CustomDetailXml");
    private final static QName _AifFaultFaultMessageListArray_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "FaultMessageListArray");
    private final static QName _FaultMessageMessage_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "Message");
    private final static QName _FaultMessageCode_QNAME = new QName(
        "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", "Code");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
     * com.pls.ax.custopenbalance.client.proxy
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ArrayOfKeyValueOfstringstring }
     *
     */
    public ArrayOfKeyValueOfstringstring createArrayOfKeyValueOfstringstring() {
        return new ArrayOfKeyValueOfstringstring();
    }

    /**
     * Create an instance of {@link TriCustOpenBalanceServiceGetAllCustOpenBalancesRequest }
     *
     */
    public TriCustOpenBalanceServiceGetAllCustOpenBalancesRequest createTriCustOpenBalanceServiceGetAllCustOpenBalancesRequest() {
        return new TriCustOpenBalanceServiceGetAllCustOpenBalancesRequest();
    }

    /**
     * Create an instance of {@link TriCustOpenBalanceServiceGetAllCustOpenBalancesResponse }
     *
     */
    public TriCustOpenBalanceServiceGetAllCustOpenBalancesResponse createTriCustOpenBalanceServiceGetAllCustOpenBalancesResponse() {
        return new TriCustOpenBalanceServiceGetAllCustOpenBalancesResponse();
    }

    /**
     * Create an instance of {@link TriCustTransListDC }
     *
     */
    public TriCustTransListDC createTriCustTransListDC() {
        return new TriCustTransListDC();
    }

    /**
     * Create an instance of {@link TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest }
     *
     */
    public TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest createTriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest() {
        return new TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest();
    }

    /**
     * Create an instance of {@link TriCustOpenBalanceServiceGetCustOpenBalanceByAccountResponse }
     *
     */
    public TriCustOpenBalanceServiceGetCustOpenBalanceByAccountResponse createTriCustOpenBalanceServiceGetCustOpenBalanceByAccountResponse() {
        return new TriCustOpenBalanceServiceGetCustOpenBalanceByAccountResponse();
    }

    /**
     * Create an instance of {@link CustOpenBalance }
     *
     */
    public CustOpenBalance createCustOpenBalance() {
        return new CustOpenBalance();
    }

    /**
     * Create an instance of {@link ArrayOfCustOpenBalance }
     *
     */
    public ArrayOfCustOpenBalance createArrayOfCustOpenBalance() {
        return new ArrayOfCustOpenBalance();
    }

    /**
     * Create an instance of {@link XppObjectBase }
     *
     */
    public XppObjectBase createXppObjectBase() {
        return new XppObjectBase();
    }

    /**
     * Create an instance of {@link CallContext }
     *
     */
    public CallContext createCallContext() {
        return new CallContext();
    }

    /**
     * Create an instance of {@link ArrayOfFaultMessage }
     *
     */
    public ArrayOfFaultMessage createArrayOfFaultMessage() {
        return new ArrayOfFaultMessage();
    }

    /**
     * Create an instance of {@link FaultMessage }
     *
     */
    public FaultMessage createFaultMessage() {
        return new FaultMessage();
    }

    /**
     * Create an instance of {@link AifFault }
     *
     */
    public AifFault createAifFault() {
        return new AifFault();
    }

    /**
     * Create an instance of {@link ArrayOfFaultMessageList }
     *
     */
    public ArrayOfFaultMessageList createArrayOfFaultMessageList() {
        return new ArrayOfFaultMessageList();
    }

    /**
     * Create an instance of {@link FaultMessageList }
     *
     */
    public FaultMessageList createFaultMessageList() {
        return new FaultMessageList();
    }

    /**
     * Create an instance of {@link InfologMessage }
     *
     */
    public InfologMessage createInfologMessage() {
        return new InfologMessage();
    }

    /**
     * Create an instance of {@link ArrayOfInfologMessage }
     *
     */
    public ArrayOfInfologMessage createArrayOfInfologMessage() {
        return new ArrayOfInfologMessage();
    }

    /**
     * Create an instance of {@link ArrayOfKeyValueOfstringstring.KeyValueOfstringstring }
     *
     */
    public ArrayOfKeyValueOfstringstring.KeyValueOfstringstring createArrayOfKeyValueOfstringstringKeyValueOfstringstring() {
        return new ArrayOfKeyValueOfstringstring.KeyValueOfstringstring();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XppObjectBase }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.Ax.Xpp", name
        = "XppObjectBase")
    public JAXBElement<XppObjectBase> createXppObjectBase(XppObjectBase value) {
        return new JAXBElement<XppObjectBase>(_XppObjectBase_QNAME, XppObjectBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AifFault }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "AifFault")
    public JAXBElement<AifFault> createAifFault(AifFault value) {
        return new JAXBElement<AifFault>(_AifFault_QNAME, AifFault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CustOpenBalance }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", name
        = "CustOpenBalance")
    public JAXBElement<CustOpenBalance> createCustOpenBalance(CustOpenBalance value) {
        return new JAXBElement<CustOpenBalance>(_CustOpenBalance_QNAME, CustOpenBalance.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFaultMessage }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name
        = "ArrayOfFaultMessage")
    public JAXBElement<ArrayOfFaultMessage> createArrayOfFaultMessage(ArrayOfFaultMessage value) {
        return new JAXBElement<ArrayOfFaultMessage>(_ArrayOfFaultMessage_QNAME, ArrayOfFaultMessage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFaultMessageList }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name
        = "ArrayOfFaultMessageList")
    public JAXBElement<ArrayOfFaultMessageList> createArrayOfFaultMessageList(ArrayOfFaultMessageList value) {
        return new JAXBElement<ArrayOfFaultMessageList>(_ArrayOfFaultMessageList_QNAME, ArrayOfFaultMessageList.class,
            null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaultMessage }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "FaultMessage")
    public JAXBElement<FaultMessage> createFaultMessage(FaultMessage value) {
        return new JAXBElement<FaultMessage>(_FaultMessage_QNAME, FaultMessage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfInfologMessage }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", name
        = "ArrayOfInfologMessage")
    public JAXBElement<ArrayOfInfologMessage> createArrayOfInfologMessage(ArrayOfInfologMessage value) {
        return new JAXBElement<ArrayOfInfologMessage>(_ArrayOfInfologMessage_QNAME, ArrayOfInfologMessage.class, null,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfCustOpenBalance }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", name
        = "ArrayOfCustOpenBalance")
    public JAXBElement<ArrayOfCustOpenBalance> createArrayOfCustOpenBalance(ArrayOfCustOpenBalance value) {
        return new JAXBElement<ArrayOfCustOpenBalance>(_ArrayOfCustOpenBalance_QNAME, ArrayOfCustOpenBalance.class, null,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfologMessage }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", name
        = "InfologMessage")
    public JAXBElement<InfologMessage> createInfologMessage(InfologMessage value) {
        return new JAXBElement<InfologMessage>(_InfologMessage_QNAME, InfologMessage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CallContext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", name = "CallContext")
    public JAXBElement<CallContext> createCallContext(CallContext value) {
        return new JAXBElement<CallContext>(_CallContext_QNAME, CallContext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaultMessageList }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name
        = "FaultMessageList")
    public JAXBElement<FaultMessageList> createFaultMessageList(FaultMessageList value) {
        return new JAXBElement<FaultMessageList>(_FaultMessageList_QNAME, FaultMessageList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TriCustTransListDC }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", name
        = "TriCustTransListDC")
    public JAXBElement<TriCustTransListDC> createTriCustTransListDC(TriCustTransListDC value) {
        return new JAXBElement<TriCustTransListDC>(_TriCustTransListDC_QNAME, TriCustTransListDC.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfKeyValueOfstringstring }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays", name
        = "ArrayOfKeyValueOfstringstring")
    public JAXBElement<ArrayOfKeyValueOfstringstring> createArrayOfKeyValueOfstringstring(
        ArrayOfKeyValueOfstringstring value) {
        return new JAXBElement<ArrayOfKeyValueOfstringstring>(_ArrayOfKeyValueOfstringstring_QNAME,
            ArrayOfKeyValueOfstringstring.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfologMessageType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", name
        = "InfologMessageType")
    public JAXBElement<InfologMessageType> createInfologMessageType(InfologMessageType value) {
        return new JAXBElement<InfologMessageType>(_InfologMessageType_QNAME, InfologMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", name = "LogonAsUser",
        scope = CallContext.class)
    public JAXBElement<String> createCallContextLogonAsUser(String value) {
        return new JAXBElement<String>(_CallContextLogonAsUser_QNAME, String.class, CallContext.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", name = "MessageId", scope
        = CallContext.class)
    public JAXBElement<String> createCallContextMessageId(String value) {
        return new JAXBElement<String>(_CallContextMessageId_QNAME, String.class, CallContext.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", name = "PartitionKey",
        scope = CallContext.class)
    public JAXBElement<String> createCallContextPartitionKey(String value) {
        return new JAXBElement<String>(_CallContextPartitionKey_QNAME, String.class, CallContext.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", name = "Language", scope
        = CallContext.class)
    public JAXBElement<String> createCallContextLanguage(String value) {
        return new JAXBElement<String>(_CallContextLanguage_QNAME, String.class, CallContext.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfKeyValueOfstringstring }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", name = "PropertyBag",
        scope = CallContext.class)
    public JAXBElement<ArrayOfKeyValueOfstringstring> createCallContextPropertyBag(ArrayOfKeyValueOfstringstring value) {
        return new JAXBElement<ArrayOfKeyValueOfstringstring>(_CallContextPropertyBag_QNAME,
            ArrayOfKeyValueOfstringstring.class, CallContext.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", name = "Company", scope
        = CallContext.class)
    public JAXBElement<String> createCallContextCompany(String value) {
        return new JAXBElement<String>(_CallContextCompany_QNAME, String.class, CallContext.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TriCustTransListDC }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2011/01/services", name = "response", scope
        = TriCustOpenBalanceServiceGetAllCustOpenBalancesResponse.class)
    public JAXBElement<TriCustTransListDC> createTriCustOpenBalanceServiceGetAllCustOpenBalancesResponseResponse(
        TriCustTransListDC value) {
        return new JAXBElement<TriCustTransListDC>(
            _TriCustOpenBalanceServiceGetAllCustOpenBalancesResponseResponse_QNAME, TriCustTransListDC.class,
            TriCustOpenBalanceServiceGetAllCustOpenBalancesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", name = "AccountNum",
        scope = CustOpenBalance.class)
    public JAXBElement<String> createCustOpenBalanceAccountNum(String value) {
        return new JAXBElement<String>(_CustOpenBalanceAccountNum_QNAME, String.class, CustOpenBalance.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfCustOpenBalance }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", name
        = "parmCustTransList", scope = TriCustTransListDC.class)
    public JAXBElement<ArrayOfCustOpenBalance> createTriCustTransListDCParmCustTransList(ArrayOfCustOpenBalance value) {
        return new JAXBElement<ArrayOfCustOpenBalance>(_TriCustTransListDCParmCustTransList_QNAME,
            ArrayOfCustOpenBalance.class, TriCustTransListDC.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2011/01/services", name = "_accountNum", scope
        = TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest.class)
    public JAXBElement<String> createTriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequestAccountNum(String value) {
        return new JAXBElement<String>(_TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequestAccountNum_QNAME,
            String.class, TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name
        = "DocumentOperation", scope = FaultMessageList.class)
    public JAXBElement<String> createFaultMessageListDocumentOperation(String value) {
        return new JAXBElement<String>(_FaultMessageListDocumentOperation_QNAME, String.class, FaultMessageList.class,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "Field", scope
        = FaultMessageList.class)
    public JAXBElement<String> createFaultMessageListField(String value) {
        return new JAXBElement<String>(_FaultMessageListField_QNAME, String.class, FaultMessageList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "XmlLine", scope
        = FaultMessageList.class)
    public JAXBElement<String> createFaultMessageListXmlLine(String value) {
        return new JAXBElement<String>(_FaultMessageListXmlLine_QNAME, String.class, FaultMessageList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "XPath", scope
        = FaultMessageList.class)
    public JAXBElement<String> createFaultMessageListXPath(String value) {
        return new JAXBElement<String>(_FaultMessageListXPath_QNAME, String.class, FaultMessageList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name
        = "ServiceOperationParameter", scope = FaultMessageList.class)
    public JAXBElement<String> createFaultMessageListServiceOperationParameter(String value) {
        return new JAXBElement<String>(_FaultMessageListServiceOperationParameter_QNAME, String.class,
            FaultMessageList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name
        = "ServiceOperation", scope = FaultMessageList.class)
    public JAXBElement<String> createFaultMessageListServiceOperation(String value) {
        return new JAXBElement<String>(_FaultMessageListServiceOperation_QNAME, String.class, FaultMessageList.class,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "Service", scope
        = FaultMessageList.class)
    public JAXBElement<String> createFaultMessageListService(String value) {
        return new JAXBElement<String>(_FaultMessageListService_QNAME, String.class, FaultMessageList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFaultMessage }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name
        = "FaultMessageArray", scope = FaultMessageList.class)
    public JAXBElement<ArrayOfFaultMessage> createFaultMessageListFaultMessageArray(ArrayOfFaultMessage value) {
        return new JAXBElement<ArrayOfFaultMessage>(_FaultMessageListFaultMessageArray_QNAME, ArrayOfFaultMessage.class,
            FaultMessageList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "XmlPosition",
        scope = FaultMessageList.class)
    public JAXBElement<String> createFaultMessageListXmlPosition(String value) {
        return new JAXBElement<String>(_FaultMessageListXmlPosition_QNAME, String.class, FaultMessageList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "Document",
        scope = FaultMessageList.class)
    public JAXBElement<String> createFaultMessageListDocument(String value) {
        return new JAXBElement<String>(_FaultMessageListDocument_QNAME, String.class, FaultMessageList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CustOpenBalance }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2011/01/services", name = "response", scope
        = TriCustOpenBalanceServiceGetCustOpenBalanceByAccountResponse.class)
    public JAXBElement<CustOpenBalance> createTriCustOpenBalanceServiceGetCustOpenBalanceByAccountResponseResponse(
        CustOpenBalance value) {
        return new JAXBElement<CustOpenBalance>(_TriCustOpenBalanceServiceGetAllCustOpenBalancesResponseResponse_QNAME,
            CustOpenBalance.class, TriCustOpenBalanceServiceGetCustOpenBalanceByAccountResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", name
        = "Message", scope = InfologMessage.class)
    public JAXBElement<String> createInfologMessageMessage(String value) {
        return new JAXBElement<String>(_InfologMessageMessage_QNAME, String.class, InfologMessage.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfInfologMessage }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name
        = "InfologMessageList", scope = AifFault.class)
    public JAXBElement<ArrayOfInfologMessage> createAifFaultInfologMessageList(ArrayOfInfologMessage value) {
        return new JAXBElement<ArrayOfInfologMessage>(_AifFaultInfologMessageList_QNAME, ArrayOfInfologMessage.class,
            AifFault.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "StackTrace",
        scope = AifFault.class)
    public JAXBElement<String> createAifFaultStackTrace(String value) {
        return new JAXBElement<String>(_AifFaultStackTrace_QNAME, String.class, AifFault.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name
        = "CustomDetailXml", scope = AifFault.class)
    public JAXBElement<String> createAifFaultCustomDetailXml(String value) {
        return new JAXBElement<String>(_AifFaultCustomDetailXml_QNAME, String.class, AifFault.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFaultMessageList }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name
        = "FaultMessageListArray", scope = AifFault.class)
    public JAXBElement<ArrayOfFaultMessageList> createAifFaultFaultMessageListArray(ArrayOfFaultMessageList value) {
        return new JAXBElement<ArrayOfFaultMessageList>(_AifFaultFaultMessageListArray_QNAME,
            ArrayOfFaultMessageList.class, AifFault.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "Message", scope
        = FaultMessage.class)
    public JAXBElement<String> createFaultMessageMessage(String value) {
        return new JAXBElement<String>(_FaultMessageMessage_QNAME, String.class, FaultMessage.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", name = "Code", scope
        = FaultMessage.class)
    public JAXBElement<String> createFaultMessageCode(String value) {
        return new JAXBElement<String>(_FaultMessageCode_QNAME, String.class, FaultMessage.class, value);
    }

}

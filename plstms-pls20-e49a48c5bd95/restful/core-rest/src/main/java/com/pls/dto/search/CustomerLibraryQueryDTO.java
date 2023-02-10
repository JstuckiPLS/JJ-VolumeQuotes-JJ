package com.pls.dto.search;

/**
 * DTO object to transfer data between client and server sides. Need to get Customer Library Data.
 * <p>
 * 
 * Example of usage:
 * 
 * <pre>
 * CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO("2008-05-25,2012-07-20,2008-05-25,2009-01-01,2009-01-31,256,A")
 * </pre>
 * 
 * @author Artem Arapov
 * 
 */
public class CustomerLibraryQueryDTO {

    private static final int FIELDS_COUNT = 7;

    private static final int FROM_DATE_FIELD = 0;

    private static final int TO_DATE_FIELD = 1;

    private static final int FROM_LOAD_DATE_FIELD = 2;

    private static final int TO_LOAD_DATE_FIELD = 3;

    private static final int PERSON_ID_FIELD = 4;

    private static final int STATUS_FIELD = 5;

    private static final int NAME_FIELD = 6;

    private static final String DELIMITER = ",";

    private String fromDate;

    private String toDate;

    private String fromLoadDate;

    private String toLoadDate;

    private String personId;

    private String status;

    private String name;

    /**
     * Default constructor.
     */
    public CustomerLibraryQueryDTO() {

    }

    /**
     * Constructor with parameter.
     * 
     * @param param
     *            parameter to transfer should be in format:
     *            "fromDate,toDate,fromLoadDate,toLoadDate,personId,status"
     */
    public CustomerLibraryQueryDTO(String param) {
        String[] tokens = param.split(DELIMITER, -1);

        if (tokens.length == FIELDS_COUNT) {
            fromDate = tokens[FROM_DATE_FIELD];
            toDate = tokens[TO_DATE_FIELD];
            fromLoadDate = tokens[FROM_LOAD_DATE_FIELD];
            toLoadDate = tokens[TO_LOAD_DATE_FIELD];
            personId = tokens[PERSON_ID_FIELD];
            status = tokens[STATUS_FIELD];
            name = tokens[NAME_FIELD];
        }
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getFromLoadDate() {
        return fromLoadDate;
    }

    public void setFromLoadDate(String fromLoadDate) {
        this.fromLoadDate = fromLoadDate;
    }

    public String getToLoadDate() {
        return toLoadDate;
    }

    public void setToLoadDate(String toLoadDate) {
        this.toLoadDate = toLoadDate;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

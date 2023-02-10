package com.pls.extint.domain.enums;

/**
 * Enum for the different data types of the API fields when creating the request. Data type URL is not used creating the request. When parsing the
 * response, only the data types STATIC_VALUE, LOOKUP and URL are used for setting data into PLS Fields.
 * 
 * @author Pavani Challa
 * 
 */
public enum DataType {

    LITERAL, STATIC_VALUE, URL, WELL_FORMED_XML, URI_PARAM, LOOKUP, QUERY_PARAM;

}

package com.pls.core.service.util;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Http message convert to convert DTO into string.
 * @author : Alexander Kirichenko
 */
public class Pojo2StringHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    /**
     * Default constructor.
     */
    public Pojo2StringHttpMessageConverter() {
        super(MediaType.TEXT_PLAIN, MediaType.TEXT_HTML);
    }

    private ObjectMapper objectMapper;

    /**
     * Set json object mapper.
     * @param objectMapper - json object mapper
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getName().contains("dto");
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return objectMapper.readerFor(clazz).readValue(inputMessage.getBody());
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        String formattedString = objectMapper.writeValueAsString(o);
        outputMessage.getBody().write(formattedString.getBytes("UTF8"));
    }
}

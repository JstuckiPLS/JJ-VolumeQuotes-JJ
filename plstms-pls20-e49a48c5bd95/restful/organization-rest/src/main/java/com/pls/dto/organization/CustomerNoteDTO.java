package com.pls.dto.organization;

import java.time.ZonedDateTime;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Customer Notes DTO.
 * 
 * @author Aleksandr Leshchenko
 */
@XmlRootElement
public class CustomerNoteDTO {
    private Integer id;
    private String text;
    private String username;
    private Long customerId;
    private ZonedDateTime createdDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.id != null && obj instanceof CustomerNoteDTO) {
            return this.id.equals(((CustomerNoteDTO) obj).id);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}

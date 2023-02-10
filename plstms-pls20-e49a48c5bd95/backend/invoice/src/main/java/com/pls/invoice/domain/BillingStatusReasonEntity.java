package com.pls.invoice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.http.annotation.Immutable;

import com.pls.core.domain.Identifiable;

/**
 * Entity for invoice error reason codes. Dictionary table.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "BILLING_STATUS_REASON_CODES")
@Immutable
public class BillingStatusReasonEntity implements Identifiable<String> {
    private static final long serialVersionUID = 8251685770246925555L;

    public static final String Q_BY_CODE = "com.pls.invoice.domain.BillingStatusReasonEntity.Q_BY_CODE";

    @Id
    @Column(name = "STATUS_REASON")
    private String id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

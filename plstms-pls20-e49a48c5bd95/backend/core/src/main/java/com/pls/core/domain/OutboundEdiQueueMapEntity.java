package com.pls.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

/**
 * Outbound EDI queue mapping entity.
 * 
 * @author Yasaman Honarvar
 *
 */
@Entity
@Table(name = "OUTBOUND_QUEUE_MAP")
@Where(clause = "SYSTEM='PLS20'")
public class OutboundEdiQueueMapEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 4714225247888332259L;

    public static final String Q_GET_ALL_QUEUE_MAPPINGS_BY_ID = "com.pls.core.domain.OutboundEdiQueueMapEntity.Q_GET_ALL_QUEUE_MAPPINGS_BY_ID";
    public static final String Q_GET_ALL_QUEUE_MAPPINGS_BY_SCAC = "com.pls.core.domain.OutboundEdiQueueMapEntity.Q_GET_ALL_QUEUE_MAPPINGS_BY_SCAC";

    @Id
    @Column(name = "ORG_ID")
    private Long id;

    @Column(name = "QUEUE_NAME")
    private String queueName;

    @Column(name = "SCAC")
    private String scac;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "SYSTEM")
    private String originSystem;

    public String getQueueName() {
        return queueName;
    }

    public String getScac() {
        return scac;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getOriginSystem() {
        return originSystem;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setOriginSystem(String originSystem) {
        this.originSystem = originSystem;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}

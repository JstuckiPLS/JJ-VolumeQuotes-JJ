package com.pls.core.domain.bo;

/**
 * BO for Location used for shipment.
 * 
 * @author Aleksandr Leshchenko
 */
public class ShipmentLocationBO {
    private Long id;
    private String name;
    private Long billToId;
    private Boolean defaultNode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public Boolean isDefaultNode() {
        return defaultNode;
    }

    public void setDefaultNode(Boolean defaultNode) {
        this.defaultNode = defaultNode;
    }
}

package com.pls.dto.organization;

/**
 * Simple customer info DTO.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class CustomerInfoDTO {

    private Long id;

    private String name;

    private Long customerNetworkId;

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

    public Long getCustomerNetworkId() {
        return customerNetworkId;
    }

    public void setCustomerNetworkId(Long customerNetworkId) {
        this.customerNetworkId = customerNetworkId;
    }
}

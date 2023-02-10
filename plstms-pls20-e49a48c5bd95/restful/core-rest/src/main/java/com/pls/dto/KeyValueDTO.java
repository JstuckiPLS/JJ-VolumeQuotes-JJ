package com.pls.dto;

/**
 * DTO to transfer key - value pair.
 * 
 * @author Artem Arapov
 *
 */
public class KeyValueDTO {

    private Long id;

    private String name;

    /**
     * Default constructor.
     */
    public KeyValueDTO() {
    }

    /**
     * Constructor.
     * 
     * @param id - id field
     * @param name - name field
     */
    public KeyValueDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

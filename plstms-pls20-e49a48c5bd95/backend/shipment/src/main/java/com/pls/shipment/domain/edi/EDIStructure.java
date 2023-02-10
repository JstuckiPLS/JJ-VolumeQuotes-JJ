package com.pls.shipment.domain.edi;

import java.util.Map;
import java.util.TreeMap;

/**
 * Abstract class to represent a EDI logical structure such as the whole EDI file, Functional group, Transaction, Segment.
 *
 * @param <T> structure type class
 * @author Mikhail Boldinov, 27/07/14
 */
public abstract class EDIStructure<T> {

    private String id;

    private Map<String, T> elements = new TreeMap<String, T>();

    /**
     * EDI structure constructor.
     *
     * @param id structure id
     */
    protected EDIStructure(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Retrieve EDI structure from EDI hierarchy by its id.
     * <p/>
     * If the structure doesn't exist yet, it's created and placed into appropriate place of EDI data.
     *
     * @param id structure id
     * @return EDI structure
     */
    public T get(String id) {
        if (elements.containsKey(id)) {
            return elements.get(id);
        }
        T element = create(id);
        elements.put(id, element);
        return element;
    }

    /**
     * Instantiates EDI structure using appropriate EDIStructure#create implementation.
     *
     * @param id structure id
     * @return new instance of appropriate EDI structure
     */
    public abstract T create(String id);

    public Map<String, T> getElements() {
        return elements;
    }
}

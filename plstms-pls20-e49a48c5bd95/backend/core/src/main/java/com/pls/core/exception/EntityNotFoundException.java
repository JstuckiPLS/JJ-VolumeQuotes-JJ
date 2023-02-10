package com.pls.core.exception;


/**
 * Signals that requested entity was not found in the data storage.
 * 
 * @author Viacheslav Krot
 */
public class EntityNotFoundException extends ApplicationException {
    private static final long serialVersionUID = 8405454642647203345L;

    /**
     * Constructs a new exception with the specified detail message.
     * @param   message the detail message.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }


    /**
     * Constructs a new exception with class and id of entity that was not found.
     * 
     * @param clazz
     *            class of entity that was not found.
     * @param id
     *            id of entity that was not found.
     */
    public EntityNotFoundException(Class<?> clazz, Object id) {
        super(String.format("Entity of type '%s' with id '%s' not found",
                clazz.getName(), id != null ? id.toString() : ""));
    }

}

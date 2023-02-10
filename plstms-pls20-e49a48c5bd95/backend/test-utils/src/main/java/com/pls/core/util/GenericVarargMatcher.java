package com.pls.core.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;

/**
 * Extension of Mockito argument matcher that matches varargs values. Should receive arrays of values specified as generic parameter to match with
 * actual parameters.
 * @param <T> type of expected value
 *
 * @author Denis Zhupinsky (Team International)
 */
public class GenericVarargMatcher<T> extends ArgumentMatcher<T> implements VarargMatcher {
    private static final long serialVersionUID = -2247437060433665200L;

    private T expectedValues;

    /**
     * Constructor.
     *
     * @param expectedValues expected values
     */
    public GenericVarargMatcher(T expectedValues) {
        this.expectedValues = expectedValues;
    }

    @Override
    public boolean matches(Object varargsArgument) {
        return new EqualsBuilder().append(expectedValues, varargsArgument).isEquals();
    }
}

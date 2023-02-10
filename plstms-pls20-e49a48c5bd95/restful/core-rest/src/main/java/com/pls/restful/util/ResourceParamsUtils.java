package com.pls.restful.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.exception.ApplicationException;

/**
 * Utility class for working with Resource.
 * 
 * @author Brichak Aleksandr
 */
public final class ResourceParamsUtils {

    private ResourceParamsUtils() {
    }

    private static final Pattern WILDCARD_PATTERN = Pattern.compile("(^[*]?[^*]{3,}[*]?$)|(^[^*]{1,3}$)");

    /**
     * Checks parameter for compliance with wildcard pattern and prepares it for Query.
     * 
     * @param param
     *            WildCard text
     * @return parameter prepared to be used in SQL/HQL etc.
     * @throws ApplicationException
     *             if the wrong parameter was entered.
     */
    public static String checkAndPrepareWildCardSearchParameter(String param) throws ApplicationException {
        String verifiedParam = param != null && WILDCARD_PATTERN.matcher(param).matches() ? param : null;
        if (StringUtils.isNotBlank(param) && StringUtils.isEmpty(verifiedParam)) {
            throw new ApplicationException("You've entered wrong WildCard search criteria.");
        }
        if (StringUtils.isNotBlank(param) && StringUtils.isNotBlank(verifiedParam)) {
            verifiedParam = StringUtils.replace(verifiedParam, "*", "%");
        }
        return verifiedParam;
    }

}

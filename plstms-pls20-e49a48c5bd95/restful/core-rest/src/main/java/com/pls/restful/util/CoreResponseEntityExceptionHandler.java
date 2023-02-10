package com.pls.restful.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.InvalidFormatException;
import com.pls.core.service.util.exception.FileSizeLimitException;
import com.pls.core.service.validation.ValidationException;
import com.pls.user.service.exc.PasswordsDoNotMatchException;

/**
 * Mapping for common application exceptions.
 * 
 * @author Maxim Medvedev
 */
@ControllerAdvice
public class CoreResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CoreResponseEntityExceptionHandler.class);

    @Value("${mode.production}")
    private boolean productionMode = true;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Handle {@link HttpStatus#BAD_REQUEST} related exceptions.
     * 
     * @param exception
     *            Not <code>null</code> {@link Exception}.
     * @return Not <code>null</code> result.
     */
    @ExceptionHandler({ ConstraintViolationException.class, DataIntegrityViolationException.class,
            ImportException.class, ApplicationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleBadRequest(Exception exception) {
        return prepareResponse(exception);
    }

    /**
     * Handle {@link HttpStatus#CONFLICT} related exceptions.
     * 
     * @param exception
     *            Not <code>null</code> {@link Exception}.
     * @return Not <code>null</code> result.
     */
    @ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class, PasswordsDoNotMatchException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handleConflict(Exception exception) {
        return prepareResponse(exception);
    }

    /**
     * Handle {@link HttpStatus#FORBIDDEN} related exceptions.
     * 
     * @param exception
     *            Not <code>null</code> {@link Exception}.
     * @return Not <code>null</code> result.
     */
    @ExceptionHandler({ AccessDeniedException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Map<String, Object> handleForbidden(Exception exception) {
        return prepareResponse(exception);
    }

    /**
     * Handle {@link HttpStatus#INTERNAL_SERVER_ERROR} related exceptions.
     * 
     * @param exception
     *            Not <code>null</code> {@link Exception}.
     * @return Not <code>null</code> result.
     */
    @ExceptionHandler({ Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleInternalServerError(Exception exception) {
        return prepareResponse(exception);
    }

    /**
     * Handle {@link HttpStatus#NOT_FOUND} related exceptions.
     * 
     * @param exception
     *            Not <code>null</code> {@link Exception}.
     * @return Not <code>null</code> result.
     */
    @ExceptionHandler({ EntityNotFoundException.class, com.pls.core.exception.EntityNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleNotFound(Exception exception) {
        return prepareResponse(exception);
    }

    /**
     * Handle {@link HttpStatus#UPGRADE_REQUIRED} related exceptions.
     * 
     * @param exception
     *            Not <code>null</code> {@link Exception}.
     * @return Not <code>null</code> result.
     */
    @ExceptionHandler({ StaleObjectStateException.class, HibernateOptimisticLockingFailureException.class })
    @ResponseStatus(HttpStatus.UPGRADE_REQUIRED)
    @ResponseBody
    public Map<String, Object> handleUpgradeRequired(Exception exception) {
        return prepareResponse(exception);
    }

    /**
     * Handle {@link HttpStatus#PRECONDITION_FAILED} related exceptions.
     * 
     * @param exception
     *            Not <code>null</code> {@link Exception}.
     * @param request the current request
     * @return Not <code>null</code> result.
     */
    @ExceptionHandler({ FileSizeLimitException.class, InvalidFormatException.class, ValidationException.class })
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    public Object handlePreconditionFailed(Exception exception, WebRequest request) {
        Map<String, Object> result;

        if (exception instanceof ValidationException) {
            result = prepareResponse(exception, ((ValidationException) exception).getErrors(), ((ValidationException) exception).getErrorMsg());
        } else {
            result = prepareResponse(exception);
        }
        if (request.getHeader("accept").contains("application/json")) {
            return result;
        }
        String response;
        try {
            response = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            LOG.error("Cannot convert exception to json string during rest exception handling", e);
            response = exception.getMessage();
        }
        return response;
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", exception, RequestAttributes.SCOPE_REQUEST);
        }

        return new ResponseEntity<Object>(prepareResponse(exception, body), headers, status);
    }

    private Map<String, Object> prepareResponse(Exception exception) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("message", StringUtils.trimToEmpty(exception.getMessage()));
        result.put("exceptionClassName", exception.getClass().getSimpleName());
        result.put("date", new Date());
        LOG.warn(exception.getMessage(), exception);
        if (!productionMode) {
            StringWriter stringWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(stringWriter));
            result.put("stackTrace", stringWriter.toString());
        }
        return result;
    }

    private Map<String, Object> prepareResponse(Exception exception, Object payload) {
        Map<String, Object> result = prepareResponse(exception);
        if (payload != null) {
            result.put("payload", payload);
        }
        return result;
    }

    private Map<String, Object> prepareResponse(Exception exception, Object payload, String errorMsg) {
        Map<String, Object> result = prepareResponse(exception);
        if (payload != null) {
            result.put("payload", payload);
        }
        if (errorMsg != null && !errorMsg.isEmpty()) {
            result.put("errorMsg", errorMsg);
        }
        return result;
    }

}

package com.pls.scheduler.util;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.core.Ordered;

/**
 * Spring bean post-processor that looks for the schedulers classes with annotation {@link EnableScheduler} and then decides
 * whether these schedulers should be run or not based on data from configuration file. At start it looks for default value from property(run.all)
 * for all schedulers. If this property is omitted in config file then by default it becomes false. Value which is defined for a specific scheduler
 * in config file overrides the value defined for all schedulers.
 *
 * @author Alexander Kirichenko
 */
public class PlsScheduledAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered {
    private static final String SCHEDULER_PREFIX = "schedulers.";

    @Value("${schedulers.run.all}")
    private String runAll;

    @Autowired
    private AbstractBeanFactory beanFactory;

    private boolean enableAll = false;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (targetClass.isAnnotationPresent(EnableScheduler.class)) {
            String schedulerName = targetClass.getAnnotation(EnableScheduler.class).name();
            if (StringUtils.isBlank(schedulerName)) {
                schedulerName = targetClass.getCanonicalName();
            }
            if (schedulerName == null) {
                return bean;
            }
            String schedulerConfig = getPropertyValue(schedulerName);
            if ((!enableAll && StringUtils.isBlank(schedulerConfig))
                    || (StringUtils.isNotBlank(schedulerConfig) && !Boolean.parseBoolean(schedulerConfig))) {
                return null;
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 1;
    }

    /**
     * Post constructor.
     */
    @PostConstruct
    protected void postConstruct() {
        enableAll = Boolean.parseBoolean(StringUtils.defaultIfBlank(runAll, "true"));
    }

    /**
     * This function uses BeanFactory, because this bean is run at the project startup and resolving property
     * through Application Context and Environment is not working.
     * 
     * @param propertyName
     *            name of the property
     * @return property value if present or <code>null</code>
     */
    private String getPropertyValue(String propertyName) {
        try {
            return beanFactory.resolveEmbeddedValue("${" + SCHEDULER_PREFIX + propertyName + "}");
        } catch (Exception e) {
            return null;
        }
    }
}

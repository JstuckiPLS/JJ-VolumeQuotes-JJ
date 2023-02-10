package com.pls.scheduler.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class level annotation which allows to configure whether this scheduler should be started or not based on a value from configuration file.
 * It has one optional attribute - name. Using this attribute you can define name for your scheduler class.
 * If this attribute was omitted then {@link PlsScheduledAnnotationBeanPostProcessor} will take class full qualify name as a name for your scheduler.
 * Config file contains default flag(run.all) for all custom schedulers in the system. If it is omitted then by default it is equal to false.
 * Value which is defined for a specific scheduler in config file overrides the value defined for all schedulers.
 *
 * @author Alexander Kirichenko
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableScheduler {

    /**
     * Represent the name of the scheduler. If omitted then full qualify class name will be taken as the
     * schedulers name.
     * 
     * @return name of the scheduler.
     */
    String name() default "";
}

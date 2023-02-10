package com.pls.restful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Rest service for running scheduled tasks.
 * 
 * @author Aleksandr Leshchenko
 */
@Controller
@RequestMapping("/test")
@Profile("QAServer")
public class ScheduledTaskExecutorResource {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Method runs scheduled task or any other method of bean by name of class and name of method.
     * 
     * @param beanName
     *            name of bean
     * @param methodName
     *            name of method
     * @throws Exception
     *             exception
     */
    @RequestMapping(value = "/runScheduledTask", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void runScheduledTask(@RequestParam("beanName") String beanName, @RequestParam("methodName") String methodName) throws Exception {
        MethodInvokingFactoryBean factory = new MethodInvokingFactoryBean();
        factory.setTargetObject(applicationContext.getBean(Class.forName(beanName)));
        factory.setTargetMethod(methodName);
        factory.prepare();
        factory.invoke();
    }
}

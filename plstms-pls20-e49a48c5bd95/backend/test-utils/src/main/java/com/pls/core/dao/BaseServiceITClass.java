package com.pls.core.dao;

import org.springframework.test.context.ContextConfiguration;

/**
 * Base class for service integration tests. This class loads all service*.xml and related dao*.xml contexts.
 * 
 * @author Maxim Medvedev
 */
@ContextConfiguration({ "classpath*:spring/service-*.xml" })
public class BaseServiceITClass extends AbstractDaoTest {
}

package com.pls.core.service.util.exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * Utility for operation with context.
 *
 * @author Denis Zhupinsky (Team International)
 */
public final class ContextUtils {
    private ContextUtils() {
    }

    /**
     * Scan package and find classes that marked with annotation.
     *
     * @param basePackage package to scan
     * @param clazz annotation class to find
     * @return list of found classes
     * @throws IOException if IO exception happens
     * @throws ClassNotFoundException if class not found
     */
    public static List<Class<?>> findClasses(String basePackage, Class<?> clazz) throws IOException, ClassNotFoundException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class<?>> candidates = new ArrayList<Class<?>>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + resolveBasePackage(basePackage) + "/" + "**/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (isCandidate(metadataReader, clazz)) {
                    candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                }
            }
        }
        return candidates;
    }

    private static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static boolean isCandidate(MetadataReader metadataReader, Class<?> clazz) throws ClassNotFoundException {
        Class c = Class.forName(metadataReader.getClassMetadata().getClassName());
        return c.getAnnotation(clazz) != null;
    }
}

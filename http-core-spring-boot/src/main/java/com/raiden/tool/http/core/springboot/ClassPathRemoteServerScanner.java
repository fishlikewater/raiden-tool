package com.raiden.tool.http.core.springboot;

import com.raiden.tool.http.HttpBootStrap;
import com.raiden.tool.http.annotation.HttpServer;
import com.raiden.tool.http.core.springboot.proxy.SpringCglibInterfaceProxy;
import com.raiden.tool.http.core.springboot.proxy.SpringJdkInterfaceProxy;
import com.raiden.tool.http.enums.ProxyEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Set;

/**
 * @author 陈添明
 */
@Slf4j
public class ClassPathRemoteServerScanner extends ClassPathBeanDefinitionScanner {

    private final ClassLoader classLoader;
    private final ProxyEnum proxyType;
    private final HttpBootStrap httpBootStrap;

    public ClassPathRemoteServerScanner(BeanDefinitionRegistry registry, ClassLoader classLoader, ProxyEnum proxyType, HttpBootStrap httpBootStrap) {
        super(registry, false);
        this.classLoader = classLoader;
        this.proxyType = proxyType;
        this.httpBootStrap = httpBootStrap;
    }

    public void registerFilters() {
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(HttpServer.class);
        this.addIncludeFilter(annotationTypeFilter);
    }


    @NonNull
    @Override
    protected Set<BeanDefinitionHolder> doScan(@NonNull String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            log.warn("No   was found in '" + Arrays.toString(basePackages)
                    + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(
            AnnotatedBeanDefinition beanDefinition) {
        if (beanDefinition.getMetadata().isInterface()) {
            try {
                Class<?> target = ClassUtils.forName(
                        beanDefinition.getMetadata().getClassName(),
                        classLoader);
                return !target.isAnnotation();
            } catch (Exception ex) {
                log.error("load class exception:", ex);
            }
        }
        return false;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition)holder.getBeanDefinition();
            if (log.isDebugEnabled()) {
                log.debug("Creating RemoteServerBean with name '" + holder.getBeanName()
                        + "' and '" + definition.getBeanClassName() + "' Interface");
            }
            definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
            definition.getPropertyValues().add("httpClientProcessor", httpBootStrap.getHttpClientProcessor());
            definition.getPropertyValues().add("httpClientBeanFactory", httpBootStrap.getHttpClientBeanFactory());
            if (proxyType == ProxyEnum.JDK){
                definition.setBeanClass(SpringJdkInterfaceProxy.class);
            }else {
                definition.setBeanClass(SpringCglibInterfaceProxy.class);
            }
            definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }
}

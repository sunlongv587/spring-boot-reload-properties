package com.sunlong.config;

import com.sunlong.annotation.RefreshScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 自定义的springboot上下文类
 *
 * @author rongdi
 * @date 2019-09-21 10:30:01
 */
@Component
public class SpringBootContext implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(SpringBootContext.class);

    private final static Map<Class<?>, RefreshScopeBeanInvoker> refreshScopeBeanInvokorMap = new HashMap<>();

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringBootContext.applicationContext == null) {
            SpringBootContext.applicationContext = applicationContext;
        }
        try {
            /**
             * 初始化准备好哪些类需要更新配置，放入map
             */
            init();

        } catch (Exception e) {
            logger.error("init refresh bean error", e);
        }

    }

    /**
     * 获取所有需要动态刷新配置的类，就是带有自定义注解 @RefreshScope 放入待刷新的集合里
     *
     * @throws ClassNotFoundException
     */
    private void init() throws ClassNotFoundException {
        // 获取Spring Bean 工厂
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        // 获取工厂里的所有beanDefinition
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {

            BeanDefinition bd = defaultListableBeanFactory.getBeanDefinition(beanName);
            // 非注解的Bean不考虑；
            if (!(bd instanceof AnnotatedBeanDefinition)) {
                return;
            }

            AnnotationMetadata at = ((AnnotatedBeanDefinition) bd).getMetadata();
            String refreshScopeAnnotationName = RefreshScope.class.getName();
            if (at.isAnnotated(refreshScopeAnnotationName)) {
                Class<?> clazz = Class.forName(at.getClassName());
                refreshScopeBeanInvokorMap.put(clazz,
                        new RefreshScopeBeanInvoker(defaultListableBeanFactory, beanName, clazz));
            }
        }
    }

    /**
     * 根据传入属性刷新spring容器中的配置
     *
     * @param props
     */
    public void refreshConfig(Map<String, Object> props) throws InvocationTargetException, IllegalAccessException {
        if (props.isEmpty() || refreshScopeBeanInvokorMap.isEmpty()) {
            return;
        }

        // 遍历所有的invoker，重新设值
        for (Iterator<Map.Entry<Class<?>, RefreshScopeBeanInvoker>> iter = refreshScopeBeanInvokorMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<Class<?>, RefreshScopeBeanInvoker> entry = iter.next();
            RefreshScopeBeanInvoker invoker = entry.getValue();

            invoker.refreshPropsIntoField(props);
        }

    }

}

package com.sunlong.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 封装的执行器，主要负责真正修改属性值
 *
 * @author rongdi
 * @date 2019-09-21 10:10:01
 */
public class RefreshScopeBeanInvoker {

    private final static String VALUE_REGEX = "\\$\\{(.*)}";

    private DefaultListableBeanFactory defaultListableBeanFactory;

    private String beanName;

    private Class<?> clazz;

    public RefreshScopeBeanInvoker(DefaultListableBeanFactory defaultListableBeanFactory, String beanName, Class<?> clazz) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        this.beanName = beanName;
        this.clazz = clazz;
    }

    /**
     * 把属性值刷新到属性中
     *
     * @param props
     */
    public void refreshPropsIntoField(Map<String, Object> props) throws IllegalAccessException, InvocationTargetException {
        // 根据beanName和beanType获取spring容器中的对象
        Object bean = defaultListableBeanFactory.getBean(beanName);
        if (bean == null) {
            bean = defaultListableBeanFactory.getBean(clazz);
        }

        // 获取所有可用的属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 如果属性被@Value修饰
            Value valueAnn = field.getAnnotation(Value.class);
            String valueAnnValue;
            if (valueAnn == null || StringUtils.isEmpty(valueAnnValue = valueAnn.value())) {
                continue;
            }

            String key = valueAnnValue.replaceAll(VALUE_REGEX, "$1");

            // 就不调用set方法了，直接给属性赋值吧。
            if (props.containsKey(key)) {
                field.setAccessible(true);
                field.set(bean, props.get(key));
            }
        }
        // 获取所有的方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            // 如果方法被 @Value 修饰
            Value valueAnn = method.getAnnotation(Value.class);
            String valueAnnValue;
            if (valueAnn == null || StringUtils.isEmpty(valueAnnValue = valueAnn.value())) {
                continue;
            }
            String key = valueAnnValue.replaceAll(VALUE_REGEX, "$1");
            Parameter[] parameters = method.getParameters();
            // 被 @Value 注解的方法没有参数？
            if (parameters == null || parameters.length == 0) {
                continue;
            }
            if (props.containsKey(key)) {
                // 执行此方法
                method.invoke(bean, props.get(key));
            }
        }
    }

}

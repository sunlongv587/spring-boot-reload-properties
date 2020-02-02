package com.sunlong.config;

import com.sun.jmx.mbeanserver.Util;
import com.sunlong.annotation.RefreshScope;
import com.sunlong.listener.ReloadListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的springboot上下文类
 *
 * @author rongdi
 * @date 2019-09-21 10:30:01
 */
@Slf4j
@Component
public class SpringBootContext implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(SpringBootContext.class);

    private final static Map<Class<?>, RefreshScopeBeanInvoker> refreshScopeBeanInvokorMap = new HashMap<>();

    public static ApplicationContext applicationContext;

    private boolean isRun = true;

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
            Environment environment = applicationContext.getBean(Environment.class);
            String activeProfile = environment.getActiveProfiles()[0];

            String target = "application-" + activeProfile + ".properties";
            String path = SpringBootContext.class.getResource("/" + target).getPath();

            /**
             * 设置需要监听的文件目录（只能监听目录）
             */
            WatchService watchService = FileSystems.getDefault().newWatchService();
            String baseDir = path.substring(0, path.length() - target.length());
            Paths.get(baseDir).register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            /**
             * 注册监听事件，修改，创建，删除
             */
//        WatchKey register = p.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10))
                    .execute(() -> {

                        while (isRun) {
                            try {
                                /**
                                 * 拿出一个轮询所有event，如果有事件触发watchKey.pollEvents();这里就有返回
                                 * 其实这里类似于nio中的Selector的轮询，都是属于非阻塞轮询
                                 */
                                WatchKey watchKey = watchService.take();
                                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                                for (WatchEvent<?> event : watchEvents) {
                                    //获取监听Path
                                    Path eventPath = Util.cast(event.context());
                                    //只关注目标文件
                                    if (!target.equals(eventPath.toString())) {
                                        continue;
                                    }
                                    /**
                                     * 拼接一个文件全路径执行onChanged方法刷新配置
                                     */
                                    //                        String fileName =  filePath + File.separator +event.context();
                                    Path fullPath = Paths.get(baseDir, eventPath.toString());
                                    log.info("start update config event,fileName:{}", fullPath.toString());
                                    new ReloadListener().onChange(this, fullPath);
                                }
                                watchKey.reset();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                log.error("error", e);
                            }
                        }

                    });
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
                continue;
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

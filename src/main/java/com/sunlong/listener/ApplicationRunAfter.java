package com.sunlong.listener;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @author sunlong
 * @since 2020-01-03
 */
//@Component
public class ApplicationRunAfter implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        Configurations configs = new Configurations();
//        configs.

//        Parameters params = new Parameters();
//        ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder =
//                new ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
//                        .configure(params.fileBased()
//                                // 文件从 spring.profile.active 环境中读取
//                                .setFile(new File("src/main/resources/application.properties"))
//                                .setEncoding("UTF-8"));
//        PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(),
//                null, 5, TimeUnit.SECONDS);
//        trigger.start();
//
////        builder.addEventListener(ConfigurationBuilderEvent.ANY, new EventListener<ConfigurationBuilderEvent>() {
////        builder.addEventListener(ConfigurationBuilderEvent.RESET, new EventListener<ConfigurationBuilderEvent>() {
//        builder.addEventListener(ConfigurationBuilderResultCreatedEvent.RESULT_CREATED, new ConfigurationLogListener());
//        PropertiesConfiguration configuration = builder.getConfiguration();
//        while (true) {
//            Thread.sleep(10000);
//            System.out.println(builder.getConfiguration().getString("property1"));
//        }

    }
}

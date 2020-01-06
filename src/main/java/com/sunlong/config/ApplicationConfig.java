package com.sunlong.config;

import com.sunlong.listener.ConfigurationLogListener;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderResultCreatedEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.concurrent.TimeUnit;

/**
 * @author sunlong
 * @since 2020-01-05
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public PropertiesConfiguration commonConfig(Environment environment) throws Exception {
        String activeProfile = environment.getActiveProfiles()[0];
        String path = "application-" + activeProfile + ".properties";
        Parameters params = new Parameters();
        ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                new ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
                        .configure(params.fileBased()
                                .setPath(path)
                                // 文件从 spring.profile.active 环境中读取
//                                .setFile(new File("src/main/resources/application.properties"))
                                .setEncoding("UTF-8"));
        PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(),
                null, 5, TimeUnit.SECONDS);
        trigger.start();

//        builder.addEventListener(ConfigurationBuilderEvent.ANY, new EventListener<ConfigurationBuilderEvent>() {
//        builder.addEventListener(ConfigurationBuilderEvent.RESET, new EventListener<ConfigurationBuilderEvent>() {
        builder.addEventListener(ConfigurationBuilderResultCreatedEvent.RESULT_CREATED, new ConfigurationLogListener());
        return builder.getConfiguration();
    }

}

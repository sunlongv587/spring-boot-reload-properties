package com.sunlong;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ConfigurationBuilderResultCreatedEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;

import static org.apache.commons.configuration2.builder.ConfigurationBuilderEvent.CONFIGURATION_REQUEST;

/**
 * @author sunlong
 * @since 2020-01-04
 */
public class TestDynamicProps {

    public static void main(String[] args) throws Exception {

        Parameters params = new Parameters();
        ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                new ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
                        .configure(params.fileBased()
                                .setFile(new File("src/main/resources/application.properties"))
                                .setEncoding("UTF-8"));
        PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(),
                null, 5, TimeUnit.SECONDS);
        trigger.start();

//        builder.addEventListener(ConfigurationBuilderEvent.ANY, new EventListener<ConfigurationBuilderEvent>() {
//        builder.addEventListener(ConfigurationBuilderEvent.RESET, new EventListener<ConfigurationBuilderEvent>() {
        builder.addEventListener(ConfigurationBuilderResultCreatedEvent.RESULT_CREATED, new EventListener<ConfigurationBuilderEvent>() {

            @Override
            public void onEvent(ConfigurationBuilderEvent event) {
                if (CONFIGURATION_REQUEST.equals(event.getEventType())) {
                    return;
                }
                System.out.println(event.getEventType());
                try {
                    ImmutableConfiguration configuration = event.getSource().getConfiguration();
                    Iterator<String> keysIterator = configuration.getKeys();
                    while (keysIterator.hasNext()) {
                        String next = keysIterator.next();
                        System.out.println("key: " + next);
                        String value = configuration.get(String.class, next);
                        System.out.println("value: " + value);
                    }

                } catch (ConfigurationException e) {
                    e.printStackTrace();
                }
                System.out.println("Event:" + event);
            }
        });

        while (true) {
            Thread.sleep(10000);
            System.out.println(builder.getConfiguration().getString("property1"));
        }

    }


}

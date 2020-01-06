package com.sunlong.listener;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.Iterator;

import static org.apache.commons.configuration2.builder.ConfigurationBuilderEvent.CONFIGURATION_REQUEST;

/**
 * @author sunlong
 * @since 2019-12-31
 */
public class ConfigurationLogListener implements EventListener<ConfigurationBuilderEvent> {

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

//        if (!event.isBeforeUpdate()) {
//            // only display events after the modification was done
//            System.out.println("Received event!");
//            System.out.println("Type = " + event.getEventType());
//            if (event.getPropertyName() != null) {
//                System.out.println("Property name = " + event.getPropertyName());
//            }
//            if (event.getPropertyValue() != null) {
//                System.out.println("Property value = " + event.getPropertyValue());
//            }
//        }

    }
}

package com.consumer.weatherapi.WeatherRest.Controller;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Controller to start/stop kafka listener
@RestController
@RequestMapping("/kafka")
public class KafkaControlController {
    private final KafkaListenerEndpointRegistry registry;

    public KafkaControlController(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/start")
    public String startListener() {
        try {
            var container = registry.getListenerContainer("weatherListener");
            if (container != null && !container.isRunning()) {
                container.start();
                return " Kafka listener started.";
            } else {
                return " Kafka listener is already running or not configured.";
            }
        } catch (Exception e) {
            return " Failed to start Kafka listener: " + e.getMessage();
        }
    }

    @PostMapping("/stop")
    public String stopListener() {
        try {
            var container = registry.getListenerContainer("weatherListener");
            if (container != null && container.isRunning()) {
                container.stop();
                return " Kafka listener stopped.";
            } else {
                return " Kafka listener is already stopped or not configured.";
            }
        } catch (Exception e) {
            return " Failed to stop Kafka listener: " + e.getMessage();
        }
    }
}


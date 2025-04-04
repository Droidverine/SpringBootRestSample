package com.consumer.weatherapi.WeatherRest.Controller;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaControlController {
    private final KafkaListenerEndpointRegistry registry;

    public KafkaControlController(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/start")
    public String startListener() {
        registry.getListenerContainer("weatherListener").start();
        return "Kafka listener started.";
    }

    @PostMapping("/stop")
    public String stopListener() {
        registry.getListenerContainer("weatherListener").stop();
        return "Kafka listener stopped.";
    }
}


package com.consumer.weatherapi.WeatherRest.Consumer;

import com.consumer.weatherapi.WeatherRest.Model.WeatherMetric;
import com.consumer.weatherapi.WeatherRest.Repository.WeatherMetricRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WeatherConsumerService {

    private final WeatherMetricRepository repository;
    private final ObjectMapper objectMapper;

    public WeatherConsumerService(WeatherMetricRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()); // Required for ZonedDateTime
    }

    @KafkaListener(id = "weatherListener", topics = "weather-metrics", groupId = "weather-consumer-group", autoStartup = "false")
    public void consume(String message) {
        try {
            WeatherMetric metric = objectMapper.readValue(message, WeatherMetric.class);
            repository.save(metric);
            System.out.println("✅ Saved from Kafka: " + metric);
        } catch (Exception e) {
            System.err.println("❌ Failed to parse/save message: " + message);
            e.printStackTrace();
        }
    }
}

package com.consumer.weatherapi.WeatherRest.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
public class WeatherMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Timestamp is required")
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @NotBlank(message = "Sensor ID cannot be blank")
    private String sensorId;

    @NotNull(message = "Temperature is required")
    @DecimalMin(value = "-100.0", message = "Temperature too low")
    @DecimalMax(value = "100.0", message = "Temperature too high")
    private Double temperature;

    @NotNull(message = "Humidity is required")
    @DecimalMin(value = "0.0", message = "Humidity too low")
    @DecimalMax(value = "100.0", message = "Humidity too high")
    private Double humidity;

    // Constructors
    public WeatherMetric() {
    }

    public WeatherMetric(String sensorId, double temperature, double humidity, LocalDateTime timestamp) {
        this.sensorId = sensorId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "WeatherMetric{" +
                "id=" + id +
                ", sensorId='" + sensorId + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", timestamp=" + timestamp +
                '}';
    }
}

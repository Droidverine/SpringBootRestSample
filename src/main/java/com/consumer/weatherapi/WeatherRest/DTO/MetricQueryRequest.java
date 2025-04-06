package com.consumer.weatherapi.WeatherRest.DTO;

import java.time.ZonedDateTime;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

//DTO For in-memory query
public class MetricQueryRequest {
    private List<String> sensorIds;
    
    @NotEmpty(message = "Metrics cannot be empty")
    private List<String> metrics;
    
    @NotNull(message = "Statistic is required")
    private String statistic; // "avg", "min", "max", "sum"
    
    
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public String getStatistic() {
        return statistic;
    }

    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }
}

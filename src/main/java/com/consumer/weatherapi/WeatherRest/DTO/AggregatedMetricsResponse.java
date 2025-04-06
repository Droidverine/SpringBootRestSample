package com.consumer.weatherapi.WeatherRest.DTO;


//DTO For Native DB query
public class AggregatedMetricsResponse {
    private String metric;
    private Double value;

    public AggregatedMetricsResponse(String metric, Double value) {
        this.metric = metric;
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}

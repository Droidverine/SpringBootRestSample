package com.consumer.weatherapi.WeatherRest.Service;

import com.consumer.weatherapi.WeatherRest.DTO.MetricQueryRequest;
import com.consumer.weatherapi.WeatherRest.Model.WeatherMetric;
import com.consumer.weatherapi.WeatherRest.Repository.WeatherMetricRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherMetricQueryService {

    private final WeatherMetricRepository repository;

    public WeatherMetricQueryService(WeatherMetricRepository repository) {
        this.repository = repository;
    }



    public Map<String, Double> queryMetrics(MetricQueryRequest request) {
        ZonedDateTime startZoned = request.getStartDate() != null
                ? request.getStartDate()
                : ZonedDateTime.now().minusDays(1);

        ZonedDateTime endZoned = request.getEndDate() != null
                ? request.getEndDate()
                : ZonedDateTime.now();

        LocalDateTime start = startZoned.toLocalDateTime();
        LocalDateTime end = endZoned.toLocalDateTime();

        List<WeatherMetric> metrics = request.getSensorIds() == null || request.getSensorIds().isEmpty()
                ? repository.findByTimestampBetween(start, end)
                : repository.findBySensorIdInAndTimestampBetween(request.getSensorIds(), start, end);


        Map<String, Double> result = new HashMap<>();

        for (String metric : request.getMetrics()) {
            List<Double> values = metrics.stream()
                    .map(m -> {
                        switch (metric) {
                            case "temperature": return m.getTemperature();
                            case "humidity": return m.getHumidity();
                            default: return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (values.isEmpty()) continue;

            switch (request.getStatistic()) {
                case "avg":
                    result.put(metric, values.stream().mapToDouble(Double::doubleValue).average().orElse(0));
                    break;
                case "min":
                    result.put(metric, values.stream().mapToDouble(Double::doubleValue).min().orElse(0));
                    break;
                case "max":
                    result.put(metric, values.stream().mapToDouble(Double::doubleValue).max().orElse(0));
                    break;
                case "sum":
                    result.put(metric, values.stream().mapToDouble(Double::doubleValue).sum());
                    break;
            }
        }

        return result;
    }
}

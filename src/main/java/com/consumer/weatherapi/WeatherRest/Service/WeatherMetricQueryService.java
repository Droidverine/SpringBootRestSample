package com.consumer.weatherapi.WeatherRest.Service;

import com.consumer.weatherapi.WeatherRest.DTO.AggregatedMetricsResponse;
import com.consumer.weatherapi.WeatherRest.DTO.MetricQueryRequest;
import com.consumer.weatherapi.WeatherRest.Model.WeatherMetric;
import com.consumer.weatherapi.WeatherRest.Repository.WeatherMetricRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherMetricQueryService {

    private final WeatherMetricRepository repository;
    @PersistenceContext
    private EntityManager entityManager;

    public WeatherMetricQueryService(WeatherMetricRepository repository) {
        this.repository = repository;
    }


    //To Get sensor/sensors data in given date and time and manually performing avg,min,max,sum in memory. This is useful for NOSQL DBs
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
                            case "temperature":
                                return m.getTemperature();
                            case "humidity":
                                return m.getHumidity();
                            default:
                                return null;
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

//DB query version when scaling or performance is critical (e.g., dashboards, analytics). Preferred for SQL Datbases
public List<AggregatedMetricsResponse> queryMetricsFromDb(List<String> sensorIds, List<String> metrics, String statistic, LocalDateTime start, LocalDateTime end) {
    List<AggregatedMetricsResponse> results = new ArrayList<>();

    for (String metric : metrics) {
        String column;
        if (metric.equalsIgnoreCase("temperature")) {
            column = "temperature";
        } else if (metric.equalsIgnoreCase("humidity")) {
            column = "humidity";
        } else {
            continue; // skip unknown metric
        }

        String function;
        switch (statistic.toLowerCase()) {
            case "avg": function = "AVG"; break;
            case "min": function = "MIN"; break;
            case "max": function = "MAX"; break;
            case "sum": function = "SUM"; break;
            default: continue; // skip unknown stat
        }

        StringBuilder sql = new StringBuilder("SELECT " + function + "(" + column + ") FROM weather_metric WHERE timestamp BETWEEN :start AND :end");

        if (sensorIds != null && !sensorIds.isEmpty()) {
            sql.append(" AND sensor_id IN :sensorIds");
        }

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("start", start);
        query.setParameter("end", end);

        if (sensorIds != null && !sensorIds.isEmpty()) {
            query.setParameter("sensorIds", sensorIds);
        }

        Object result = query.getSingleResult();

        if (result != null) {
            Double value = ((Number) result).doubleValue();
            results.add(new AggregatedMetricsResponse(metric, value));
        } else {
            results.add(new AggregatedMetricsResponse(metric, null)); // 👈 or skip this if you prefer
        }
    }

    return results;
}





}

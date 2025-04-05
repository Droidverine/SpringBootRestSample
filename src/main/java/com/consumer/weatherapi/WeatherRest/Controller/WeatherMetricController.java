package com.consumer.weatherapi.WeatherRest.Controller;

import com.consumer.weatherapi.WeatherRest.DTO.AggregatedMetricsResponse;
import com.consumer.weatherapi.WeatherRest.DTO.MetricQueryRequest;
import com.consumer.weatherapi.WeatherRest.Model.WeatherMetric;
import com.consumer.weatherapi.WeatherRest.Repository.WeatherMetricRepository;
import com.consumer.weatherapi.WeatherRest.Service.WeatherMetricQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.validation.Valid;

import java.util.List;


import java.util.Map;
import java.util.stream.Collectors;

//Controller for Rest Endpoints for adding metrics & querying
@RestController
@RequestMapping("v1/metrics")
public class WeatherMetricController {

    private final WeatherMetricRepository repository;
    private final WeatherMetricQueryService queryService;
    private final Validator validator; // ✅ Declare the validator

    // Constructor injection for all dependencies
    public WeatherMetricController(WeatherMetricRepository repository,
                                   WeatherMetricQueryService queryService,
                                   Validator validator) {
        this.repository = repository;
        this.queryService = queryService;
        this.validator = validator;
    }

    @PostMapping
    public ResponseEntity<?> addMetric(@RequestBody WeatherMetric metric) {
        Set<ConstraintViolation<WeatherMetric>> violations = validator.validate(metric);

        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<WeatherMetric> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        WeatherMetric savedMetric = repository.save(metric);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Metric saved successfully.");
        response.put("metricId", savedMetric.getId());
        response.put("sensorId", savedMetric.getSensorId());
        response.put("timestamp", savedMetric.getTimestamp());

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/query")
    public ResponseEntity<?> queryMetrics(@Valid @RequestBody MetricQueryRequest request) {
        if (!List.of("avg", "min", "max", "sum").contains(request.getStatistic())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid statistic: must be one of avg, min, max, sum"));
        }

        for (String metric : request.getMetrics()) {
            if (!List.of("temperature", "humidity").contains(metric)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid metric: " + metric + " (allowed: temperature, humidity)"));
            }
        }

        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                return ResponseEntity.badRequest().body(Map.of("error", "startDate must be before endDate"));
            }
            long days = java.time.Duration.between(request.getStartDate(), request.getEndDate()).toDays();
            if (days < 1 || days > 31) {
                return ResponseEntity.badRequest().body(Map.of("error", "Date range must be between 1 day and 1 month"));
            }
        }

        Map<String, Double> result = queryService.queryMetrics(request);
        List<AggregatedMetricsResponse> formatted = convertToListResponse(result);
        return ResponseEntity.ok(formatted);
    }

    @PostMapping("/query-db")
    public ResponseEntity<?> queryMetricsDb(@RequestBody MetricQueryRequest request) {
        LocalDateTime start = request.getStartDate() != null ? request.getStartDate().toLocalDateTime() : LocalDateTime.now().minusDays(1);
        LocalDateTime end = request.getEndDate() != null ? request.getEndDate().toLocalDateTime() : LocalDateTime.now();
        List<AggregatedMetricsResponse> result = queryService.queryMetricsFromDb(request.getSensorIds(), request.getMetrics(), request.getStatistic(), start, end);
        return ResponseEntity.ok(result);
    }

    private List<AggregatedMetricsResponse> convertToListResponse(Map<String, Double> input) {
        return input.entrySet()
                .stream()
                .map(entry -> new AggregatedMetricsResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }


}

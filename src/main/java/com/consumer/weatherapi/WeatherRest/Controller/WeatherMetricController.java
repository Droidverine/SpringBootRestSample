package com.consumer.weatherapi.WeatherRest.Controller;

import com.consumer.weatherapi.WeatherRest.DTO.MetricQueryRequest;
import com.consumer.weatherapi.WeatherRest.Model.WeatherMetric;
import com.consumer.weatherapi.WeatherRest.Repository.WeatherMetricRepository;
import com.consumer.weatherapi.WeatherRest.Service.WeatherMetricQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jakarta.validation.Valid;
import java.util.List;


import java.util.Map;

@RestController
@RequestMapping("/metrics")
public class WeatherMetricController {

    private final WeatherMetricRepository repository;
    private final WeatherMetricQueryService queryService;
    private final Validator validator; // ✅ Declare the validator

    // ✅ Constructor injection for all dependencies
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

        repository.save(metric);
        return ResponseEntity.ok("✅ Metric saved via REST.");
    }

    @PostMapping("/query")
        public ResponseEntity<?> queryMetrics(@Valid @RequestBody MetricQueryRequest request) {
        // Custom validations
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

        return ResponseEntity.ok(queryService.queryMetrics(request));
    }

}

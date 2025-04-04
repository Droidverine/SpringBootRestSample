package com.consumer.weatherapi.WeatherRest;

import com.consumer.weatherapi.WeatherRest.DTO.MetricQueryRequest;
import com.consumer.weatherapi.WeatherRest.Model.WeatherMetric;
import com.consumer.weatherapi.WeatherRest.Repository.WeatherMetricRepository;
import com.consumer.weatherapi.WeatherRest.Service.WeatherMetricQueryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WeatherMetricQueryServiceTest {

    @Test
    void testQueryMetrics_avgTemperature() {
        WeatherMetricRepository mockRepo = Mockito.mock(WeatherMetricRepository.class);
        WeatherMetricQueryService service = new WeatherMetricQueryService(mockRepo);

        ZonedDateTime now = ZonedDateTime.now();
        WeatherMetric metric1 = new WeatherMetric("sensor1", 20.0, 40.0, now.toLocalDateTime());
        WeatherMetric metric2 = new WeatherMetric("sensor1", 30.0, 50.0, now.toLocalDateTime());

        when(mockRepo.findBySensorIdInAndTimestampBetween(
                anyList(), any(), any())
        ).thenReturn(List.of(metric1, metric2));

        MetricQueryRequest request = new MetricQueryRequest();
        request.setSensorIds(List.of("sensor1"));
        request.setMetrics(List.of("temperature"));
        request.setStatistic("avg");
        request.setStartDate(now.minusDays(1));
        request.setEndDate(now);

        Map<String, Double> result = service.queryMetrics(request);

        assertEquals(25.0, result.get("temperature"));
    }
}

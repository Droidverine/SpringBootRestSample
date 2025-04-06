
# Weather Metrics REST Consumer Service

---

## ✅ Features

-  **POST weather metrics** via REST or Kafka consumer
-  **Query** for min, max, sum, or average of weather metrics (e.g., temperature, humidity)
-  Supports custom date ranges or defaults to the latest 24 hours
-  Enable/Disable Kafka listener at runtime via REST ( You will need producer though : https://github.com/Droidverine/KafkaWeatherDataProducer.git)
-  Input validation and clean JSON error responses

---

## 📦 Requirements

- Java 17
- Maven 3.6+
- Docker & Docker Compose

---

## 🚀 Setup and Run

```
# Clone the repo
git clone https://github.com/Droidverine/SpringBootRestSample.git
cd SpringBootRestSample



# Start MySQL and PhpMyAdmin ( if required you can change mysql & phpmyadmin ports in .env file)
docker-compose -f docker-compose.db.yml --env-file ./env up -d

# Build and run the app  ( if mysql config changed please make sure that you export with correct url 
(i.e. export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3308/weather_db)

java -jar target/WeatherRest-0.0.1-SNAPSHOT.jar

(Default port is 8080 for application, if you want to change please export APP_PORT={yourportnumber} i.e.export APP_PORT=9090)
```

Access PhpMyAdmin: [http://localhost:8081](http://localhost:8081)  
Default credentials:  
- **Server**: `mysql-consumer`  
- **Username**: `root`  
- **Password**: `root`

---

## 📮 Postman Collection

Importable Weather API Postman Collection - https://github.com/Droidverine/SpringBootRestSample/blob/2b8e8bd4a51cb868872efa442745a7c266fe1af0/Weather%20Consumer%20API%20Template.postman_collection.json

---

## 📬 API Endpoints

### POST `/metrics`

Submit weather metrics:

```json
{
  "sensorId": "sensor1",
  "temperature": 25.5,
  "humidity": 60.2,
  "timestamp": "2025-04-04T12:30:00"
}
```

---

### POST `/metrics/query`

Query example – average temperature and humidity for all sensors in the last 24 hours:

```json
{
  "sensorIds": [],
  "metrics": ["temperature", "humidity"],
  "statistic": "avg"
}
```

### POST `/metrics/query_db`

Query example – max temperature and humidity for all sensors in the last 24 hours:

```json
{
  "sensorIds": [],
  "metrics": ["temperature", "humidity"],
  "statistic": "max"
}
```

More examples in the Postman collection.

---

### POST `/kafka/start` and `/kafka/stop`

Start/Stop Kafka consumer listener at runtime.

---

## ⚙️ Kafka & Consumer Setup

Make sure Kafka is running on `localhost:9092` ( Link to producer : https://github.com/Droidverine/KafkaWeatherDataProducer.git)
Topic name: `weather-metrics` 
Group ID: `weather-consumer-group`

---
## Tests

This project includes **unit tests** using **JUnit 5** and **Mockito** to verify the service logic and data processing.

### Run All Tests

Run the following command in the project root (in case of port change some tests might fail):

```
mvn test
```

Or use your IDE (e.g., IntelliJ):
> Right-click on the `test` directory or any test class → Run

### 🔹 Test Coverage

| Test Class                      | Description                                 |
|---------------------------------|---------------------------------------------|
| `WeatherMetricQueryServiceTest` | Verifies logic for querying weather metrics |
| `WeatherMetricControllerTest`   | Verfies Rest endpoints and responses        |


###  Technologies Used

- JUnit 5
- Mockito
- Spring Boot Starter Test

##  Project Structure

- `Controller` - REST endpoints
- `Consumer` - Kafka listener
- `DTO` - Data Transfer Object
- `Model` - JPA Entities
- `Repository` - Data persistence
- `Service` - Business logic

---

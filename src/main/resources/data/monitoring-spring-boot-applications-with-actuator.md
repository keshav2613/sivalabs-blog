Monitoring is a critical aspect of maintaining production applications. Spring Boot Actuator provides built-in capabilities for monitoring and managing your application in production. This article explores how to leverage Spring Boot Actuator to gain insights into your application's health, metrics, and more.

## What is Spring Boot Actuator?

Spring Boot Actuator is a sub-project of Spring Boot that adds several production-ready features to your application. It provides HTTP endpoints or JMX beans to monitor and interact with your application, including:

- Health checks
- Metrics collection
- Environment information
- Thread dump
- Heap dump
- And much more

## Getting Started with Actuator

### Adding Actuator to Your Project

To add Actuator to your Spring Boot application, include the following dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

With this dependency, Spring Boot automatically configures several endpoints. By default, only the `/actuator/health` endpoint is exposed over HTTP.

### Enabling Endpoints

To expose more endpoints, configure them in your `application.yml` or `application.properties`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

## Key Actuator Endpoints

### Health Endpoint

The health endpoint (`/actuator/health`) provides basic information about your application's health:

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963170816,
        "free": 91300069376,
        "threshold": 10485760
      }
    }
  }
}
```

### Info Endpoint

The info endpoint (`/actuator/info`) displays application information:

```yaml
info:
  app:
    name: @project.name@
    description: @project.description@
    version: @project.version@
    java:
      version: ${java.version}
  team:
    contact: dev@example.com
```

### Metrics Endpoint

The metrics endpoint (`/actuator/metrics`) provides metrics information:

```json
{
  "names": [
    "jvm.memory.max",
    "jvm.memory.used",
    "http.server.requests",
    "process.cpu.usage",
    "system.cpu.usage",
    "jdbc.connections.active"
  ]
}
```

To view specific metrics, append the metric name to the URL:

```
/actuator/metrics/http.server.requests
```

### Environment Endpoint

The environment endpoint (`/actuator/env`) exposes configuration properties:

```json
{
  "activeProfiles": [
    "prod"
  ],
  "propertySources": [
    {
      "name": "server.ports",
      "properties": {
        "local.server.port": {
          "value": 8080
        }
      }
    }
  ]
}
```

## Custom Health Indicators

You can create custom health indicators to check specific components of your application:

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private final DataSource dataSource;
    
    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT 1");
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next() && resultSet.getInt(1) == 1) {
                return Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Connected")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Invalid response")
                        .build();
            }
        } catch (SQLException e) {
            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("status", "Connection failed")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
```

## Custom Metrics

You can register and record custom metrics using Micrometer, which is included with Spring Boot Actuator:

```java
@Service
public class OrderService {
    
    private final Counter orderCounter;
    private final Timer orderProcessingTimer;
    
    public OrderService(MeterRegistry meterRegistry) {
        this.orderCounter = meterRegistry.counter("orders.created");
        this.orderProcessingTimer = meterRegistry.timer("orders.processing.time");
    }
    
    public Order createOrder(OrderRequest request) {
        return orderProcessingTimer.record(() -> {
            // Order creation logic
            Order order = new Order();
            // ...
            
            // Increment the counter
            orderCounter.increment();
            
            return order;
        });
    }
}
```

## Integrating with Prometheus and Grafana

Spring Boot Actuator can expose metrics in a format that Prometheus can scrape:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

With this dependency, Actuator exposes a `/actuator/prometheus` endpoint that Prometheus can scrape.

### Prometheus Configuration

Configure Prometheus to scrape your application:

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['localhost:8080']
```

### Grafana Dashboard

Once Prometheus is collecting metrics, you can create Grafana dashboards to visualize them:

1. Add Prometheus as a data source in Grafana
2. Create dashboards with panels for key metrics:
   - JVM memory usage
   - HTTP request rates and latencies
   - Database connection pool stats
   - Custom business metrics

## Securing Actuator Endpoints

Actuator endpoints contain sensitive information, so it's important to secure them:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when_authorized
```

With Spring Security:

```java
@Configuration
public class ActuatorSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requestMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeRequests()
                .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                .anyRequest().hasRole("ACTUATOR")
            .and()
            .httpBasic();
    }
}
```

## Customizing Actuator

### Custom Endpoints

You can create custom Actuator endpoints:

```java
@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {
    
    private final DataSource dataSource;
    
    public DatabaseEndpoint(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @ReadOperation
    public Map<String, Object> connectionInfo() {
        Map<String, Object> info = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            info.put("active", true);
            info.put("url", connection.getMetaData().getURL());
            info.put("driverName", connection.getMetaData().getDriverName());
            info.put("databaseProductName", connection.getMetaData().getDatabaseProductName());
            info.put("databaseProductVersion", connection.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            info.put("active", false);
            info.put("error", e.getMessage());
        }
        
        return info;
    }
    
    @WriteOperation
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT 1");
            ResultSet resultSet = statement.executeQuery();
            
            result.put("success", resultSet.next() && resultSet.getInt(1) == 1);
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}
```

### Info Contributors

You can add custom information to the info endpoint:

```java
@Component
public class BuildInfoContributor implements InfoContributor {
    
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("build", Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "machine", System.getenv("HOSTNAME"),
                "user", System.getProperty("user.name")
        ));
    }
}
```

## Distributed Tracing with Actuator

Spring Boot Actuator works well with distributed tracing systems:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

```yaml
spring:
  sleuth:
    sampler:
      probability: 1.0
  zipkin:
    base-url: http://localhost:9411
```

## Best Practices

1. **Secure Endpoints**: Always secure Actuator endpoints in production.
2. **Monitor Key Metrics**: Focus on metrics that matter for your application.
3. **Set Up Alerts**: Configure alerts for critical thresholds.
4. **Use Custom Health Indicators**: Create health indicators for critical components.
5. **Implement Distributed Tracing**: For microservices architectures.
6. **Regularly Review Metrics**: Look for trends and anomalies.
7. **Document Dashboards**: Ensure team members understand the metrics.
8. **Test Monitoring in Pre-Production**: Verify monitoring works before deploying to production.

## Conclusion

Spring Boot Actuator provides powerful tools for monitoring and managing your applications in production. By leveraging its capabilities, you can gain valuable insights into your application's health, performance, and behavior. Combined with tools like Prometheus and Grafana, you can create comprehensive monitoring solutions that help ensure your application runs smoothly and reliably.
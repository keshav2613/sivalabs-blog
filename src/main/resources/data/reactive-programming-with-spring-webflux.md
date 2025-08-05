Reactive programming is a paradigm that focuses on building non-blocking, asynchronous applications that can handle a large number of concurrent connections with fewer resources. Spring WebFlux is Spring's reactive-stack web framework, built on Project Reactor, that provides an alternative to the traditional Spring MVC approach. This article explores reactive programming concepts and how to implement them using Spring WebFlux.

## Understanding Reactive Programming

Reactive programming is based on the Reactive Manifesto, which defines reactive systems as:

- **Responsive**: Systems respond in a timely manner
- **Resilient**: Systems stay responsive in the face of failure
- **Elastic**: Systems stay responsive under varying workload
- **Message-Driven**: Systems rely on asynchronous message-passing

In Java, reactive programming is implemented through the Reactive Streams specification, which defines a standard for asynchronous stream processing with non-blocking backpressure.

## Spring WebFlux vs Spring MVC

| Feature | Spring MVC | Spring WebFlux |
|---------|------------|----------------|
| Programming Model | Imperative | Reactive |
| Blocking | Yes | No |
| Concurrency Model | Thread per request | Event loop |
| Servlet API | Required | Optional (Servlet 3.1+) |
| Server Support | Servlet containers (Tomcat, Jetty) | Servlet 3.1+ containers, Netty, Undertow |
| Dependencies | spring-boot-starter-web | spring-boot-starter-webflux |

## Getting Started with Spring WebFlux

### Setting Up a WebFlux Project

Add the WebFlux dependency to your Spring Boot project:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### Creating a Reactive REST Controller

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductRepository productRepository;
    
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @GetMapping
    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getProductById(@PathVariable String id) {
        return productRepository.findById(id)
                .map(product -> ResponseEntity.ok(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> updateProduct(@PathVariable String id, @RequestBody Product product) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    return productRepository.save(existingProduct);
                })
                .map(updatedProduct -> ResponseEntity.ok(updatedProduct))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable String id) {
        return productRepository.deleteById(id);
    }
}
```

## Reactive Types: Mono and Flux

Spring WebFlux is built on Project Reactor, which provides two main reactive types:

- **Mono<T>**: Represents a stream of 0 or 1 elements
- **Flux<T>**: Represents a stream of 0 to N elements

### Working with Mono

```java
// Creating a Mono
Mono<String> mono = Mono.just("Hello, Reactive World!");

// Transforming a Mono
Mono<String> upperCaseMono = mono.map(String::toUpperCase);

// Subscribing to a Mono
upperCaseMono.subscribe(
    data -> System.out.println("Received: " + data),
    error -> System.err.println("Error: " + error),
    () -> System.out.println("Completed")
);
```

### Working with Flux

```java
// Creating a Flux
Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5);

// Transforming a Flux
Flux<Integer> doubledFlux = flux.map(n -> n * 2);

// Filtering a Flux
Flux<Integer> evenFlux = doubledFlux.filter(n -> n % 2 == 0);

// Subscribing to a Flux
evenFlux.subscribe(
    data -> System.out.println("Received: " + data),
    error -> System.err.println("Error: " + error),
    () -> System.out.println("Completed")
);
```

## Reactive Data Access

Spring Data provides reactive repositories for MongoDB, Redis, Cassandra, and R2DBC (Reactive Relational Database Connectivity).

### Reactive MongoDB Repository

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```

```java
@Document
public class Product {
    @Id
    private String id;
    private String name;
    private BigDecimal price;
    
    // Getters and setters
}

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
    Flux<Product> findByNameContaining(String name);
    Mono<Product> findByNameAndPrice(String name, BigDecimal price);
}
```

### Reactive R2DBC for SQL Databases

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-postgresql</artifactId>
</dependency>
```

```java
@Configuration
@EnableR2dbcRepositories
public class R2dbcConfig extends AbstractR2dbcConfiguration {
    
    @Value("${spring.r2dbc.url}")
    private String url;
    
    @Value("${spring.r2dbc.username}")
    private String username;
    
    @Value("${spring.r2dbc.password}")
    private String password;
    
    @Override
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(
                ConnectionFactoryOptions.builder()
                        .option(DRIVER, "postgresql")
                        .option(HOST, "localhost")
                        .option(PORT, 5432)
                        .option(USER, username)
                        .option(PASSWORD, password)
                        .option(DATABASE, "productdb")
                        .build());
    }
}

@Table("products")
public class Product {
    @Id
    private Long id;
    private String name;
    private BigDecimal price;
    
    // Getters and setters
}

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByNameContaining(String name);
    Mono<Product> findByNameAndPrice(String name, BigDecimal price);
}
```

## Reactive WebClient

WebClient is a non-blocking, reactive HTTP client that replaces the traditional RestTemplate:

```java
@Service
public class ProductService {
    
    private final WebClient webClient;
    
    public ProductService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.example.com").build();
    }
    
    public Flux<Product> getAllProducts() {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class);
    }
    
    public Mono<Product> getProductById(String id) {
        return webClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class);
    }
    
    public Mono<Product> createProduct(Product product) {
        return webClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class);
    }
}
```

## Functional Endpoints

Spring WebFlux supports a functional programming model for defining endpoints:

```java
@Configuration
public class RouterConfig {
    
    @Bean
    public RouterFunction<ServerResponse> productRoutes(ProductHandler productHandler) {
        return RouterFunctions
                .route(GET("/api/products").and(accept(APPLICATION_JSON)), productHandler::getAllProducts)
                .andRoute(GET("/api/products/{id}").and(accept(APPLICATION_JSON)), productHandler::getProductById)
                .andRoute(POST("/api/products").and(contentType(APPLICATION_JSON)), productHandler::createProduct)
                .andRoute(PUT("/api/products/{id}").and(contentType(APPLICATION_JSON)), productHandler::updateProduct)
                .andRoute(DELETE("/api/products/{id}"), productHandler::deleteProduct);
    }
}

@Component
public class ProductHandler {
    
    private final ProductRepository productRepository;
    
    public ProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productRepository.findAll(), Product.class);
    }
    
    public Mono<ServerResponse> getProductById(ServerRequest request) {
        String id = request.pathVariable("id");
        return productRepository.findById(id)
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    
    public Mono<ServerResponse> createProduct(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);
        
        return productMono.flatMap(product ->
                ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productRepository.save(product), Product.class));
    }
    
    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> productMono = request.bodyToMono(Product.class);
        
        return productRepository.findById(id)
                .flatMap(existingProduct -> productMono.flatMap(product -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    return productRepository.save(existingProduct);
                }))
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    
    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        return productRepository.deleteById(id)
                .then(ServerResponse.noContent().build());
    }
}
```

## Server-Sent Events (SSE)

WebFlux makes it easy to implement Server-Sent Events for real-time updates:

```java
@RestController
@RequestMapping("/api/events")
public class EventController {
    
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductEvent> getProductEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> new ProductEvent(sequence, "Product update at " + LocalDateTime.now()));
    }
}

public class ProductEvent {
    private final Long eventId;
    private final String eventMessage;
    
    // Constructor, getters
}
```

## WebSockets

WebFlux also supports WebSockets for bidirectional communication:

```java
@Configuration
public class WebSocketConfig {
    
    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> handlerMap = new HashMap<>();
        handlerMap.put("/ws/products", new ProductWebSocketHandler());
        
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(handlerMap);
        return handlerMapping;
    }
    
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}

public class ProductWebSocketHandler implements WebSocketHandler {
    
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> output = Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> {
                    try {
                        ProductEvent event = new ProductEvent(sequence, "Product update at " + LocalDateTime.now());
                        return session.textMessage(new ObjectMapper().writeValueAsString(event));
                    } catch (JsonProcessingException e) {
                        return session.textMessage("Error: " + e.getMessage());
                    }
                })
                .onErrorResume(e -> Mono.empty());
        
        return session.send(output);
    }
}
```

## Error Handling

Proper error handling is crucial in reactive applications:

```java
@RestControllerAdvice
public class GlobalErrorHandler {
    
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationErrors(WebExchangeBindException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put
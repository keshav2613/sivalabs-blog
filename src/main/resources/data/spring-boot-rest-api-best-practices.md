Building RESTful APIs with Spring Boot is a common task for Java developers. However, creating a well-designed, maintainable, and secure API requires following certain best practices. This article outlines key principles and patterns to help you build better Spring Boot REST APIs.

## API Design Principles

### Use Proper HTTP Methods

RESTful APIs should use HTTP methods according to their defined purpose:

- **GET**: Retrieve resources without side effects
- **POST**: Create new resources
- **PUT**: Update existing resources (full update)
- **PATCH**: Partially update resources
- **DELETE**: Remove resources

### Consistent URL Structure

Follow these guidelines for URL structure:

- Use nouns, not verbs (e.g., `/users` not `/getUsers`)
- Use plural nouns for collections (e.g., `/users` not `/user`)
- Use nested resources for relationships (e.g., `/users/{id}/orders`)
- Use query parameters for filtering, sorting, and pagination

## Error Handling

Implement a global exception handler using `@ControllerAdvice` to provide consistent error responses:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

## Versioning

Consider API versioning from the start. Common approaches include:

1. **URL Path Versioning**: `/api/v1/users`
2. **Query Parameter Versioning**: `/api/users?version=1`
3. **Header Versioning**: Using custom headers like `X-API-Version: 1`
4. **Media Type Versioning**: Using Accept headers like `Accept: application/vnd.company.v1+json`

## Security Best Practices

### Use HTTPS

Always use HTTPS in production to encrypt data in transit.

### Implement Authentication and Authorization

Spring Security provides robust options for securing your API:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer().jwt();
        
        return http.build();
    }
}
```

### Rate Limiting

Implement rate limiting to prevent abuse:

```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final RateLimiter rateLimiter;
    
    public RateLimitingFilter() {
        // Allow 10 requests per second
        this.rateLimiter = RateLimiter.create(10.0);
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        if (!rateLimiter.tryAcquire()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
```

## Documentation

Use Springdoc OpenAPI (formerly Swagger) to document your API:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>
```

Configure OpenAPI documentation:

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My Spring Boot API")
                        .version("1.0")
                        .description("API Documentation")
                        .contact(new Contact().name("Developer").email("dev@example.com")));
    }
}
```

## Testing

Write comprehensive tests for your API:

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }
}
```

## Conclusion

Following these best practices will help you build robust, maintainable, and secure REST APIs with Spring Boot. Remember that good API design is an iterative process, and it's important to gather feedback from API consumers to continuously improve your API.
GraphQL is a query language for APIs and a runtime for executing those queries with your existing data. It provides a more efficient, powerful, and flexible alternative to REST. Spring Boot, with its robust ecosystem, offers excellent support for building GraphQL APIs. This article explores how to implement GraphQL APIs using Spring Boot.

## Understanding GraphQL

GraphQL was developed by Facebook in 2012 and released as an open-source project in 2015. Unlike REST, which exposes a fixed set of endpoints, GraphQL exposes a single endpoint that can respond to queries requesting specific data. Key features include:

- **Client-Specified Queries**: Clients can request exactly the data they need
- **Single Request**: Multiple resources can be fetched in a single request
- **Strong Typing**: The schema defines the data types and relationships
- **Introspection**: The API can be queried for its own schema
- **Real-time Updates**: Subscriptions allow for real-time data updates

## Setting Up a Spring Boot GraphQL Project

### Adding Dependencies

To create a GraphQL API with Spring Boot, add the following dependencies to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### Configuring GraphQL

Configure GraphQL in your `application.yml`:

```yaml
spring:
  graphql:
    graphiql:
      enabled: true
    schema:
      printer:
        enabled: true
    path: /graphql
```

## Defining the GraphQL Schema

Create a schema file at `src/main/resources/graphql/schema.graphqls`:

```graphql
type Query {
    bookById(id: ID!): Book
    allBooks: [Book!]!
    authorById(id: ID!): Author
    allAuthors: [Author!]!
}

type Mutation {
    createBook(book: BookInput!): Book!
    updateBook(id: ID!, book: BookInput!): Book
    deleteBook(id: ID!): Boolean
    createAuthor(author: AuthorInput!): Author!
}

type Subscription {
    bookAdded: Book!
}

type Book {
    id: ID!
    title: String!
    pageCount: Int
    author: Author!
    genre: Genre
}

type Author {
    id: ID!
    name: String!
    books: [Book!]!
}

enum Genre {
    SCIENCE_FICTION
    FANTASY
    MYSTERY
    THRILLER
    ROMANCE
    NON_FICTION
}

input BookInput {
    title: String!
    pageCount: Int
    authorId: ID!
    genre: Genre
}

input AuthorInput {
    name: String!
}
```

## Creating Domain Models

Create Java classes that correspond to your GraphQL types:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String id;
    private String title;
    private Integer pageCount;
    private String authorId;
    private Genre genre;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    private String id;
    private String name;
}

public enum Genre {
    SCIENCE_FICTION,
    FANTASY,
    MYSTERY,
    THRILLER,
    ROMANCE,
    NON_FICTION
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInput {
    private String title;
    private Integer pageCount;
    private String authorId;
    private Genre genre;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorInput {
    private String name;
}
```

## Implementing Data Access

Create repositories to manage your data:

```java
@Repository
public class BookRepository {
    private final Map<String, Book> books = new HashMap<>();
    
    public BookRepository() {
        // Initialize with sample data
        Book book1 = new Book("1", "Dune", 412, "1", Genre.SCIENCE_FICTION);
        Book book2 = new Book("2", "Foundation", 255, "2", Genre.SCIENCE_FICTION);
        Book book3 = new Book("3", "1984", 328, "3", Genre.SCIENCE_FICTION);
        
        books.put(book1.getId(), book1);
        books.put(book2.getId(), book2);
        books.put(book3.getId(), book3);
    }
    
    public Book findById(String id) {
        return books.get(id);
    }
    
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }
    
    public List<Book> findByAuthorId(String authorId) {
        return books.values().stream()
                .filter(book -> book.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }
    
    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(UUID.randomUUID().toString());
        }
        books.put(book.getId(), book);
        return book;
    }
    
    public boolean deleteById(String id) {
        return books.remove(id) != null;
    }
}

@Repository
public class AuthorRepository {
    private final Map<String, Author> authors = new HashMap<>();
    
    public AuthorRepository() {
        // Initialize with sample data
        Author author1 = new Author("1", "Frank Herbert");
        Author author2 = new Author("2", "Isaac Asimov");
        Author author3 = new Author("3", "George Orwell");
        
        authors.put(author1.getId(), author1);
        authors.put(author2.getId(), author2);
        authors.put(author3.getId(), author3);
    }
    
    public Author findById(String id) {
        return authors.get(id);
    }
    
    public List<Author> findAll() {
        return new ArrayList<>(authors.values());
    }
    
    public Author save(Author author) {
        if (author.getId() == null) {
            author.setId(UUID.randomUUID().toString());
        }
        authors.put(author.getId(), author);
        return author;
    }
}
```

## Creating GraphQL Controllers

Implement controllers to handle GraphQL queries, mutations, and subscriptions:

```java
@Controller
public class BookController {
    
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final Sinks.Many<Book> bookSink;
    
    public BookController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookSink = Sinks.many().multicast().onBackpressureBuffer();
    }
    
    @QueryMapping
    public Book bookById(@Argument String id) {
        return bookRepository.findById(id);
    }
    
    @QueryMapping
    public List<Book> allBooks() {
        return bookRepository.findAll();
    }
    
    @SchemaMapping(typeName = "Book", field = "author")
    public Author getAuthor(Book book) {
        return authorRepository.findById(book.getAuthorId());
    }
    
    @MutationMapping
    public Book createBook(@Argument BookInput book) {
        Book newBook = new Book(
                null,
                book.getTitle(),
                book.getPageCount(),
                book.getAuthorId(),
                book.getGenre()
        );
        Book savedBook = bookRepository.save(newBook);
        bookSink.tryEmitNext(savedBook);
        return savedBook;
    }
    
    @MutationMapping
    public Book updateBook(@Argument String id, @Argument BookInput book) {
        Book existingBook = bookRepository.findById(id);
        if (existingBook == null) {
            return null;
        }
        
        existingBook.setTitle(book.getTitle());
        existingBook.setPageCount(book.getPageCount());
        existingBook.setAuthorId(book.getAuthorId());
        existingBook.setGenre(book.getGenre());
        
        return bookRepository.save(existingBook);
    }
    
    @MutationMapping
    public boolean deleteBook(@Argument String id) {
        return bookRepository.deleteById(id);
    }
    
    @SubscriptionMapping
    public Flux<Book> bookAdded() {
        return bookSink.asFlux();
    }
}

@Controller
public class AuthorController {
    
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    
    public AuthorController(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }
    
    @QueryMapping
    public Author authorById(@Argument String id) {
        return authorRepository.findById(id);
    }
    
    @QueryMapping
    public List<Author> allAuthors() {
        return authorRepository.findAll();
    }
    
    @SchemaMapping(typeName = "Author", field = "books")
    public List<Book> getBooks(Author author) {
        return bookRepository.findByAuthorId(author.getId());
    }
    
    @MutationMapping
    public Author createAuthor(@Argument AuthorInput author) {
        Author newAuthor = new Author(null, author.getName());
        return authorRepository.save(newAuthor);
    }
}
```

## Error Handling

Implement proper error handling for your GraphQL API:

```java
@Component
public class GraphQLExceptionHandler implements DataFetcherExceptionResolver {
    
    private final Logger logger = LoggerFactory.getLogger(GraphQLExceptionHandler.class);
    
    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
        logger.error("Error executing GraphQL query", exception);
        
        if (exception instanceof ResourceNotFoundException) {
            return Mono.just(Collections.singletonList(
                    GraphQLError.newError()
                            .message(exception.getMessage())
                            .location(environment.getField().getSourceLocation())
                            .path(environment.getExecutionStepInfo().getPath())
                            .extensions(Collections.singletonMap("classification", "NOT_FOUND"))
                            .build()
            ));
        }
        
        if (exception instanceof ValidationException) {
            return Mono.just(Collections.singletonList(
                    GraphQLError.newError()
                            .message(exception.getMessage())
                            .location(environment.getField().getSourceLocation())
                            .path(environment.getExecutionStepInfo().getPath())
                            .extensions(Collections.singletonMap("classification", "VALIDATION_ERROR"))
                            .build()
            ));
        }
        
        return Mono.just(Collections.singletonList(
                GraphQLError.newError()
                        .message("Internal server error")
                        .location(environment.getField().getSourceLocation())
                        .path(environment.getExecutionStepInfo().getPath())
                        .extensions(Collections.singletonMap("classification", "INTERNAL_ERROR"))
                        .build()
        ));
    }
}

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
```

## Testing GraphQL APIs

Write tests for your GraphQL API:

```java
@SpringBootTest
@AutoConfigureGraphQlTester
class BookControllerTest {
    
    @Autowired
    private GraphQlTester graphQlTester;
    
    @Test
    void shouldGetBookById() {
        // language=GraphQL
        String query = """
            query {
                bookById(id: "1") {
                    id
                    title
                    pageCount
                    author {
                        id
                        name
                    }
                    genre
                }
            }
        """;
        
        graphQlTester.document(query)
                .execute()
                .path("bookById")
                .entity(Book.class)
                .satisfies(book -> {
                    assertThat(book.getId()).isEqualTo("1");
                    assertThat(book.getTitle()).isEqualTo("Dune");
                    assertThat(book.getPageCount()).isEqualTo(412);
                });
    }
    
    @Test
    void shouldCreateBook() {
        // language=GraphQL
        String mutation = """
            mutation {
                createBook(book: {
                    title: "New Book"
                    pageCount: 250
                    authorId: "1"
                    genre: FANTASY
                }) {
                    id
                    title
                    pageCount
                    genre
                }
            }
        """;
        
        graphQlTester.document(mutation)
                .execute()
                .path("createBook")
                .entity(Book.class)
                .satisfies(book -> {
                    assertThat(book.getId()).isNotNull();
                    assertThat(book.getTitle()).isEqualTo("New Book");
                    assertThat(book.getPageCount()).isEqualTo(250);
                    assertThat(book.getGenre()).isEqualTo(Genre.FANTASY);
                });
    }
}
```

## Security for GraphQL APIs

Secure your GraphQL API with Spring Security:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttp
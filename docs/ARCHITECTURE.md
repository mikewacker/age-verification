# Architecture

**Audience:** Engineers who build web applications, but may not build them in Java.

**Controlling Idea:** The product exhibits craftsmanship because of the architectural choices that were made.

- Focus on getting things done. Frameworks can help, but they can also become a distraction.
- Quality matters. That includes unit tests.

**I just want to jump into the code.**

See [`SiteService`](/service/src/main/java/org/example/age/service/SiteService.java)/[`AvsService`](/service/src/main/java/org/example/age/service/AvsService.java), [`SiteApp`](/app/src/main/java/org/example/age/app/SiteApp.java)/[`AvsApp`](/app/src/main/java/org/example/age/app/AvsApp.java), or [`Demo`](/demo/src/main/java/org/example/age/demo/Demo.java).

## Tech Stack

**Web APIs**

- **[OpenAPI](https://www.openapis.org/):** Defines web APIs using a language-agnostic framework. A natural starting point given our audience.

**Java Web APIs and Web Clients**

Go POJO: Plain Old Java Object. Getting things done is easier when you work with POJO interfaces.

- **[JAX-RS](https://jakarta.ee/specifications/restful-ws/4.0/):** Creates web APIs as POJO interfaces with a few annotations. ([Generated](/buildSrc/src/main/kotlin/openapi-java.gradle.kts) from the OpenAPI YAML.)
- **[Retrofit](https://square.github.io/retrofit/):** Creates web clients as POJO interfaces with a few annotations. (Generated from the OpenAPI YAML.)
- **[Immutables](https://immutables.github.io/) + [Jackson](https://github.com/FasterXML/jackson):** Creates value types for JSON data as POJO interfaces with a few annotations.

**Java Web Applications**

- **[Dropwizard](https://www.dropwizard.io/):** Web application framework that focuses on getting things done&mdash;and has a shallow learning curve.
- **[Dagger](https://dagger.dev/):** Dependency injection framework. Dependencies are injected at compile-time; mistakes are compilation errors.

**Stores**

- **[DynamoDB](https://aws.amazon.com/dynamodb/)**
- **[Redis](https://redis.io/)**

## Detailed Design

### Project Structure

Gradle is the build system. The code is split into multiple Gradle modules (e.g., `:api`, `:service`).

**Interfaces**

- The OpenAPI YAML file, as well as the corresponding JAX-RS and Retrofit interfaces, can be found in `:api`.
- Components such as stores are abstracted away as interfaces; these interfaces are defined in `:service:module`.
    - The idea is similar to a DAO, but it is expanded to also cover, e.g., calls to another service.

**Implementations**

- Implementations of the JAX-RS interfaces can be found in `:service`.
- Implementations of the interfaces in `:service:module` can be found in `:module:*` (e.g., `:module:store-redis`).
    - Many modules come with configuration, which is defined via Immutables + Jackson.
    - Many modules depend on [`LiteEnv`](/module/common/src/main/java/org/example/age/module/common/LiteEnv.java), a lightweight facade for the Dropwizard `Environment`.
- Since this is a proof-of-concept, some modules in `:module:*` have "demo" implementations.

**Web Applications**

- The web applications can be found in `:app`.
- A Dropwizard app only needs to implement one method: `void run(T configuration, Environment environment)`.
    - The configuration class (`T extends Configuration`) is a [container](/app/src/main/java/org/example/age/app/config/SiteAppConfig.java) for all the module-specific configuration.
    - An [implementation](/module/common/src/main/java/org/example/age/module/common/DropwizardLiteEnv.java) of the `LiteEnv` facade is provided using the Dropwizard `Environment`.
- Since a proof-of-concept is not deployed to production, ops features are excluded: health checks, metrics, logging, etc.

### Testability

Most of the test coverage comes from unit tests.

**Unit Testing `:service`**

- Very lightweight fakes for the interfaces in `:service:module` can be found in `:module:test`.
    - These fakes can be built quickly and are self-contained. 
    - In turn, they may not implement expiration logic, use preset accounts, hard-code configuration, etc.
- Services in `:service` can be unit tested in a hermetic, ephemeral environment by using `:module:test`.
- ([Test](/app/src/testFixtures/java/org/example/age/app/TestSiteApp.java) [applications](/app/src/testFixtures/java/org/example/age/app/TestAvsApp.java) can also be quickly stood up and [tested end-to-end](/app/src/test/java/org/example/age/app/TestAppVerificationTest.java) using `:module:test`.)

**Unit Testing `:module:*`**

- The interfaces in `:service:module` come with test templates. ([example](/service/module/src/testFixtures/java/org/example/age/service/module/store/testing/AvsAccountStoreTestTemplate.java))
- A self-contained [test implementation](/module/common/src/testFixtures/java/org/example/age/module/common/testing/TestLiteEnvModule.java) of `LiteEnv` is provided by `:module:common`.
- Docker runs stores such as Redis and DynamoDB in containers; unit tests for stores depend on these containers.
    - The modules for stores come with test fixtures that clean the container and set up tests. ([example](/module/store-dynamodb/src/testFixtures/java/org/example/age/module/store/dynamodb/testing/DynamoDbTestContainer.java))

## Architectural Complications

### Custom OpenAPI Java Plugin

**Convention Plugin**

**Problem:** The [convention plugin](/buildSrc/src/main/kotlin/org.example.age.java-conventions.gradle.kts) (Spotless, ErrorProne, `-Werror`, etc.) is not compatible with generated code.

- `:api` contains types that are generated from an OpenAPI YAML file; this code does not always follow the conventions.
- `:api` also contains a few hand-coded types (e.g., `SecureId`); conventions should apply to this code.
- Putting generated and hand-coded types in separate Gradle modules would create a circular dependency between modules.
 
**Solution:** Apply conventions to `:api` on a best-effort basis.

- Spotless and ErrorProne have options to exclude files by path; this can be used to exclude generated code.
- `-Werror` is still enforced, but a few `-Xlint` warnings are disabled.

**Duplicate Types**

**Problem:** The custom OpenAPI Java plugin generates two versions of the code for both models and APIs.

- The OpenAPI generator is called twice: once for the server (JAX-RS), and once for the client (Retrofit).

**Solution:** Deduplicate models, but not the APIs.

- Separate client and server APIs make sense, but add [`AsyncCalls`](/common/src/main/java/org/example/age/common/AsyncCalls.java) as an adapter between them. See: square/retrofit#573
- The models should be deduplicated, however. The generated model types for JAX-RS should be reused for Retrofit.
- **Downside**: In `build.gradle.kts`, the `openApiJava` block must manually list out all the schemas (`dedupSchemas`).

### Dagger

**Request Context**

**Problem:** How do I access the HTTP headers for a request? We cannot add a `@Context HttpHeaders` arg to...

- the `@Inject`'ed constructor. The Dagger component produces singleton-scoped services that are registered with Jersey.
- the methods of the JAX-RS interface. This interface is generated from the OpenAPI YAML file.

**Solution:** Use HK2 to inject a `Provider<HttpHeaders>` via a `ContainerLifecycleListener`. See: [`DropwizardRequestContext`](/module/common/src/main/java/org/example/age/module/common/DropwizardRequestContext.java)

- **Downside:** This provider only works if you call `get()` in the thread that handles the HTTP request.

### Testability

**Unit Testing a Service**

**Problem:** What is the difference between throwing `NotFoundException` and returning it in a failed `CompletionStage`?

- End-to-end, both would result would in a 404 error.
- When unit testing a service class, those are two different outcomes.

**Solution:** For unit tests, use a decorator to convert uncaught exceptions to a failed `CompletionStage`. See: [`TestSiteService`](/service/src/test/java/org/example/age/service/testing/TestSiteService.java)

- **Downside:** You will have to write a small amount of boilerplate code.

**Cleaning Docker Containers**

**Problem:** Using Docker containers for stores such as Redis and DynamoDB can lead to cross-test pollution.

- The containers are started before tests are run and stay up.

**Solution:** Create a JUnit Jupiter extension that cleans a container before all and after all tests in a test class.

- Cross-test pollution is manageable within a single test class, so it cleans after all tests instead of after each test.
- It also cleans before all tests in case another test class didn't properly clean the container.
- **Downside:** You have to implement cleaning logic for each store, though it's not a lot of work in practice.

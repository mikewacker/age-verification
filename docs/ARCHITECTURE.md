# Architecture

**Audience:** Developers who are familiar with web applications, but whose primary language may not be Java.

## Overview

### I just want to jump into the code. Where do I start?

- The core code for the web services can be found in [`SiteService`](/service/src/main/java/org/example/age/service/SiteService.java)/[`AvsService`](/service/src/main/java/org/example/age/service/AvsService.java).
- If you want to understand how the web applications are built end-to-end, start with [`SiteApp`](/app/src/main/java/org/example/age/app/SiteApp.java)/[`AvsApp`](/app/src/main/java/org/example/age/app/AvsApp.java).
- The code that runs the demo can be found in [`Demo`](/app/src/main/java/org/example/age/demo/Demo.java).

### Tech Stack

**Goal:** Implementing/using a web service is as easy as implementing/using a POJO (plain old Java object) interface.

Here are the key components of the tech stack:

- **[OpenAPI](https://www.openapis.org/):** Defines web APIs using a language-agnostic framework.
    - JAX-RS and Retrofit code is generated from the OpenAPI YAML file.
- **[Redis](https://redis.io/):** Key-value database.
- **[Dropwizard](https://www.dropwizard.io/):** Web application framework. (Uses Jetty and Jersey behind the scenes.)
- **[JAX-RS](https://jakarta.ee/specifications/restful-ws/4.0/):** Turns POJO interfaces into web APIs with a few annotations.
- **[Retrofit](https://square.github.io/retrofit/):** Turns POJO interfaces into web clients with a few annotations. Similar but not identical to JAX-RS.

These libraries are also used:

- **[Jackson](https://github.com/FasterXML/jackson):** JSON library.
- **[Dagger](https://dagger.dev/):** Dependency injection framework. Dependency injection is done at compile-time; mistakes are compilation errors.
- **[Immutables](https://immutables.github.io/):** Create a value type as an interface, and let Immutables generate the implementation. Used for configuration.

This tech stack is oriented towards GTD: Getting Things Done.

### Production-Quality Proof of Concept

Production-quality proof of concept is an oxymoron. Here's what it means in practice:

- Would you write tests for production code? Yes. Will you write similar tests for this code? Yes.
- Web services are mostly written as if they are production services.
- Web services have a modular design; components such as stores are abstracted away as interfaces (e.g., DAOs).
- Modules only need to provide "demo" implementations of these interfaces. (But, even "demo" modules still have tests.)
- Since a proof-of-concept is not deployed to production, ops features are excluded: health checks, metrics, logging, etc.

## Detailed Design

### Project Structure

Gradle is the build system. The code is split into multiple Gradle modules (e.g., `:api`, `:service`):

- The OpenAPI YAML file can be found in `:api`.
    - A custom Gradle plugin ([`openapi-java.gradle.kts`](/buildSrc/src/main/kotlin/openapi-java.gradle.kts)) generates JAX-RS and Retrofit code from this YAML file.
    - If the YAML file changes, running `./gradlew :api:build` will update the generated code and compile said code.
- The implementation of the JAX-RS interfaces can be found in `:service`.
- Components such as stores are abstracted away as interfaces (e.g., DAOs); these interfaces can be found in `:service:module`.
    - To unit-test a service, these interfaces are implemented with very lightweight, in-memory fakes.
- Implementations of these interfaces can be found in `:module:*` (e.g., `module:store-redis`).
    - Some modules depend on configuration.
    - Some modules depend on a thread pool and/or a JSON `ObjectMapper`.
    - *Keys are stored in configuration; obviously, a production app would not do that.*
- The app can be found in `:app`.
    - A Dropwizard app only needs to implement one method: `void run(T configuration, Environment environment)`.
    - The configuration class (`T extends Configuration`) is a container for all the module-specific configuration.
    - The `Environment` can be used to provide thread pools and the `ObjectMapper`.

### Testability and Test Infrastructure

In evaluating the tech stack, testability if a key consideration.

- How easy is it to test the code, including unit tests?
    - Most of the test coverage comes from unit tests; there's only one integration test and one end-to-end test.
    - A few unit tests do need to create a very small test service and stand up a test app to run this service.
- How much test infrastructure needs to be built?

This tech stack scores very well here:

- Dropwizard has a great, easy-to-use testing library; it's used both for end-to-end testing and for testing the configuration.
- An embedded Redis server is available for testing (though that project isn't owned by Redis).
- Retrofit comes with a test library that can easily create fake `Call` objects.

A `:testing` module exists, but it's fairly lightweight (e.g., [`CompletionStageTesting`](/testing/src/main/java/org/example/age/testing/CompletionStageTesting.java)).

### Alternatives Considered

**Undertow**

*Note: An earlier version of this project used this approach.*

Undertow is highly performant web server.

Advantages

- This is obviously a very flexible approach, and it's very easy to mix-and-match libraries.
- Undertow is fairly easy to learn, despite the lack of documentation.
    - The dead-simple ["Hello, world!" example](https://undertow.io/) on their homepage was a major selling point.
- You will not get blocked by learning curves, frameworks behaving oddly, frameworks not supporting something you want, etc.
    - The benefits of this should not be underestimated.
- Working at a lower layer definitely has educational benefits, even if that's not what you would do for a production app.
    - That experience did influence some design decision for the current architecture.

Disadvantages

- You will essentially end up [building your own framework](https://github.com/mikewacker/drift) on top of Undertow.
    - Examples: mapping an HTTP request to a call to your POJO interface, configuration for the app, etc.
    - The cost of this should not be underestimated, even for a minimal framework that's designed for prototyping.

**Spring Boot**

Spring Boot is the most popular web application framework for Java.

- **Audience** is a key consideration.
    - If the audience was Java developers, Spring Boot would be a sensible choice.
    - Since the audience's primary language may not be Java, the steep learning curve of Spring Boot becomes a major issue.
- Spring Boot tends to lock you within the Spring ecosystem; the easiest path is to build things "the Spring way."
    - If you're fine with using JAX-RS/Jersey and Jetty, it's easier to mix-and-match libraries with Dropwizard.
    - Representative Example: In theory, Spring supports Jersey. In practice, it's an unpopular option with minimal support.
- Dropwizard, by contrast, stays out of your way until you need it.
    - Defining the web API and building the web service (`api`, `service`, and `module`) requires no knowledge of Dropwizard.
    - The code to build a Dropwizard web application on top of the web service (`app`) is easy to understand.

### Appendix: Design Notes

**Conflicting Plugins: OpenAPI and Conventions**

Problem

- The `:api` module contains code that is generated from an OpenAPI YAML file via a [custom plugin](/buildSrc/src/main/kotlin/openapi-java.gradle.kts).
  - The custom plugin in turn uses the [OpenAPI generator plugin](https://github.com/OpenAPITools/openapi-generator).
- The `:api` module will also use a few hand-coded types (e.g., [`SecureId`](/api/custom/src/main/java/org/example/age/api/crypto/SecureId.java)).
  - The [convention plugin](/buildSrc/src/main/kotlin/org.example.age.java-conventions.gradle.kts) (ErrorProne, Spotless, warnings as errors, etc.) should apply to this code.
  - But, the convention plugin cannot be applied to code that is generated by an external tool.
- Dependencies between hand-coded types and generated types run in both directions.
   - If a hand-coded type is used in `schemaMappings`, the generated code will depend on this hand-coded type.
   - Some hand-coded types will depend on generated types.

Solution

- Put both generated types and hand-coded types in the `:api` module.
- Make a best effort to apply conventions to the `:api` module.
   - Spotless and ErrorProne have options to exclude files by path.
   - `-Werror` is still enforced, but a few `-Xlint` warnings are disabled.

**OpenAPI Java Plugin: To Duplicate or Not To Duplicate?**

Problem

- The custom OpenAPI Java plugin generates two versions of the code for both models and APIs.
  - The generator is called twice: once for the server (JAX-RS), and once for client (Retrofit).

Solution

- Separate client and server APIs make sense, but add [`AsyncCalls`](/api/custom/src/main/java/org/example/age/api/util/AsyncCalls.java) as an adapter between them.
  - For a brief explanation of why Retrofit doesn't reuse the JAX-RS annotations, see square/retrofit#573.
  - The key difference is that the JAX-RS interface returns a `CompletionStage<V>`, and the Retrofit one returns a `Call<V>`.
- The models should be deduplicated, however.
  - The OpenAPI generator has a `schemaMappings` arg that can use an existing model type instead of generating one.
  - The plugin can pass in all the JAX-RS model types as `schemaMappings` when generating the Retrofit code.
  - **Downside**: In `build.gradle.kts`, the `openApiJava` block must manually list out all the schemas (`dedupSchemas`).

**Request Context: OpenAPI, Dropwizard, and Dagger**

Problem

- How can we access the HTTP headers for a request?
    - We cannot add a `@Context HttpHeaders` arg to the JAX-RS interface; it's generated from the OpenAPI YAML file.
    - We cannot inject `@Context HttpHeaders` into the service class.
        - To inject `@Context` into classes, you would need to call `register(Class<?>)` (not `register(Object)`).
        - If you call `register(Class<?>)`, it will inject instances of that class using HK2, not Dagger.
        - With Dagger, you can only create a singleton via a Dagger component, and then register it via `register(Object)`.

Solution

- Create an implementation of [`ContainerRequestFilter`](https://jakarta.ee/specifications/restful-ws/4.0/apidocs/jakarta.ws.rs/jakarta/ws/rs/container/containerrequestfilter).
    - This implementation can store the `ContainerRequestContext` in a `ThreadLocal` variable. See: [`RequestContextFilter`](/service/module/src/main/java/org/example/age/service/module/request/RequestContextFilter.java)
    - In theory, you can annotate this class with `@Provider`, but it's easier to just register it with Jersey.
    - **Downside:** If request handling moves to a different thread, `RequestContextFilter` will no longer work.
        - E.g., if the service makes an asynchronous call to another service, the callback will run in a different thread. 

**Unit-Testing a Service Class**

Problem

- What is the difference between 1) throwing `NotFoundException`, and 2) returning a `CompletionStage` with said exception?
    - In an end-to-end test, there's no difference; both result in a 404 error.
    - If you directly call a method of the service class in a unit test, those are obviously two very different results.

Solution

- Create a wrapper implementation of the JAX-RS interface that converts uncaught exceptions to a failed `CompletionStage`.
    - It is better to create a test wrapper than it is to alter the code for the service class. 
    - **Downside:** You have to write boilerplate code to create a wrapper for each JAX-RS interface.

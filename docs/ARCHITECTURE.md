# Architecture

**Audience:** Developers who are familiar with web applications, but whose primary language may not be Java.

## Overview

### I just want to jump into the code. Where do I start?

- The core code for the web services can be found in [`SiteService`](/service/src/main/java/org/example/age/service/SiteService.java)/[`AvsService`](/service/src/main/java/org/example/age/service/AvsService.java).
- If you want to understand how the web applications are built end-to-end, start with [`SiteApp`](/app/src/main/java/org/example/age/app/SiteApp.java)/[`AvsApp`](/app/src/main/java/org/example/age/app/AvsApp.java).

### Tech Stack

**Goal:** Implementing/using a web service is as easy as implementing/using a POJO (plain old Java object) interface.

- **[OpenAPI](https://www.openapis.org/):** Language-agnostic framework used to define the web APIs.
    - JAX-RS and Retrofit code is generated from the OpenAPI YAML file.
- **[JAX-RS](https://jakarta.ee/specifications/restful-ws/4.0/):** Turns POJO interfaces into web APIs with a few annotations.
- **[Retrofit](https://square.github.io/retrofit/):** Turns POJO interfaces into web clients with a few annotations. Similar but not identical to JAX-RS.
- **[Jackson](https://github.com/FasterXML/jackson):** JSON library.
- **[Dropwizard](https://www.dropwizard.io/):** Web application framework. (Uses Jetty and Jersey behind the scenes.)
- **[Dagger](https://dagger.dev/):** Dependency injection framework. Dependency injection is done at compile-time; mistakes are compilation errors.
- **[Immutables](https://immutables.github.io/):** Create a value type as an interface, and let Immutables generate the implementation. Used for configuration.

This tech stack is oriented towards GTD: Getting Things Done.

### Production-Quality Proof of Concept

Production-quality proof of concept is an oxymoron. Here's what it means in practice:

- Would you write tests for production code? Yes. Will you write similar tests for this code? Yes.
- Web services are mostly written as if they are production services.
- Web services have a modular design; components such as stores are abstracted away as interfaces.
- Modules only need to provide "demo" implementations of these interfaces. (But, even "demo" modules still have tests.)
- Since a proof-of-concept is not deployed to production, ops features are excluded: health checks, metrics, logging, etc.

## Detailed Design

### Project Structure

Gradle is the build system. The code is split into multiple Gradle modules (e.g., `:api`, `:service`):

- The OpenAPI YAML file can be found in `:api`.
    - A custom Gradle plugin ([`openapi-java.gradle.kts`](/buildSrc/src/main/kotlin/openapi-java.gradle.kts)) generates JAX-RS and Retrofit code from this YAML file.
    - (A few hand-coded types can be found in `:api:custom`.)
- The implementation of the JAX-RS interfaces can be found in `:service`.
- Components such as stores are abstracted away as interfaces; these interfaces can be found in `:service:module`.
    - To unit-test a service, these interfaces are implemented with very lightweight, in-memory fakes.
- Implementations of these interfaces can be found in `:module:*` (e.g., `module:store-demo`).
    - Since this is a proof of concept, URLs, keys, prepopulated users, etc. are all provided via configuration.
        - *Obviously, a production app would not, e.g., store keys in configuration.*
    - Some modules may depend on a thread pool and/or a JSON `ObjectMapper`.
- The app can be found in `:app`.
    - A Dropwizard app only needs to implement one method: `void run(T configuration, Environment environment)`.
    - The configuration class (`T extends Configuration`) is a container for all the module-specific configuration.
    - The `Environment` can be used to provide thread pools and the `ObjectMapper`.

### Alternatives Considered

**Undertow**

*Note: An earlier version of this project used this approach.*

Undertow is highly performant web server.

Advantages

- This is obviously a very flexible approach, and it's very easy to mix-and-match libraries.
- Undertow is fairly easy to learn, despite the lack of documentation.
    - The ["Hello, world!" example](https://undertow.io/) on their homepage is dead simple.
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

TODO

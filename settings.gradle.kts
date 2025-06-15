rootProject.name = "age-verification"

include(
        "common",

        // interfaces
        "api",
        "service:module",

        // implementations
        "service",
        "module:common",
        "module:request-demo",
        "module:client",
        "module:store-redis",
        "module:store-dynamodb",
        "module:crypto-demo",
        "module:test",

        // app
        "app",
        "demo",
)

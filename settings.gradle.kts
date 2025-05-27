rootProject.name = "age-verification"

include(
        // interfaces
        "api",
        "service:module",

        // implementations
        "service",
        "module:request-demo",
        "module:client",
        "module:store-redis",
        "module:store-dynamodb",
        "module:crypto-demo",

        // app
        "app",
        "demo",

        // test
        "testing",
        "test-containers",
)

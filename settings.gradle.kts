rootProject.name = "age-verification"

include(
        "common:util",

    "common:api",
    "site:api",
    "avs:api",

    "common:spi",
    "common:spi-testing",
    "service:module",

    "service",

    "common:env",

    "common:provider:request-demo",

    "module:common",
    "module:client",
    "module:store-redis",
    "module:store-dynamodb",
    "module:crypto-demo",
    "module:test",

    "common:app",
    "app",

    "testing",

    "demo",
)

rootProject.name = "age-verification"

include(
    ":common:annotation",

    "common:api",
    "site:api",
    "avs:api",

    "common:spi",
    "site:spi",
    "avs:spi",
    "common:spi-testing",
    "service:module",

    "service",

    "common:env",

    "common:provider:request-demo",
    "common:provider:redis",
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

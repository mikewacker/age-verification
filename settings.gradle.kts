rootProject.name = "age-verification"

include(
        "common:util",

    "common:api",
    "site:api",
    "avs:api",

    "service:module",

    "common:env",

    "service",

    "module:common",
    "module:request-demo",
    "module:client",
    "module:store-redis",
    "module:store-dynamodb",
    "module:crypto-demo",
    "module:test",

    "app",

    "testing",

    "demo",
)

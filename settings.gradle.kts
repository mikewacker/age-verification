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

    "common:client:api",
    "common:client:dynamodb",
    "common:client:redis",
    "site:client:avs",
    "avs:client:site",

    "common:provider:account-demo",
    "module:common",
    "module:store-redis",
    "module:store-dynamodb",
    "module:crypto-demo",
    "module:test",

    "common:app",
    "site:app",
    "avs:app",
    "integrationTest:app",

    "testing",

    "demo",
)

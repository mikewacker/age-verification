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
    "site:spi-testing",
    "avs:spi-testing",
    "service:module",

    "service",

    "common:env",

    "common:client:api",
    "common:client:dynamodb",
    "common:client:redis",
    "site:client:avs",
    "avs:client:site",

    "common:provider:account-demo",
    "common:provider:pendingstore-redis",
    "common:provider:signingkey-demo",
    "site:provider:accountstore-dynamodb",
    "site:provider:accountstore-redis",
    "site:provider:userlocalizer-demo",
    "avs:provider:accountstore-dynamodb",
    "avs:provider:accountstore-redis",
    "avs:provider:certificatesigner-demo",
    "avs:provider:userlocalizer-demo",
    "module:common",
    "module:store-dynamodb",
    "module:crypto-demo",
    "module:test",

    "common:app",
    "site:app",
    "avs:app",
    "integration-test:app",

    "testing",

    "demo",
)

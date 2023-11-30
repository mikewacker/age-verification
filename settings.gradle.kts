rootProject.name = "age-verification"

include(
        /* generic modules */

        // api types (does not depend on Undertow)
        "api",

        // infrastructure (depends on Undertow)
        "infra-api",
        "infra-service",
        "infra-server",
        "infra-client",
        "testing-api",
        "testing-server",

        /* product modules */

        // data types
        "data",

        // api
        "common-api",
        "avs-api",
        "site-api",

        // service
        "common-data",
        "common-crypto",
        "common-store",
        "site-service",
        "common",
        "verification-poc",

        // demo
        "demo",
)

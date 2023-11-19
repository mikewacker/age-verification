rootProject.name = "age-verification"

include(
        // data types
        "data",

        // api types
        "api",

        // api
        "infra-api",
        "common-api",
        "avs-api",
        "site-api",

        // service
        "infra-service",
        "common-service",
        "site-service",
        "common",
        "verification-poc",

        // server
        "common-server",
        "testing-server",

        // demo
        "demo",
)

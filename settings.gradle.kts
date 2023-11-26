rootProject.name = "age-verification"

include(
        // data types
        "data",

        // generic api types
        "api",

        // generic infrastructure
        "infra-api",
        "infra-service",
        "testing-api",
        "testing-server", // mostly generic; uses DataMapper from :data to get an ObjectMapper

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

        // server
        "common-server",

        // demo
        "demo",
)

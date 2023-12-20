rootProject.name = "age-verification"

include(
        /* generic modules */

        // base + crypto (only depends on Jackson, Immutables; ":base:api" depends on nothing)
        "base:api:base",
        "base:api:adapter",
        "base:data:json",
        "base:data:crypto",

        "crypto:data",

        // infra + testing (depends on Undertow, OkHttp)
        "infra:api",
        "infra:service",
        "infra:client",

        "testing",

        /* product modules */
        "core:data",
        "core:api:types",
        "core:api:extractors",
        "core:api:endpoint",
        "core:service:types",
        "core:service:crypto",
        "core:service:endpoint",

        "module:extractor:builtin",
        "module:extractor:demo",
        "module:extractor:test",
        "module:store:inmemory",
        "module:service:test",

        // other
        "core:verification-poc",

        "demo",
)

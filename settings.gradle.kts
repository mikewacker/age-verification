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
        "core:api:types:common",
        "core:api:types:site",
        "core:api:types:avs",
        "core:api:extractors:common",
        "core:api:endpoint:site",
        "core:api:endpoint:avs",
        "core:service:types:common",
        "core:service:types:site",
        "core:service:types:avs",
        "core:service:crypto:common",
        "core:service:endpoint:site",
        "core:service:endpoint:avs",
        "core:integration-test",

        "module:extractor:builtin:common",
        "module:extractor:test:common",
        "module:store:inmemory:common",
        "module:service:test:common",
        "module:service:test:site",
        "module:service:test:avs",

        // other
        "core:verification-poc",

        "demo",
)

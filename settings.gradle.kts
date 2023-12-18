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

        "module:extractor:common:builtin",
        "module:extractor:common:test",
        "module:store:common:inmemory",
        "module:store:avs:test",
        "module:key:common:test",
        "module:config:site:test",
        "module:config:avs:test",
        "module:location:common:test",

        // other
        "core:verification-poc",

        "demo",
)

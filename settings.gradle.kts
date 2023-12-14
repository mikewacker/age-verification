rootProject.name = "age-verification"

include(
        /* generic modules */

        // base (:api depends on nothing, :data only depends on Jackson, Immutables)
        "base:api:base",
        "base:api:adapter",
        "base:data:json",
        "base:data:crypto",

        // infra (depends on Undertow, OkHttp)
        "infra:api",
        "infra:service",
        "infra:server",
        "infra:client",

        "testing",

        /* product modules */
        "core:data",
        "core:api:types:common",
        "core:api:types:site",
        "core:api:types:avs",
        "core:api:module:common",
        "core:api:endpoint:site",
        "core:api:endpoint:avs",
        "core:service:module:common",
        "core:service:module:site",
        "core:service:module:avs",
        "core:service:endpoint:common",
        "core:service:endpoint:site",

        "module:extractor:common:builtin",
        "module:extractor:common:test",
        "module:store:common:inmemory",
        "module:key:common:test",
        "module:config:site:test",
        "module:location:common:test",

        // other
        "core:verification-poc",

        "demo",
)

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

        // data
        "core:data",

        // api
        "core:common:api:types",
        "core:common:api:module",
        "core:avs:api:types",
        "core:avs:api:endpoint",
        "core:site:api:types",
        "core:site:api:endpoint",

        "module:extractor:common:builtin",
        "module:extractor:common:test",

        // service
        "core:common:service:module",
        "core:common:service:endpoint",
        "core:avs:service:module",
        "core:site:service:module",
        "core:site:service:endpoint",

        "module:store:common:inmemory",

        "module:key:common:test",

        "module:config:site:test",

        "module:location:common:test",

        // other
        "core:verification-poc",

        "demo",
)

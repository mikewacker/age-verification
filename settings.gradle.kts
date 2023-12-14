rootProject.name = "age-verification"

include(
        /* generic modules */
        "api:base",
        "api:adapter",
        "api:data:json",
        "api:data:crypto",

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

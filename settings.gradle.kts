rootProject.name = "age-verification"

include(
        /* generic modules */
        "api:base",
        "api:crypto-data",

        "infra:api",
        "infra:service",
        "infra:server",
        "infra:client",

        "testing",

        /* product modules */

        // data
        "core:data",

        // api
        "core:common:api-types",
        "core:common:api-extractors",
        "core:avs:api",
        "core:site:api",

        "module:extractor:common:builtin",

        // service
        "core:common:service-types",
        "core:common:service",
        "core:avs:service-types",
        "core:site:service-types",
        "core:site:service",

        "module:store:common:inmemory",

        "module:key:common:test",

        "module:config:common:test",
        "module:config:site:test",

        // other
        "core:verification-poc",

        "demo",
)

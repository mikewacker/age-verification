rootProject.name = "age-verification"

include(
        // generic modules
        "api-types",

        "infra:api",
        "infra:service",
        "infra:server",
        "infra:client",

        "testing",

        // product modules
        "core:data",

        "core:common:api-types",
        "core:common:api",
        "core:common:service-types",
        "core:common:service",

        "core:avs:api",

        "core:site:api",
        "core:site:service",

        "core:legacy",
        "core:verification-poc",

        "module:extractor:common:builtin",

        "module:key:common:test",

        "module:config:common:test",
        "module:config:site:test",

        "demo",
)

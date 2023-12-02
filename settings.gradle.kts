rootProject.name = "age-verification"

include(
        // generic modules
        "api",

        "infra:api",
        "infra:service",
        "infra:server",
        "infra:client",

        "testing",

        // product modules
        "core:data",
        "core:common:api",
        "core:common:service",
        "core:avs:api",
        "core:site:api",
        "core:site:service",
        "core:legacy",
        "core:verification-poc",

        "module:extractor:common:builtin",

        "demo",
)

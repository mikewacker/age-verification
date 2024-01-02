rootProject.name = "age-verification"

include(
        // modules to move to Drift
        "base:api:adapter",

        "infra:api",

        // modules to keep
        "crypto:data",

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
        "module:service:resource",
        "module:service:test",

        "demo",
)

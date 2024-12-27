rootProject.name = "age-verification"

include(
        // new
        "crypto",
        "api",

        // legacy
        "crypto:data",

        "core:data",
        "core:api:types",
        "core:api:extractors",
        "core:api:endpoint",
        "core:service:types",
        "core:service:crypto",
        "core:service:endpoint",
        "core:server",

        "module:extractor:builtin",
        "module:extractor:demo",
        "module:extractor:test",
        "module:store:inmemory",
        "module:service:resource",
        "module:service:test",

        "demo",
)

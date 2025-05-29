import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`
    id("org.openapi.generator")
}

// Create the extension and some path utilities.
interface OpenApiJavaExtension {
    val packageName: Property<String>
    val inputSpec: RegularFileProperty
    val inputSpecPath: Provider<String>
        get() = inputSpec.map { it.asFile.absolutePath }
    val dedupSchemas: ListProperty<String>
    val schemaMappings: MapProperty<String, String>
}

val extension = extensions.create<OpenApiJavaExtension>("openApiJava")
extension.packageName.convention("")
extension.inputSpec.convention(layout.projectDirectory.file("src/main/resources/api.yaml"))
extension.dedupSchemas.convention(listOf())
extension.schemaMappings.convention(mapOf())

afterEvaluate {
    if (extension.packageName.get().isEmpty()) {
        throw GradleException("openApiJava.packageName must be set")
    }
}

fun buildDirPath(relativePath: String): Provider<String> =
    layout.buildDirectory.dir(relativePath).map { it.asFile.absolutePath }

// Create a task to validate the OpenAPI YAML.
tasks.register<org.openapitools.generator.gradle.plugin.tasks.ValidateTask>("openApiJavaValidate") {
    group = "OpenAPI Java"
    description = "Validates the OpenAPI YAML."

    inputSpec = extension.inputSpecPath
}

// Create a task to generate JAX-RS server interfaces from the Open API YAML.
val commonGenerateConfigOptions = mapOf(
    "dateLibrary" to "java8",
    "generateBuilders" to "true",
    "hideGenerationTimestamp" to "true",
    "openApiNullable" to "false",
    "sourceFolder" to "",
    "useJakartaEe" to "true",
)

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiJavaGenerateServerTmp") {
    logging.captureStandardOutput(LogLevel.INFO)

    generatorName = "jaxrs-spec"
    inputSpec = extension.inputSpecPath
    schemaMappings = extension.schemaMappings
    outputDir = buildDirPath("tmp/openApiServer")

    // Generate async interfaces for the apis.
    configOptions = commonGenerateConfigOptions + mapOf(
        "interfaceOnly" to "true",
        "supportAsync" to "true",
        "useSwaggerAnnotations" to "false",
        "useTags" to "true",
    )

    // Generate only the apis and models. Don't generate the invokers or other supporting files.
    val apiPackageName = extension.packageName.map { "$it.api" }
    apiPackage = apiPackageName
    modelPackage = apiPackageName
    globalProperties = mapOf(
        "apis" to "",
        "models" to "",
    )
}

tasks.register<Copy>("openApiJavaGenerateServer") {
    group = "OpenAPI Java"
    description = "Generates JAX-RS server interfaces from the OpenAPI YAML."

    from(tasks.named("openApiJavaGenerateServerTmp"))
    into(layout.buildDirectory.dir("generated/sources/openApiServer/java/main"))
    filter { it.replace("<@Valid ", "<") } // see: https://github.com/OpenAPITools/openapi-generator/issues/20377
}

// Create a task to generate Retrofit clients from the Open API YAML.
fun createOpenApiGeneratorIgnore(outputDirPath: String, vararg lines: String) {
    val ignoreFile = layout.buildDirectory.file("$outputDirPath/.openapi-generator-ignore").get().asFile
    val contents = lines.joinToString("") { "$it\n" }
    ignoreFile.writeText(contents)
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiJavaGenerateClient") {
    group = "OpenAPI Java"
    description = "Generates Retrofit clients from the OpenAPI YAML."
    logging.captureStandardOutput(LogLevel.INFO)

    val outputDirPath = "generated/sources/openApiClient/java/main"
    generatorName = "java"
    inputSpec = extension.inputSpecPath
    schemaMappings = provider {
        val packageName = extension.packageName.get()
        extension.dedupSchemas.get().associateWith { "$packageName.api.$it" } + extension.schemaMappings.get()
    }
    outputDir = buildDirPath(outputDirPath)

    // Generate Retrofit clients using Jackson.
    configOptions = commonGenerateConfigOptions + mapOf(
        "library" to "retrofit2",
        "serializationLibrary" to "jackson",
    )

    // Generate only the apis and models (and a few invoker sources that are referenced). Don't generate test.
    val clientPackageName = extension.packageName.map { "$it.api.client" }
    apiPackage = clientPackageName
    modelPackage = clientPackageName
    invokerPackage = clientPackageName.map { "$it.util" }
    globalProperties = mapOf(
        "apiTests" to "false",
        "modelTests" to "false",
    )

    doFirst {
        // Excluding everything and then including things can be finicky with ignore files, but this works.
        val clientPackagePath = clientPackageName.get().replace('.', '/')
        createOpenApiGeneratorIgnore(outputDirPath,
            "**",
            "!$clientPackagePath/*.java",
            "!$clientPackagePath/util/CollectionFormats.java",
            "!$clientPackagePath/util/StringUtil.java",
        )
    }
}

// Compile the Java sources that are generated from the OpenAPI YAML.
sourceSets {
    main {
        java {
            srcDir(tasks.named("openApiJavaGenerateServer"))
            srcDir(tasks.named("openApiJavaGenerateClient"))
        }
    }
}

repositories {
    mavenCentral()
}

val libs = the<LibrariesForLibs>() // version catalog workaround for buildSrc

dependencies {
    api(libs.bundles.jaxRs)
    api(libs.bundles.json)
    api(libs.bundles.retrofit)
}

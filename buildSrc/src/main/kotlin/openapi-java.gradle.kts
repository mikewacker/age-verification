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
}

val extension = extensions.create<OpenApiJavaExtension>("openApiJava")
extension.packageName.convention("")
extension.inputSpec.convention(layout.projectDirectory.file("src/main/resources/api.yaml"))

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

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiJavaGenerateServer") {
    group = "OpenAPI Java"
    description = "Generates JAX-RS server interfaces from the OpenAPI YAML."
    logging.captureStandardOutput(LogLevel.INFO)

    generatorName = "jaxrs-spec"
    inputSpec = extension.inputSpecPath
    outputDir = buildDirPath("generated/sources/openApiServer/java/main")

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
    outputDir = buildDirPath(outputDirPath)

    // Generate Retrofit clients using Jackson.
    configOptions = commonGenerateConfigOptions + mapOf(
        "library" to "retrofit2",
        "serializationLibrary" to "jackson",
    )

    // Generate only the sources for main (except OAuth). Don't generate test.
    val clientPackageName = extension.packageName.map { "$it.client" }
    apiPackage = clientPackageName
    modelPackage = clientPackageName
    invokerPackage = clientPackageName.map { "$it.retrofit" }
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
            "!$clientPackagePath/retrofit/*.java",
            "!$clientPackagePath/retrofit/auth/Api*.java",
            "!$clientPackagePath/retrofit/auth/Http*.java",
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
    api(libs.jackson.annotations)
    api(libs.jackson.databind)
    api(libs.jakartaAnnotation.api)
    api(libs.jakartaValidation.api)
    api(libs.jaxRs.api)
    api(libs.okhttp.okhttp)
    api(libs.retrofit.retrofit)
    implementation(libs.jackson.datatypeJsr310)
    implementation(libs.retrofit.converterJackson)
    implementation(libs.retrofit.converterScalars)
}

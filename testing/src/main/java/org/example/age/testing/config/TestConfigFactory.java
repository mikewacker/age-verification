package org.example.age.testing.config;

import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jersey.validation.Validators;
import jakarta.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.example.age.testing.json.TestObjectMapper;

/** Parses and validates individual pieces of configuration. */
public final class TestConfigFactory<T> {

    private static final Validator validator = Validators.newValidator();
    private static final ConfigurationSourceProvider configResourceProvider = new ResourceConfigurationSourceProvider();

    private final ConfigurationFactory<T> configFactory;

    /** Creates a configuration parser for the provided configuration class. */
    public static <T> TestConfigFactory<T> forClass(Class<T> configClass) {
        ConfigurationFactory<T> configFactory =
                new YamlConfigurationFactory<>(configClass, validator, TestObjectMapper.get(), "dw");
        return new TestConfigFactory<>(configFactory);
    }

    /** Builds configuration from lines of YAML. */
    public T build(String... lines) throws Exception {
        String yaml = String.join("\n", lines);
        ConfigurationSourceProvider configSourceProvider = new StringConfigurationSourceProvider(yaml);
        return configFactory.build(configSourceProvider, "");
    }

    /** Builds configuration from a resource file. */
    public T buildFromResource(String path) throws Exception {
        return configFactory.build(configResourceProvider, path);
    }

    private TestConfigFactory(ConfigurationFactory<T> configFactory) {
        this.configFactory = configFactory;
    }

    /** Provides configuration from a string. */
    private record StringConfigurationSourceProvider(String yaml) implements ConfigurationSourceProvider {

        @Override
        public InputStream open(String path) {
            byte[] bytes = yaml.getBytes(StandardCharsets.UTF_8);
            return new ByteArrayInputStream(bytes);
        }
    }
}

package org.example.age.app.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.core.Configuration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import jakarta.validation.Validator;
import java.io.IOException;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Loads and validates configuration that is stored in test resources. */
public final class TestConfigLoader<C extends Configuration> implements BeforeAllCallback {

    private final Class<C> configClass;
    private YamlConfigurationFactory<C> configFactory;

    /** Create a configuration loader. */
    public TestConfigLoader(Class<C> configClass) {
        this.configClass = configClass;
    }

    /** Loads configuration from test resources. */
    public C load(String path) throws IOException, ConfigurationException {
        ConfigurationSourceProvider provider = new ResourceConfigurationSourceProvider();
        return configFactory.build(provider, path);
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        Validator validator = Validators.newValidator();
        ObjectMapper mapper = Jackson.newObjectMapper();
        configFactory = new YamlConfigurationFactory<>(configClass, validator, mapper, "");
    }
}

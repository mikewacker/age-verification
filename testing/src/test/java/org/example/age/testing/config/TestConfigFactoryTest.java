package org.example.age.testing.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.dropwizard.configuration.ConfigurationException;
import org.example.age.common.api.AgeRange;
import org.junit.jupiter.api.Test;

public class TestConfigFactoryTest {

    private static final TestConfigFactory<AgeRange> configFactory = TestConfigFactory.forClass(AgeRange.class);

    @Test
    public void build() throws Exception {
        AgeRange ageRange = configFactory.build("min: 13", "max: 17");
        AgeRange expectedAgeRange = AgeRange.builder().min(13).max(17).build();
        assertThat(ageRange).isEqualTo(expectedAgeRange);
    }

    @Test
    public void buildFromResource() throws Exception {
        AgeRange ageRange = configFactory.buildFromResource("config.yml");
        AgeRange expectedAgeRange = AgeRange.builder().min(13).max(17).build();
        assertThat(ageRange).isEqualTo(expectedAgeRange);
    }

    @Test
    public void error_UnrecognizedField() {
        assertThatThrownBy(() -> configFactory.build("min: 13", "max: 17", "dne: abc"))
                .isInstanceOf(ConfigurationException.class)
                .hasCauseInstanceOf(UnrecognizedPropertyException.class);
    }
}

package org.example.age.testing.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.dropwizard.configuration.ConfigurationException;
import org.example.age.common.api.AgeRange;
import org.junit.jupiter.api.Test;

public class TestConfigParserTest {

    private static final TestConfigParser<AgeRange> configParser = TestConfigParser.forClass(AgeRange.class);

    @Test
    public void parseLines() throws Exception {
        AgeRange ageRange = configParser.parseLines("min: 13", "max: 17");
        AgeRange expectedAgeRange = AgeRange.builder().min(13).max(17).build();
        assertThat(ageRange).isEqualTo(expectedAgeRange);
    }

    @Test
    public void parseResource() throws Exception {
        AgeRange ageRange = configParser.parseResource("config.yml");
        AgeRange expectedAgeRange = AgeRange.builder().min(13).max(17).build();
        assertThat(ageRange).isEqualTo(expectedAgeRange);
    }

    @Test
    public void error_UnrecognizedField() {
        assertThatThrownBy(() -> configParser.parseLines("min: 13", "max: 17", "dne: abc"))
                .isInstanceOf(ConfigurationException.class)
                .hasCauseInstanceOf(UnrecognizedPropertyException.class);
    }
}

package org.example.age.provider.common.signingkey.demo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigInteger;
import org.example.age.common.annotation.ValueStyle;
import org.immutables.value.Value;

/** Configuration for a private key using elliptic-curve cryptography. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableEcPrivateKeyConfig.class)
public interface EcPrivateKeyConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Value of s. */
    BigInteger s();

    /** Builder for the configuration. */
    final class Builder extends ImmutableEcPrivateKeyConfig.Builder {

        Builder() {}
    }
}

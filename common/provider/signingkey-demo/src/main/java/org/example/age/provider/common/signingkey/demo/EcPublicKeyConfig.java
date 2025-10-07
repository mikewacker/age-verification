package org.example.age.provider.common.signingkey.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigInteger;
import java.security.spec.ECPoint;
import org.example.age.common.annotation.ValueStyle;
import org.immutables.value.Value;

/** Configuration for a public key using elliptic-curve cryptography. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableEcPublicKeyConfig.class)
public interface EcPublicKeyConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** X-value of w. */
    BigInteger wX();

    /** Y-value of w. */
    BigInteger wY();

    /** Value of w. */
    @Value.Derived
    @JsonIgnore
    default ECPoint w() {
        return new ECPoint(wX(), wY());
    }

    /** Builder for the configuration. */
    final class Builder extends ImmutableEcPublicKeyConfig.Builder {

        Builder() {}
    }
}

package org.example.age.module.crypto.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigInteger;
import java.security.spec.ECPoint;
import org.example.age.service.api.config.ConfigStyle;
import org.immutables.value.Value;

/** Public key for elliptic curve cryptography. Does not include the definition of the curve. */
@Value.Immutable
@ConfigStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableEccPublicKey.class)
public interface EccPublicKey {

    /** Creates a public key. */
    static EccPublicKey of(BigInteger wX, BigInteger wY) {
        return ImmutableEccPublicKey.builder().wX(wX).wY(wY).build();
    }

    /** X-value of w. */
    BigInteger wX();

    /** Y-value of w. */
    BigInteger wY();

    /** Value of w. */
    @JsonIgnore
    @Value.Derived
    default ECPoint w() {
        return new ECPoint(wX(), wY());
    }
}

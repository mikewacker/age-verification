package org.example.age.module.crypto.demo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigInteger;
import org.example.age.api.ApiStyle;
import org.immutables.value.Value;

/** Private key for elliptic curve cryptography. Does not include the definition of the curve. */
@Value.Immutable
@ApiStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableEccPrivateKey.class)
public interface EccPrivateKey {

    /** Creates a private key. */
    static EccPrivateKey of(BigInteger s) {
        return ImmutableEccPrivateKey.builder().s(s).build();
    }

    /** Value of s. */
    BigInteger s();
}

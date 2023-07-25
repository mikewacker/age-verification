package org.example.age.demo;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.example.age.data.SecureId;

/** Loads objects from resource files. */
final class Resources {

    private static final ClassLoader classLoader = Resources.class.getClassLoader();

    /**
     * Loads an Ed25519 {@link KeyPair} from the resource file at the relative path.
     *
     *  <p>The private key was generated with the following command:</p>
     *
     *  <p><code>openssl genpkey -algorithm ed25519 -out <i>filename.pem</i></code></p>
     */
    public static KeyPair loadEd25519KeyPair(Path path) throws IOException {
        PrivateKeyInfo bcPrivateKey = loadPrivateKey(path);
        SubjectPublicKeyInfo bcPublicKey = getEd25519PublicKey(bcPrivateKey);
        return convertKeyPair(bcPrivateKey, bcPublicKey);
    }

    /** Loads a {@link SecureId} from the resource file at the relative path. */
    public static SecureId loadSecureId(Path path) throws IOException {
        try (InputStream inputStream = loadInputStream(path)) {
            byte[] bytes = ByteStreams.toByteArray(inputStream);
            return SecureId.ofBytes(bytes);
        }
    }

    /** Loads the private key from the resource file at the relative path. */
    private static PrivateKeyInfo loadPrivateKey(Path path) throws IOException {
        try (Reader reader = new InputStreamReader(loadInputStream(path), StandardCharsets.UTF_8);
                PEMParser pemParser = new PEMParser(reader)) {
            return (PrivateKeyInfo) pemParser.readObject();
        }
    }

    /** Gets the public key from the Ed25519 private key. */
    private static SubjectPublicKeyInfo getEd25519PublicKey(PrivateKeyInfo bcPrivateKey) throws IOException {
        Ed25519PrivateKeyParameters privateKeyParams =
                (Ed25519PrivateKeyParameters) PrivateKeyFactory.createKey(bcPrivateKey);
        Ed25519PublicKeyParameters publicKeyParams = privateKeyParams.generatePublicKey();
        return new SubjectPublicKeyInfo(bcPrivateKey.getPrivateKeyAlgorithm(), publicKeyParams.getEncoded());
    }

    /** Converts the key pair from PEM to JCA. */
    private static KeyPair convertKeyPair(PrivateKeyInfo bcPrivateKey, SubjectPublicKeyInfo bcPublicKey)
            throws PEMException {
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKey privateKey = converter.getPrivateKey(bcPrivateKey);
        PublicKey publicKey = converter.getPublicKey(bcPublicKey);
        return new KeyPair(publicKey, privateKey);
    }

    /** Loads an input stream for a resource file at the relative path. */
    private static InputStream loadInputStream(Path path) {
        return classLoader.getResourceAsStream(path.toString());
    }

    // static class
    private Resources() {}
}

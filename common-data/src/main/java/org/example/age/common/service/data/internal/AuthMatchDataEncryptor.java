package org.example.age.common.service.data.internal;

import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

/** Encrypts and decrypts {@link AuthMatchData}. */
public interface AuthMatchDataEncryptor {

    /** Encrypts {@link AuthMatchData}. */
    AesGcmEncryptionPackage encrypt(AuthMatchData authData, Aes256Key authKey);

    /** Decrypts {@link AuthMatchData}, or returns an error status code. */
    HttpOptional<AuthMatchData> tryDecrypt(AesGcmEncryptionPackage authToken, Aes256Key authKey);
}

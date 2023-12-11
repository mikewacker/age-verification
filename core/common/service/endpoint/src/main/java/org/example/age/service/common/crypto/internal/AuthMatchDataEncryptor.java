package org.example.age.service.common.crypto.internal;

import org.example.age.api.base.HttpOptional;
import org.example.age.api.common.AuthMatchData;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

/** Encrypts and decrypts {@link AuthMatchData}. */
public interface AuthMatchDataEncryptor {

    /** Encrypts {@link AuthMatchData}. */
    AesGcmEncryptionPackage encrypt(AuthMatchData authData, Aes256Key authKey);

    /** Decrypts {@link AuthMatchData}, or returns an error status code. */
    HttpOptional<AuthMatchData> tryDecrypt(AesGcmEncryptionPackage authToken, Aes256Key authKey);
}

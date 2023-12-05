package org.example.age.data.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;

/** Internal {@link ObjectMapper}. */
final class InternalMapper {

    public static final ObjectMapper INSTANCE = new ObjectMapper();

    // static class
    private InternalMapper() {}
}

package org.example.age.common.api.data;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Authentication data that is used to determine if two actions were performed by the same person.
 *
 * <p>This authentication check is done on a best-effort basis,
 * subject to tradeoffs involving privacy, false positives, etc.</p>
 *
 * <p>The data should be serializable as JSON.</p>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface AuthMatchData {

    /** Matches two sets of data, determining if they came from the same person. */
    boolean match(AuthMatchData other);
}

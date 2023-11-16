package org.example.age.common.api.data;

/**
 * Authentication data that is used to determine if two actions were performed by the same person.
 *
 * <p>This authentication check is done on a best-effort basis,
 * subject to tradeoffs involving privacy, false positives, etc.</p>
 *
 * <p>The data should be serializable as JSON.</p>
 */
public interface AuthMatchData {

    /** Matches two sets of data, determining if they came from the same person. */
    boolean match(AuthMatchData other);
}

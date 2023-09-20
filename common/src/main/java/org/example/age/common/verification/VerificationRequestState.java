package org.example.age.common.verification;

import org.example.age.certificate.AuthToken;
import org.example.age.certificate.VerificationRequest;

/** Current state for a verification request. Some fields may be conditionally present based on the status. */
public class VerificationRequestState {

    private VerificationRequestStatus status;
    private VerificationRequest request;
    private AuthToken localToken;

    /** Creates an inactive state. */
    public static VerificationRequestState inactive() {
        return new VerificationRequestState(VerificationRequestStatus.INACTIVE, null, null);
    }

    /** Creates a pending state. */
    public static VerificationRequestState pending(VerificationRequest request, AuthToken token) {
        return new VerificationRequestState(VerificationRequestStatus.PENDING, request, token);
    }

    /** Creates an expired state. */
    public static VerificationRequestState expired(VerificationRequest request) {
        return new VerificationRequestState(VerificationRequestStatus.EXPIRED, request, null);
    }

    /** Gets the current verification request status. */
    public VerificationRequestStatus status() {
        return status;
    }

    /** Gets the pending or expired verification request. */
    public VerificationRequest verificationRequest() {
        return checkAttributeSet(request);
    }

    /** Gets the local authentication token, if the verification request is pending. */
    public AuthToken localAuthToken() {
        return checkAttributeSet(localToken);
    }

    /** Checks that the attribute is set for the current status. */
    private <T> T checkAttributeSet(T attribute) {
        if (attribute == null) {
            String message = String.format("attribute not set when the status is %s", status);
            throw new IllegalStateException(message);
        }

        return attribute;
    }

    private VerificationRequestState(
            VerificationRequestStatus status, VerificationRequest request, AuthToken localToken) {
        this.status = status;
        this.request = request;
        this.localToken = localToken;
    }
}

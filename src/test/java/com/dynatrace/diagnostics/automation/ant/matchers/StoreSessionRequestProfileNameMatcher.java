package com.dynatrace.diagnostics.automation.ant.matchers;

import com.dynatrace.sdk.server.sessions.models.StoreSessionRequest;
import org.mockito.ArgumentMatcher;

public class StoreSessionRequestProfileNameMatcher extends ArgumentMatcher<StoreSessionRequest> {
    private String matchedSystemProfile;

    public StoreSessionRequestProfileNameMatcher(String matchedSessionName) {
        this.matchedSystemProfile = matchedSessionName;
    }

    @Override
    public boolean matches(Object request) {
        if (request instanceof StoreSessionRequest) {
            StoreSessionRequest storeSessionRequest = (StoreSessionRequest) request;

            return storeSessionRequest.getSystemProfile().equals(this.getMatchedSystemProfile());
        }

        return false;
    }

    public String getMatchedSystemProfile() {
        return matchedSystemProfile;
    }

    public void setMatchedSystemProfile(String matchedSystemProfile) {
        this.matchedSystemProfile = matchedSystemProfile;
    }
}

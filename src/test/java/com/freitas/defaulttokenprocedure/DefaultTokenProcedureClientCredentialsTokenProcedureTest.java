package com.freitas.defaulttokenprocedure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.attribute.scim.v2.extensions.DelegationAttributes;
import se.curity.identityserver.sdk.attribute.token.AccessTokenAttributes;
import se.curity.identityserver.sdk.data.authorization.Delegation;
import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.context.ClientCredentialsTokenProcedurePluginContext;
import se.curity.identityserver.sdk.service.issuer.AccessTokenIssuer;
import se.curity.identityserver.sdk.service.issuer.DelegationIssuer;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultTokenProcedureClientCredentialsTokenProcedureTest {

    private final DefaultTokenProcedureClientCredentialsTokenProcedure procedure;

    DefaultTokenProcedureClientCredentialsTokenProcedureTest() {
        this.procedure = new DefaultTokenProcedureClientCredentialsTokenProcedure();
    }

    @Test
    @DisplayName("Should generate an access token")
    void testGenerateAccessToken() throws TokenIssuerException {
        // given
        var context = mock(ClientCredentialsTokenProcedurePluginContext.class);
        var delegationAttributes = mock(DelegationAttributes.class);
        var delegationIssuer = mock(DelegationIssuer.class);
        var delegation = mock(Delegation.class);
        var accessTokenData = mock(AccessTokenAttributes.class);
        var accessTokenIssuer = mock(AccessTokenIssuer.class);

        when(context.getDefaultDelegationData()).thenReturn(delegationAttributes);
        when(context.getDelegationIssuer()).thenReturn(delegationIssuer);
        when(delegationIssuer.issue(delegationAttributes)).thenReturn(delegation);
        when(context.getDefaultAccessTokenData()).thenReturn(accessTokenData);
        when(context.getAccessTokenIssuer()).thenReturn(accessTokenIssuer);
        when(accessTokenIssuer.issue(accessTokenData, delegation)).thenReturn("valid-access-token");
        when(accessTokenData.getExpires()).thenReturn(Instant.now().plusSeconds(300));
        when(accessTokenData.getScope()).thenReturn("admin_read");

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(4, output.getViewData().size()),
                () -> assertEquals("admin_read", output.getViewData().get("scope")),
                () -> assertEquals("valid-access-token", output.getViewData().get("access_token")),
                () -> assertEquals("bearer", output.getViewData().get("token_type")),
                () -> assertEquals(300L, (Long) output.getViewData().get("expires_in"), 2)
        );
    }

    @Test
    @DisplayName("Should not generate an access token when exception is thrown")
    void testException() throws TokenIssuerException {
        // given
        var context = mock(ClientCredentialsTokenProcedurePluginContext.class);
        var delegationAttributes = mock(DelegationAttributes.class);
        var delegationIssuer = mock(DelegationIssuer.class);
        var delegation = mock(Delegation.class);
        var accessTokenData = mock(AccessTokenAttributes.class);
        var accessTokenIssuer = mock(AccessTokenIssuer.class);

        when(context.getDefaultDelegationData()).thenReturn(delegationAttributes);
        when(context.getDelegationIssuer()).thenReturn(delegationIssuer);
        when(delegationIssuer.issue(delegationAttributes)).thenReturn(delegation);
        when(context.getDefaultAccessTokenData()).thenReturn(accessTokenData);
        when(context.getAccessTokenIssuer()).thenReturn(accessTokenIssuer);
        when(accessTokenIssuer.issue(accessTokenData, delegation)).thenThrow(new TokenIssuerException("Invalid state."));

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(2, output.getViewData().size()),
                () -> assertEquals("token_issuer_exception", output.getViewData().get("_problemType")),
                () -> assertEquals("Could not issue new tokens", output.getViewData().get("_problemTitle"))
        );
    }

}
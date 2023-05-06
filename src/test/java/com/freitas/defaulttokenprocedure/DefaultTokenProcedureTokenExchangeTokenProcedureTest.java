package com.freitas.defaulttokenprocedure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.attribute.token.AccessTokenAttributes;
import se.curity.identityserver.sdk.data.authorization.Delegation;
import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.context.TokenExchangeTokenProcedurePluginContext;
import se.curity.identityserver.sdk.service.issuer.AccessTokenIssuer;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultTokenProcedureTokenExchangeTokenProcedureTest {

    private final DefaultTokenProcedureTokenExchangeTokenProcedure procedure;

    DefaultTokenProcedureTokenExchangeTokenProcedureTest() {
        this.procedure = new DefaultTokenProcedureTokenExchangeTokenProcedure();
    }

    @Test
    @DisplayName("Should generate an access token")
    void testGenerateToken() throws TokenIssuerException {
        // given
        var context = mock(TokenExchangeTokenProcedurePluginContext.class);
        var delegation = mock(Delegation.class);
        var accessTokenAttributes = mock(AccessTokenAttributes.class);
        var accessTokenIssuer = mock(AccessTokenIssuer.class);
        when(context.getDelegation()).thenReturn(delegation);
        when(context.getDefaultAccessTokenData(delegation)).thenReturn(accessTokenAttributes);
        when(context.getAccessTokenIssuer()).thenReturn(accessTokenIssuer);
        when(accessTokenIssuer.issue(accessTokenAttributes, delegation)).thenReturn("valid-token");
        when(accessTokenAttributes.getScope()).thenReturn("admin_read");
        when(accessTokenAttributes.getExpires()).thenReturn(Instant.now().plusSeconds(300L));

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(4, output.getViewData().size()),
                () -> assertEquals("admin_read", output.getViewData().get("scope")),
                () -> assertEquals("valid-token", output.getViewData().get("access_token")),
                () -> assertEquals("bearer", output.getViewData().get("token_type")),
                () -> assertEquals(300L, (Long) output.getViewData().get("expires_in"), 2)
        );
    }

    @Test
    @DisplayName("Should not generate an access token when exception is thrown")
    void testException() throws TokenIssuerException {
        // given
        var context = mock(TokenExchangeTokenProcedurePluginContext.class);
        var delegation = mock(Delegation.class);
        var accessTokenAttributes = mock(AccessTokenAttributes.class);
        var accessTokenIssuer = mock(AccessTokenIssuer.class);
        when(context.getDelegation()).thenReturn(delegation);
        when(context.getDefaultAccessTokenData(delegation)).thenReturn(accessTokenAttributes);
        when(context.getAccessTokenIssuer()).thenReturn(accessTokenIssuer);
        when(accessTokenIssuer.issue(accessTokenAttributes, delegation)).thenThrow(new TokenIssuerException("Invalid state."));

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
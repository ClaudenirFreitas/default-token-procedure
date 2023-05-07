package com.freitas.defaulttokenprocedure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.attribute.scim.v2.extensions.DelegationAttributes;
import se.curity.identityserver.sdk.attribute.token.AccessTokenAttributes;
import se.curity.identityserver.sdk.attribute.token.IdTokenAttributes;
import se.curity.identityserver.sdk.attribute.token.RefreshTokenAttributes;
import se.curity.identityserver.sdk.data.authorization.Delegation;
import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.context.AuthorizationCodeTokenProcedurePluginContext;
import se.curity.identityserver.sdk.service.issuer.AccessTokenIssuer;
import se.curity.identityserver.sdk.service.issuer.DelegationIssuer;
import se.curity.identityserver.sdk.service.issuer.IdTokenIssuer;
import se.curity.identityserver.sdk.service.issuer.RefreshTokenIssuer;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultTokenProcedureAuthorizationCodeTokenProcedureTest {

    private final DefaultTokenProcedureAuthorizationCodeTokenProcedure procedure;

    DefaultTokenProcedureAuthorizationCodeTokenProcedureTest() {
        this.procedure = new DefaultTokenProcedureAuthorizationCodeTokenProcedure();
    }

    @Test
    @DisplayName("Should generate an access token without id token")
    void testShouldGenerateAnAccessTokenWithoutIdToken() throws TokenIssuerException {
        // given
        var context = mock(AuthorizationCodeTokenProcedurePluginContext.class);
        var delegationData = mock(DelegationAttributes.class);
        var delegationIssuer = mock(DelegationIssuer.class);
        var issuedDelegation = mock(Delegation.class);
        var accessTokenData = mock(AccessTokenAttributes.class);
        var accessTokenIssuer = mock(AccessTokenIssuer.class);
        var refreshTokenData = mock(RefreshTokenAttributes.class);
        var refreshTokenIssuer = mock(RefreshTokenIssuer.class);

        when(context.getDefaultDelegationData()).thenReturn(delegationData);
        when(context.getDelegationIssuer()).thenReturn(delegationIssuer);
        when(delegationIssuer.issue(delegationData)).thenReturn(issuedDelegation);
        when(context.getDefaultAccessTokenData()).thenReturn(accessTokenData);
        when(context.getAccessTokenIssuer()).thenReturn(accessTokenIssuer);
        when(accessTokenIssuer.issue(accessTokenData, issuedDelegation)).thenReturn("valid-access-token");
        when(accessTokenData.getScope()).thenReturn("openid");
        when(accessTokenData.getExpires()).thenReturn(Instant.now().plusSeconds(300L));
        when(context.getDefaultRefreshTokenData()).thenReturn(refreshTokenData);
        when(context.getRefreshTokenIssuer()).thenReturn(refreshTokenIssuer);
        when(refreshTokenIssuer.issue(refreshTokenData, issuedDelegation)).thenReturn("valid-refresh-token");
        when(context.getDefaultIdTokenData()).thenReturn(null);

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(5, output.getViewData().size()),
                () -> assertEquals("valid-access-token", output.getViewData().get("access_token")),
                () -> assertEquals("openid", output.getViewData().get("scope")),
                () -> assertEquals("valid-refresh-token", output.getViewData().get("refresh_token")),
                () -> assertEquals("bearer", output.getViewData().get("token_type")),
                () -> assertEquals(300L, (Long) output.getViewData().get("expires_in"), 2)
        );
    }

    @Test
    @DisplayName("Should generate an access token with id token")
    void testShouldGenerateAnAccessTokenWithIdToken() throws TokenIssuerException {
        // given
        var context = mock(AuthorizationCodeTokenProcedurePluginContext.class);
        var delegationData = mock(DelegationAttributes.class);
        var delegationIssuer = mock(DelegationIssuer.class);
        var issuedDelegation = mock(Delegation.class);
        var accessTokenData = mock(AccessTokenAttributes.class);
        var accessTokenIssuer = mock(AccessTokenIssuer.class);
        var refreshTokenData = mock(RefreshTokenAttributes.class);
        var refreshTokenIssuer = mock(RefreshTokenIssuer.class);
        var idTokenAttributes = mock(IdTokenAttributes.class);
        var idTokenIssuer = mock(IdTokenIssuer.class);

        when(context.getDefaultDelegationData()).thenReturn(delegationData);
        when(context.getDelegationIssuer()).thenReturn(delegationIssuer);
        when(delegationIssuer.issue(delegationData)).thenReturn(issuedDelegation);
        when(context.getDefaultAccessTokenData()).thenReturn(accessTokenData);
        when(context.getAccessTokenIssuer()).thenReturn(accessTokenIssuer);
        when(accessTokenIssuer.issue(accessTokenData, issuedDelegation)).thenReturn("valid-access-token");
        when(accessTokenData.getScope()).thenReturn("openid");
        when(accessTokenData.getExpires()).thenReturn(Instant.now().plusSeconds(300L));
        when(context.getDefaultRefreshTokenData()).thenReturn(refreshTokenData);
        when(context.getRefreshTokenIssuer()).thenReturn(refreshTokenIssuer);
        when(refreshTokenIssuer.issue(refreshTokenData, issuedDelegation)).thenReturn("valid-refresh-token");
        when(context.getDefaultIdTokenData()).thenReturn(idTokenAttributes);
        when(context.getIdTokenIssuer()).thenReturn(idTokenIssuer);
        when(idTokenIssuer.atHash("valid-access-token")).thenReturn("valid-at-hash");
        when(idTokenIssuer.issue(idTokenAttributes, issuedDelegation)).thenReturn("valid-id-token");

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(6, output.getViewData().size()),
                () -> assertEquals("valid-access-token", output.getViewData().get("access_token")),
                () -> assertEquals("openid", output.getViewData().get("scope")),
                () -> assertEquals("valid-refresh-token", output.getViewData().get("refresh_token")),
                () -> assertEquals("bearer", output.getViewData().get("token_type")),
                () -> assertEquals(300L, (Long) output.getViewData().get("expires_in"), 2),
                () -> assertEquals("valid-id-token", output.getViewData().get("id_token"))
        );
    }

    @Test
    @DisplayName("Should not generate an access token when exception is thrown")
    void testException() throws TokenIssuerException {
        // given
        var context = mock(AuthorizationCodeTokenProcedurePluginContext.class);
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
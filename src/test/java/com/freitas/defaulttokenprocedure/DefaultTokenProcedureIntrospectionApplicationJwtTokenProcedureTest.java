package com.freitas.defaulttokenprocedure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.attribute.token.AccessTokenAttributes;
import se.curity.identityserver.sdk.attribute.token.TokenDataAttributes;
import se.curity.identityserver.sdk.data.authorization.Delegation;
import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.context.IntrospectionTokenProcedurePluginContext;
import se.curity.identityserver.sdk.procedure.token.context.PresentedIntrospectedToken;
import se.curity.identityserver.sdk.service.issuer.AccessTokenIssuer;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultTokenProcedureIntrospectionApplicationJwtTokenProcedureTest {

    private final DefaultTokenProcedureIntrospectionApplicationJwtTokenProcedure procedure;

    DefaultTokenProcedureIntrospectionApplicationJwtTokenProcedureTest() {
        this.procedure = new DefaultTokenProcedureIntrospectionApplicationJwtTokenProcedure();
    }

    @Test
    @DisplayName("Should not introspect the opaque token when opaque token is null")
    void testShouldNotIntrospectWhenOpaqueTokenIsNull() {
        // given
        var context = mock(IntrospectionTokenProcedurePluginContext.class);
        var delegation = mock(Delegation.class);
        when(context.getDefaultAccessTokenJwtIssuer()).thenReturn(null);
        when(context.getDelegation()).thenReturn(delegation);

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertTrue(output.getViewData().isEmpty())
        );
    }

    @Test
    @DisplayName("Should not introspect the opaque token when opaque token is not active")
    void testShouldNotIntrospectWhenOpaqueTokenIsNotActive() {
        // given
        var context = mock(IntrospectionTokenProcedurePluginContext.class);
        var accessTokenJwtIssuer = mock(AccessTokenIssuer.class);
        var delegation = mock(Delegation.class);
        var presentedIntrospectedToken = mock(PresentedIntrospectedToken.class);
        when(presentedIntrospectedToken.isActive()).thenReturn(false);
        when(context.getDefaultAccessTokenJwtIssuer()).thenReturn(accessTokenJwtIssuer);
        when(context.getDelegation()).thenReturn(delegation);
        when(context.getPresentedToken()).thenReturn(presentedIntrospectedToken);

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertTrue(output.getViewData().isEmpty())
        );
    }

    @Test
    @DisplayName("Should not introspect the opaque token when delegation is null")
    void testShouldNotIntrospectWhenDelegationIsNull() {
        // given
        var context = mock(IntrospectionTokenProcedurePluginContext.class);
        var accessTokenJwtIssuer = mock(AccessTokenIssuer.class);
        var presentedIntrospectedToken = mock(PresentedIntrospectedToken.class);
        when(presentedIntrospectedToken.isActive()).thenReturn(true);
        when(context.getDefaultAccessTokenJwtIssuer()).thenReturn(accessTokenJwtIssuer);
        when(context.getDelegation()).thenReturn(null);
        when(context.getPresentedToken()).thenReturn(presentedIntrospectedToken);

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertTrue(output.getViewData().isEmpty())
        );
    }

    @Test
    @DisplayName("Should not introspect the opaque token when exception is thrown")
    void testException() throws TokenIssuerException {
        // given
        var context = mock(IntrospectionTokenProcedurePluginContext.class);
        var accessTokenJwtIssuer = mock(AccessTokenIssuer.class);
        var delegation = mock(Delegation.class);
        var presentedIntrospectedToken = mock(PresentedIntrospectedToken.class);
        var tokenDataAttributes = buildTokenDataAttributes();
        when(presentedIntrospectedToken.isActive()).thenReturn(true);
        when(presentedIntrospectedToken.getTokenData()).thenReturn(tokenDataAttributes);
        when(accessTokenJwtIssuer.issue(any(AccessTokenAttributes.class), any(Delegation.class)))
                .thenThrow(new TokenIssuerException("Invalid state."));
        when(context.getDefaultAccessTokenJwtIssuer()).thenReturn(accessTokenJwtIssuer);
        when(context.getDelegation()).thenReturn(delegation);
        when(context.getPresentedToken()).thenReturn(presentedIntrospectedToken);

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

    @Test
    @DisplayName("Should introspect the opaque token")
    void testShouldIntrospectTheOpaqueToken() throws TokenIssuerException {
        // given
        var context = mock(IntrospectionTokenProcedurePluginContext.class);
        var accessTokenJwtIssuer = mock(AccessTokenIssuer.class);
        var delegation = mock(Delegation.class);
        var presentedIntrospectedToken = mock(PresentedIntrospectedToken.class);
        var tokenDataAttributes = buildTokenDataAttributes();
        when(presentedIntrospectedToken.isActive()).thenReturn(true);
        when(presentedIntrospectedToken.getTokenData()).thenReturn(tokenDataAttributes);
        when(accessTokenJwtIssuer.issue(any(AccessTokenAttributes.class), any(Delegation.class))).thenReturn("valid-jwt");
        when(context.getDefaultAccessTokenJwtIssuer()).thenReturn(accessTokenJwtIssuer);
        when(context.getDelegation()).thenReturn(delegation);
        when(context.getPresentedToken()).thenReturn(presentedIntrospectedToken);

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(2, output.getViewData().size()),
                () -> assertEquals(true, output.getViewData().get("active")),
                () -> assertEquals("valid-jwt", output.getViewData().get("jwt"))
        );
    }

    private TokenDataAttributes buildTokenDataAttributes() {
        return TokenDataAttributes.fromMap(
                Map.of(
                        "purpose", "access_token",
                        "sub", "test@test.com",
                        "iss", "curity",
                        "scope", "admin_read",
                        "created", Instant.now().getEpochSecond(),
                        "iat", Instant.now().getEpochSecond(),
                        "nbf", Instant.now().getEpochSecond(),
                        "exp", Instant.now().plusSeconds(300L).getEpochSecond()
                )
        );
    }

}
package com.freitas.defaulttokenprocedure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.attribute.token.TokenDataAttributes;
import se.curity.identityserver.sdk.data.authorization.Delegation;
import se.curity.identityserver.sdk.procedure.token.context.IntrospectionTokenProcedurePluginContext;
import se.curity.identityserver.sdk.procedure.token.context.PresentedIntrospectedToken;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultTokenProcedureIntrospectionTokenProcedureTest {

    private final DefaultTokenProcedureIntrospectionTokenProcedure procedure;

    DefaultTokenProcedureIntrospectionTokenProcedureTest() {
        this.procedure = new DefaultTokenProcedureIntrospectionTokenProcedure();
    }

    @Test
    @DisplayName("Should not introspect the opaque token when active is false")
    void testActiveIsFalse() {
        // given
        var context = mock(IntrospectionTokenProcedurePluginContext.class);
        var presentedIntrospectedToken = mock(PresentedIntrospectedToken.class);
        when(presentedIntrospectedToken.isActive()).thenReturn(false);
        when(context.getPresentedToken()).thenReturn(presentedIntrospectedToken);

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(1, output.getViewData().size()),
                () -> assertEquals(false, output.getViewData().get("active"))
        );
    }

    @Test
    @DisplayName("Should introspect the opaque token when active is true")
    void testActiveIsTrue() {
        // given
        var context = mock(IntrospectionTokenProcedurePluginContext.class);
        var presentedIntrospectedToken = mock(PresentedIntrospectedToken.class);
        var tokenData = mock(TokenDataAttributes.class);
        var delegation = mock(Delegation.class);
        when(tokenData.asMap()).thenReturn(Collections.emptyMap());
        when(presentedIntrospectedToken.isActive()).thenReturn(true);
        when(presentedIntrospectedToken.getType()).thenReturn("bearer");
        when(presentedIntrospectedToken.getExpiredScopes()).thenReturn(List.of("openid"));
        when(delegation.getClientId()).thenReturn("test-client");
        when(presentedIntrospectedToken.getTokenData()).thenReturn(tokenData);
        when(presentedIntrospectedToken.getTokenDelegation()).thenReturn(delegation);
        when(context.getPresentedToken()).thenReturn(presentedIntrospectedToken);

        // when
        var output = procedure.run(context);

        // then
        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(4, output.getViewData().size()),
                () -> assertEquals(true, output.getViewData().get("active")),
                () -> assertEquals("bearer", output.getViewData().get("token_type")),
                () -> assertEquals("test-client", output.getViewData().get("client_id")),
                () -> assertEquals(List.of("openid"), output.getViewData().get("expired_scope"))
        );
    }

}
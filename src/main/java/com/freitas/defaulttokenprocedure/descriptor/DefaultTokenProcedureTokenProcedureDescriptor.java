package com.freitas.defaulttokenprocedure.descriptor;

import com.freitas.defaulttokenprocedure.*;
import com.freitas.defaulttokenprocedure.config.DefaultTokenProcedureTokenProcedureConfig;
import se.curity.identityserver.sdk.plugin.descriptor.TokenProcedurePluginDescriptor;
import se.curity.identityserver.sdk.procedure.token.*;

public final class DefaultTokenProcedureTokenProcedureDescriptor implements TokenProcedurePluginDescriptor<DefaultTokenProcedureTokenProcedureConfig> {

    @Override
    public String getPluginImplementationType() {
        return "default-token-procedure";
    }

    @Override
    public Class<DefaultTokenProcedureTokenProcedureConfig> getConfigurationType() {
        return DefaultTokenProcedureTokenProcedureConfig.class;
    }

    @Override
    public Class<? extends RefreshTokenProcedure> getOAuthTokenEndpointRefreshTokenProcedure() {
        return DefaultTokenProcedureRefreshTokenProcedure.class;
    }

    @Override
    public Class<? extends AuthorizeCodeTokenProcedure> getOAuthAuthorizeEndpointCodeTokenProcedure() {
        return DefaultTokenProcedureAuthorizeCodeTokenProcedure.class;
    }

    @Override
    public Class<? extends AuthorizeImplicitTokenProcedure> getOAuthAuthorizeEndpointImplicitTokenProcedure() {
        return DefaultTokenProcedureAuthorizeImplicitTokenProcedure.class;
    }

    @Override
    public Class<? extends DeviceAuthorizationTokenProcedure> getOAuthDeviceAuthorizationTokenProcedure() {
        return DefaultTokenProcedureDeviceAuthorizationTokenProcedure.class;
    }

    @Override
    public Class<? extends IntrospectionApplicationJwtTokenProcedure> getOAuthIntrospectApplicationJwtTokenProcedure() {
        return DefaultTokenProcedureIntrospectionApplicationJwtTokenProcedure.class;
    }

    @Override
    public Class<? extends AssertionTokenProcedure> getOAuthTokenEndpointAssertionTokenProcedure() {
        return DefaultTokenProcedureAssertionTokenProcedure.class;
    }

    @Override
    public Class<? extends AuthorizationCodeTokenProcedure> getOAuthTokenEndpointAuthorizationCodeTokenProcedure() {
        return DefaultTokenProcedureAuthorizationCodeTokenProcedure.class;
    }

    @Override
    public Class<? extends IntrospectionTokenProcedure> getOAuthIntrospectTokenProcedure() {
        return DefaultTokenProcedureIntrospectionTokenProcedure.class;
    }

    @Override
    public Class<? extends BackchannelAuthenticationTokenProcedure> getOAuthTokenEndpointBackchannelAuthenticationTokenProcedure() {
        return DefaultTokenProcedureBackchannelAuthenticationTokenProcedure.class;
    }

    @Override
    public Class<? extends ClientCredentialsTokenProcedure> getOAuthTokenEndpointClientCredentialsTokenProcedure() {
        return DefaultTokenProcedureClientCredentialsTokenProcedure.class;
    }

    @Override
    public Class<? extends DeviceCodeTokenProcedure> getOAuthTokenEndpointDeviceCodeTokenProcedure() {
        return DefaultTokenProcedureDeviceCodeTokenProcedure.class;
    }

    @Override
    public Class<? extends RopcTokenProcedure> getOAuthTokenEndpointRopcTokenProcedure() {
        return DefaultTokenProcedureRopcTokenProcedure.class;
    }

    @Override
    public Class<? extends TokenExchangeTokenProcedure> getOAuthTokenEndpointTokenExchangeTokenProcedure() {
        return DefaultTokenProcedureTokenExchangeTokenProcedure.class;
    }

    @Override
    public Class<? extends OpenIdAuthorizeEndpointHybridTokenProcedure> getOpenIdAuthorizeEndpointHybridTokenProcedure() {
        return DefaultTokenProcedureOpenIdAuthorizeEndpointHybridTokenProcedure.class;
    }

    @Override
    public Class<? extends OpenIdUserInfoTokenProcedure> getOpenIdUserInfoTokenProcedure() {
        return DefaultTokenProcedureOpenIdUserInfoTokenProcedure.class;
    }

    @Override
    public Class<? extends AssistedTokenProcedure> getOAuthAssistedTokenTokenProcedure() {
        return DefaultTokenProcedureAssistedTokenProcedure.class;
    }

}

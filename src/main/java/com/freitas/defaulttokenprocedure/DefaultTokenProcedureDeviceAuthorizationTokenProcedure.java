package com.freitas.defaulttokenprocedure;

import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.DeviceAuthorizationTokenProcedure;
import se.curity.identityserver.sdk.procedure.token.context.DeviceAuthorizationTokenProcedurePluginContext;
import se.curity.identityserver.sdk.web.ResponseModel;

import java.time.Instant;
import java.util.HashMap;

public final class DefaultTokenProcedureDeviceAuthorizationTokenProcedure implements DeviceAuthorizationTokenProcedure {

    @Override
    public ResponseModel run(DeviceAuthorizationTokenProcedurePluginContext context) {
        var delegationData = context.getDefaultDelegationData();
        var issuedDelegation = context.getDelegationIssuer().issue(delegationData);
        var accessTokenData = context.getDefaultAccessTokenData();

        try {
            var issuedAccessToken = context.getAccessTokenIssuer().issue(accessTokenData, issuedDelegation);
            var refreshTokenData = context.getDefaultRefreshTokenData();
            var issuedRefreshToken = context.getRefreshTokenIssuer().issue(refreshTokenData, issuedDelegation);

            var responseData = new HashMap<String, Object>(6);
            responseData.put("access_token", issuedAccessToken);
            responseData.put("scope", accessTokenData.getScope());
            responseData.put("refresh_token", issuedRefreshToken);
            responseData.put("token_type", "bearer");
            responseData.put("expires_in", accessTokenData.getExpires().getEpochSecond() - Instant.now().getEpochSecond());

            if (context.getScopeNames().contains("openid")) {
                var idTokenData = context.getDefaultIdTokenData();
                var idTokenIssuer = context.getIdTokenIssuer();
                idTokenData.with(Attribute.of("at_hash", idTokenIssuer.atHash(issuedAccessToken)));
                responseData.put("id_token", idTokenIssuer.issue(idTokenData, issuedDelegation));
            }

            return ResponseModel.mapResponseModel(responseData);
        } catch (TokenIssuerException e) {
            return ResponseModel.problemResponseModel("token_issuer_exception", "Could not issue new tokens");
        }
    }

}

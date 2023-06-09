package com.freitas.defaulttokenprocedure;

import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.AuthorizationCodeTokenProcedure;
import se.curity.identityserver.sdk.procedure.token.context.AuthorizationCodeTokenProcedurePluginContext;
import se.curity.identityserver.sdk.web.ResponseModel;

import java.time.Instant;
import java.util.HashMap;

public final class DefaultTokenProcedureAuthorizationCodeTokenProcedure implements AuthorizationCodeTokenProcedure {

    @Override
    public ResponseModel run(AuthorizationCodeTokenProcedurePluginContext context) {
        try {
            var delegationData = context.getDefaultDelegationData();
            var issuedDelegation = context.getDelegationIssuer().issue(delegationData);
            var accessTokenData = context.getDefaultAccessTokenData();
            var issuedAccessToken = context.getAccessTokenIssuer().issue(accessTokenData, issuedDelegation);
            var refreshTokenData = context.getDefaultRefreshTokenData();
            var issuedRefreshToken = context.getRefreshTokenIssuer().issue(refreshTokenData, issuedDelegation);

            var responseData = new HashMap<String, Object>(6);
            responseData.put("access_token", issuedAccessToken);
            responseData.put("scope", accessTokenData.getScope());
            responseData.put("refresh_token", issuedRefreshToken);
            responseData.put("token_type", "bearer");
            responseData.put("expires_in", accessTokenData.getExpires().getEpochSecond() - Instant.now().getEpochSecond());

            var idTokenData = context.getDefaultIdTokenData();
            if (idTokenData != null) {
                var idTokenIssuer = context.getIdTokenIssuer();
                var atHash = idTokenIssuer.atHash(issuedAccessToken);
                var idToken = idTokenIssuer.issue(idTokenData, issuedDelegation);
                idTokenData.with(Attribute.of("at_hash", atHash));
                responseData.put("id_token", idToken);
            }
            return ResponseModel.mapResponseModel(responseData);
        } catch (TokenIssuerException e) {
            return ResponseModel.problemResponseModel("token_issuer_exception", "Could not issue new tokens");
        }
    }

}


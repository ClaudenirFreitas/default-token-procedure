package com.freitas.defaulttokenprocedure;

import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.RopcTokenProcedure;
import se.curity.identityserver.sdk.procedure.token.context.RopcTokenProcedurePluginContext;
import se.curity.identityserver.sdk.web.ResponseModel;

import java.time.Instant;
import java.util.HashMap;

public final class DefaultTokenProcedureRopcTokenProcedure implements RopcTokenProcedure {

    @Override
    public ResponseModel run(RopcTokenProcedurePluginContext context) {
        var delegationData = context.getDefaultDelegationData();
        var issuedDelegation = context.getDelegationIssuer().issue(delegationData);
        var accessTokenData = context.getDefaultAccessTokenData();

        try {
            var issuedAccessToken = context.getAccessTokenIssuer().issue(accessTokenData, issuedDelegation);
            var refreshTokenData = context.getDefaultRefreshTokenData();
            var issuedRefreshToken = context.getRefreshTokenIssuer().issue(refreshTokenData, issuedDelegation);

            var responseData = new HashMap<String, Object>(5);
            responseData.put("scope", accessTokenData.getScope());
            responseData.put("access_token", issuedAccessToken);
            responseData.put("refresh_token", issuedRefreshToken);
            responseData.put("token_type", "bearer");
            responseData.put("expires_in", accessTokenData.getExpires().getEpochSecond() - Instant.now().getEpochSecond());

            return ResponseModel.mapResponseModel(responseData);
        } catch (TokenIssuerException e) {
            return ResponseModel.problemResponseModel("token_issuer_exception", "Could not issue new tokens");
        }
    }

}

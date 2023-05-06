package com.freitas.defaulttokenprocedure;

import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.TokenExchangeTokenProcedure;
import se.curity.identityserver.sdk.procedure.token.context.TokenExchangeTokenProcedurePluginContext;
import se.curity.identityserver.sdk.web.ResponseModel;

import java.time.Instant;
import java.util.HashMap;

public final class DefaultTokenProcedureTokenExchangeTokenProcedure implements TokenExchangeTokenProcedure {

    @Override
    public ResponseModel run(TokenExchangeTokenProcedurePluginContext context) {
        try {
            var delegation = context.getDelegation();
            var accessTokenData = context.getDefaultAccessTokenData(delegation);
            var issuedAccessToken = context.getAccessTokenIssuer().issue(accessTokenData, delegation);

            var responseData = new HashMap<String, Object>(4);
            responseData.put("scope", accessTokenData.getScope());
            responseData.put("access_token", issuedAccessToken);
            responseData.put("token_type", "bearer");
            responseData.put("expires_in", accessTokenData.getExpires().getEpochSecond() - Instant.now().getEpochSecond());

            return ResponseModel.mapResponseModel(responseData);
        } catch (TokenIssuerException e) {
            return ResponseModel.problemResponseModel("token_issuer_exception", "Could not issue new tokens");
        }
    }

}

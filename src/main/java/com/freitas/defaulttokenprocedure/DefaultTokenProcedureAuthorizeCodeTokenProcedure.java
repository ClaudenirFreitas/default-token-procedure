package com.freitas.defaulttokenprocedure;

import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.AuthorizeCodeTokenProcedure;
import se.curity.identityserver.sdk.procedure.token.context.AuthorizeTokenProcedurePluginContext;
import se.curity.identityserver.sdk.procedure.token.context.OpenIdConnectAuthorizeTokenProcedurePluginContext;
import se.curity.identityserver.sdk.web.ResponseModel;

import java.util.HashMap;

public final class DefaultTokenProcedureAuthorizeCodeTokenProcedure implements AuthorizeCodeTokenProcedure {

    @Override
    public ResponseModel run(AuthorizeTokenProcedurePluginContext context) {
        var authorizationCodeData = context.getDefaultAuthorizationCodeData();

        try {
            var issuedAuthorizationCode = context.getAuthorizationCodeIssuer().issue(authorizationCodeData);

            var responseData = new HashMap<String, Object>(4);
            responseData.put("code", issuedAuthorizationCode);
            responseData.put("state", context.getProvidedState());
            responseData.put("iss", context.getIssuer());

            if (context.getScopeNames().contains("openid") && context instanceof OpenIdConnectAuthorizeTokenProcedurePluginContext openIdContext) {
                responseData.put("session_state", openIdContext.getSessionState());
            }

            return ResponseModel.mapResponseModel(responseData);
        } catch (TokenIssuerException e) {
            return ResponseModel.problemResponseModel("token_issuer_exception", "Could not issue new tokens");
        }
    }

}

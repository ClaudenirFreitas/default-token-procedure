package com.freitas.defaulttokenprocedure;

import se.curity.identityserver.sdk.attribute.token.AccessTokenAttributes;
import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.IntrospectionApplicationJwtTokenProcedure;
import se.curity.identityserver.sdk.procedure.token.context.IntrospectionTokenProcedurePluginContext;
import se.curity.identityserver.sdk.web.ResponseModel;

import java.util.HashMap;


public final class DefaultTokenProcedureIntrospectionApplicationJwtTokenProcedure implements IntrospectionApplicationJwtTokenProcedure {

    @Override
    public ResponseModel run(IntrospectionTokenProcedurePluginContext context) {
        var responseData = new HashMap<String, Object>(2);
        var defaultAtJwtIssuer = context.getDefaultAccessTokenJwtIssuer();

        var delegation = context.getDelegation();

        try {
            if (defaultAtJwtIssuer != null && context.getPresentedToken().isActive() && delegation != null) {
                responseData.put("jwt", defaultAtJwtIssuer.issue(AccessTokenAttributes.of(context.getPresentedToken().getTokenData()), delegation));
                responseData.put("active", true);
            }

            return ResponseModel.mapResponseModel(responseData);
        } catch (TokenIssuerException e) {
            return ResponseModel.problemResponseModel("token_issuer_exception", "Could not issue new tokens");
        }
    }

}

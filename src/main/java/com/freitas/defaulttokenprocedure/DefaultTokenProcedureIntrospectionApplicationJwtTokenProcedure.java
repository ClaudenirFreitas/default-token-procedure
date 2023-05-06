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
        var defaultAtJwtIssuer = context.getDefaultAccessTokenJwtIssuer();
        var delegation = context.getDelegation();

        try {
            var responseData = new HashMap<String, Object>(2);
            if (defaultAtJwtIssuer != null && context.getPresentedToken().isActive() && delegation != null) {
                var accessTokenAttributes = AccessTokenAttributes.of(context.getPresentedToken().getTokenData());
                var jwt = defaultAtJwtIssuer.issue(accessTokenAttributes, delegation);
                responseData.put("jwt", jwt);
                responseData.put("active", true);
            }

            return ResponseModel.mapResponseModel(responseData);
        } catch (TokenIssuerException e) {
            return ResponseModel.problemResponseModel("token_issuer_exception", "Could not issue new tokens");
        }
    }

}

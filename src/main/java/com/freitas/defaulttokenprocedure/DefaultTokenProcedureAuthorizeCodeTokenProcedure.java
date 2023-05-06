package com.freitas.defaulttokenprocedure;

import com.freitas.defaulttokenprocedure.config.DefaultTokenProcedureTokenProcedureConfig;
import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.procedure.token.AuthorizeCodeTokenProcedure;
import se.curity.identityserver.sdk.procedure.token.context.AuthorizeTokenProcedurePluginContext;
import se.curity.identityserver.sdk.procedure.token.context.OpenIdConnectAuthorizeTokenProcedurePluginContext;
import se.curity.identityserver.sdk.web.ResponseModel;

import java.util.HashMap;

public final class DefaultTokenProcedureAuthorizeCodeTokenProcedure implements AuthorizeCodeTokenProcedure
{
    private final DefaultTokenProcedureTokenProcedureConfig _configuration;

    public DefaultTokenProcedureAuthorizeCodeTokenProcedure(DefaultTokenProcedureTokenProcedureConfig configuration)
    {
        _configuration = configuration;
    }

    @Override
    public ResponseModel run(AuthorizeTokenProcedurePluginContext context)
    {
        var authorizationCodeData = context.getDefaultAuthorizationCodeData();

        try
        {
            var issuedAuthorizationCode = context.getAuthorizationCodeIssuer().issue(authorizationCodeData);

            var responseData = new HashMap<String, Object>(4);
            responseData.put("code", issuedAuthorizationCode);
            responseData.put("state", context.getProvidedState());
            responseData.put("iss", context.getIssuer());

            if (context.getScopeNames().contains("openid"))
            {
                if (context instanceof OpenIdConnectAuthorizeTokenProcedurePluginContext)
                {
                    responseData.put("session_state", ((OpenIdConnectAuthorizeTokenProcedurePluginContext) context).getSessionState());
                }
            }

            return ResponseModel.mapResponseModel(responseData);
        }
        catch (TokenIssuerException e)
        {
            return ResponseModel.problemResponseModel("token_issuer_exception", "Could not issue new tokens");
        }
    }
}

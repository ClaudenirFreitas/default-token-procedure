package com.freitas.defaulttokenprocedure;

import se.curity.identityserver.sdk.procedure.token.OpenIdUserInfoTokenProcedure;
import se.curity.identityserver.sdk.procedure.token.context.OpenIdUserInfoTokenProcedurePluginContext;
import se.curity.identityserver.sdk.web.ResponseModel;

public final class DefaultTokenProcedureOpenIdUserInfoTokenProcedure implements OpenIdUserInfoTokenProcedure {

    @Override
    public ResponseModel run(OpenIdUserInfoTokenProcedurePluginContext context) {
        var responseData = context.getDefaultResponseData().asMap();
        var name = context.getAccountAttributes().getName();
        if (name != null) {
            var formattedName = name.getFormatted();
            if (formattedName != null && !formattedName.isEmpty()) {
                responseData.put("name", formattedName);
            }
        }

        var presentedTokenData = context.getPresentedToken().getTokenData();
        responseData.put("scope", presentedTokenData.getMandatoryValue("scope", String.class));

        var delegation = context.getPresentedToken().getTokenDelegation();
        if (delegation != null) {
            responseData.put("client_id", delegation.getClientId());
        }

        var accountAttributes = context.getAccountAttributes();
        if (accountAttributes != null) {
            responseData.put("preferred_username", accountAttributes.getUserName());
        }

        return ResponseModel.mapResponseModel(responseData);
    }

}

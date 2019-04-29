package io.slingr.endpoints.http;

import io.slingr.endpoints.HttpEndpoint;
import io.slingr.endpoints.configurations.Configuration;
import io.slingr.endpoints.exceptions.EndpointException;
import io.slingr.endpoints.exceptions.ErrorCode;
import io.slingr.endpoints.framework.annotations.ApplicationLogger;
import io.slingr.endpoints.framework.annotations.EndpointConfiguration;
import io.slingr.endpoints.framework.annotations.EndpointProperty;
import io.slingr.endpoints.framework.annotations.SlingrEndpoint;
import io.slingr.endpoints.services.AppLogs;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.utils.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>HTTP endpoint
 *
 * <p>Created by dgaviola on 08/22/15.
 */
@SlingrEndpoint(name = "http", functionPrefix = "_")
public class Http extends HttpEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(Http.class);

    @ApplicationLogger
    private AppLogs appLogs;

    @EndpointProperty
    private String baseUrl;

    @EndpointConfiguration
    private Json configuration;

    @Override
    public String getApiUri() {
        return StringUtils.isNotBlank(baseUrl) ? baseUrl : "";
    }

    @Override
    public void endpointStarted() {
        final String headers = configuration.string("defaultHeaders", "");
        try {
            final Json jHeaders = checkHeaders(headers);
            jHeaders.forEachMapString(httpService()::setupDefaultHeader);
        } catch (Exception ex){
            appLogs.error(String.format("Invalid default headers defined for HTTP endpoint. Please check them [%s]", headers));
        }

        httpService().setDefaultEmptyPath(configuration.string("emptyPath", ""));

        httpService().setRememberCookies(Configuration.parseBooleanValue(configuration.string("rememberCookies"), false));

        if(StringUtils.isBlank(baseUrl)){
            httpService().setAllowExternalUrl(true);
        } else {
            httpService().setAllowExternalUrl(Configuration.parseBooleanValue(configuration.string("allowExternalUrl"), false));
        }

        httpService().setFollowRedirects(Configuration.parseBooleanValue(configuration.string("followRedirects"), true));
        httpService().setConnectionTimeout(configuration.integer("connectionTimeout", 5000));
        httpService().setReadTimeout(configuration.integer("readTimeout", 60000));

        final String authType = configuration.string("authType", "");

        if(StringUtils.isNotBlank(authType)) {
            final String username = configuration.string("username", "");
            final String password = configuration.string("password", "");

            if ("basic".equalsIgnoreCase(authType)) {
                httpService().setupBasicAuthentication(username, password);

                logger.info(String.format("Configured HTTP Basic authentication: username [%s] - password [%s]", username, Strings.maskToken(password)));
            } else if ("digest".equalsIgnoreCase(authType)) {
                httpService().setupDigestAuthentication(username, password);

                logger.info(String.format("Configured HTTP Digest authentication: username [%s] - password [%s]", username, Strings.maskToken(password)));
            } else {
                logger.info("Configured without HTTP authentication");
            }
        }

        logger.info(String.format("Configured HTTP endpoint: baseUrl [%s]", baseUrl));
    }


    /**
     * Converts the string headers representation in a Json map object
     *
     * @param stringHeaders string headers list
     * @return json map object
     */
    private static Json checkHeaders(String stringHeaders) {
        final Json headers = Json.map();
        try {
            if (StringUtils.isNotBlank(stringHeaders)){
                final String[] pairs = StringUtils.split(stringHeaders, ",");
                for (String pair : pairs) {
                    final String[] keyValue = StringUtils.split(pair, "=");

                    headers.set(keyValue[0].trim(), keyValue.length > 1 ? keyValue[1].trim() : true);
                }
            }
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Default headers [%s] are invalid", stringHeaders));
        }
        return headers;
    }
}

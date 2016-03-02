/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.view;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * Utility for adding a content security policy for securing viewed documents.
 */
public class ViewContentSecurityPolicy {
    private static String CONTENT_SECURITY_POLICY = StringUtils.join(Arrays.asList(
            // Unless another directive applies, prevent loading content
            "default-src 'none'",

            // Allow CSS, fonts, images and media (video, audio etc) to come from any domain or inline
            "font-src * 'unsafe-inline'",
            // Firefox requires explicitly allowing data: scheme for base64 images.
            "img-src * 'unsafe-inline' data:",
            "style-src * 'unsafe-inline'",
            "media-src * 'unsafe-inline'",
            // Since view server returns specific fixed scripts; we hardcode exceptions to run those particular scripts.
            // Tested against viewserver 11.0.0 build 1262096
            "script-src 'sha256-OdLsMSxX1u85wjaFsZU9ffssiX6Lpel0V+I6Jmn3kHI=' 'sha256-UlZRaL7g2EUMkPiWo2wmQXJVku/0E1A7Cp/mTgG22ZA='",

            // Behaves like the iframe sandbox attribute, disabling potentially dangerous features such as form submission
            // Allow same origin so CSS etc can be loaded
            // We need allow-scripts so viewserver's inline javascript can run
            "sandbox allow-same-origin allow-scripts"
    ), "; ");

    private ViewContentSecurityPolicy() {}

    /**
     * Add content security policy headers to an HTTP response. These control what child content can be loaded from the
     * proxied document, reducing the risk of allowing users to serve arbitrary documents from the application domain.
     * @param response The HTTP response
     */
    public static void addContentSecurityPolicy(final HttpServletResponse response) {
        // We need both headers to support all browsers
        response.addHeader("Content-Security-Policy", CONTENT_SECURITY_POLICY);
        response.addHeader("X-Content-Security-Policy", CONTENT_SECURITY_POLICY);
    }
}

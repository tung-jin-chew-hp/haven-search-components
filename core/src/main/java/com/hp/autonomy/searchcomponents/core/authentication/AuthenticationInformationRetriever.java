/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.authentication;

import org.springframework.security.core.Authentication;

import java.security.Principal;

public interface AuthenticationInformationRetriever<P extends Principal> {
    Authentication getAuthentication();
    P getPrincipal();
}

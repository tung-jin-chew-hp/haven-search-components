/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Retrieves {@link AciService} object for connecting to either QMS or Content,
 * depending upon which is configured/required
 */
public interface AciServiceRetriever {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String ACI_SERVICE_RETRIEVER_BEAN_NAME = "aciServiceRetriever";

    /**
     * Whether or not a qms server is configured
     *
     * @return Whether or not a qms server is configured
     */
    boolean qmsEnabled();

    /**
     * Retrieves object for connecting to either QMS or Content
     *
     * @param queryType if raw, will retrieve Content AciService regardless; otherwise will retrieve QMS AciService if available
     * @return the QMS AciService if required and configured, otherwise the Content AciService
     */
    AciService getAciService(final SearchRequest.QueryType queryType);
}

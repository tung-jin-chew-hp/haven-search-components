/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.test;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.test.IntegrationTestUtils;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class HodIntegrationTestUtils extends IntegrationTestUtils<ResourceIdentifier, HodSearchResult, HodErrorException> {
    @Override
    public List<ResourceIdentifier> getDatabases() {
        return Collections.singletonList(ResourceIdentifier.NEWS_ENG);
    }

    @Override
    public QueryRestrictions<ResourceIdentifier> buildQueryRestrictions() {
        return new HodQueryRestrictions.Builder()
                .setQueryText("*")
                .setFieldText("")
                .setDatabases(getDatabases())
                .setAnyLanguage(true)
                .build();
    }
}

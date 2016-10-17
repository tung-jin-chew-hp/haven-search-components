/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.QsElement;
import com.hp.autonomy.types.idol.responses.QueryResponseData;
import com.hp.autonomy.types.requests.idol.actions.query.QueryActions;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@Service
public class IdolRelatedConceptsService implements RelatedConceptsService<QsElement, String, AciErrorException> {

    private final HavenSearchAciParameterHandler parameterHandler;
    private final AciService contentAciService;
    private final Processor<QueryResponseData> queryResponseProcessor;

    @Autowired
    public IdolRelatedConceptsService(final HavenSearchAciParameterHandler parameterHandler, final AciService contentAciService, final ProcessorFactory processorFactory) {
        this.parameterHandler = parameterHandler;
        this.contentAciService = contentAciService;
        queryResponseProcessor = processorFactory.getResponseDataProcessor(QueryResponseData.class);
    }

    @Override
    public List<QsElement> findRelatedConcepts(final RelatedConceptsRequest<String> relatedConceptsRequest) throws AciErrorException {
        final AciParameters parameters = new AciParameters(QueryActions.Query.name());
        parameterHandler.addSecurityInfo(parameters);
        parameterHandler.addSearchRestrictions(parameters, relatedConceptsRequest.getQueryRestrictions());
        parameters.add(QueryParams.MaxResults.name(), relatedConceptsRequest.getMaxResults());
        parameters.add(QueryParams.Print.name(), PrintParam.NoResults);
        parameters.add(QueryParams.QuerySummary.name(), true);
        parameters.add(QueryParams.QuerySummaryLength.name(), relatedConceptsRequest.getQuerySummaryLength());

        final QueryResponseData responseData = contentAciService.executeAction(parameters, queryResponseProcessor);
        return responseData.getQs() != null ? responseData.getQs().getElement() : Collections.emptyList();
    }
}

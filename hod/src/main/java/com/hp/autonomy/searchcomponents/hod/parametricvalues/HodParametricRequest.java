/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = HodParametricRequest.Builder.class)
public class HodParametricRequest implements ParametricRequest<ResourceIdentifier> {
    private static final long serialVersionUID = 2235023046934181036L;

    public static final int MAX_VALUES_DEFAULT = 5;

    private List<String> fieldNames = Collections.emptyList();
    private Integer maxValues = MAX_VALUES_DEFAULT;
    private QueryRestrictions<ResourceIdentifier> queryRestrictions;
    private boolean modified = true;
    // NOTE: not supported by HOD, and doing so with AUTN_DATE will be fun with ESF which has no default autn_date field
    private String datePeriod = null;

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private List<String> fieldNames = Collections.emptyList();
        private Integer maxValues = MAX_VALUES_DEFAULT;
        private QueryRestrictions<ResourceIdentifier> queryRestrictions;
        private boolean modified = true;
        private String datePeriod = null;

        public Builder(final ParametricRequest<ResourceIdentifier> hodParametricRequest) {
            fieldNames = hodParametricRequest.getFieldNames();
            maxValues = hodParametricRequest.getMaxValues();
            queryRestrictions = hodParametricRequest.getQueryRestrictions();
            modified = hodParametricRequest.isModified();
            datePeriod = hodParametricRequest.getDatePeriod();
        }

        public HodParametricRequest build() {
            return new HodParametricRequest(fieldNames, maxValues, queryRestrictions, modified, datePeriod);
        }
    }
}

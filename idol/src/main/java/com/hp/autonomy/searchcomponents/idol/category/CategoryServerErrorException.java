/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.category;

import lombok.Getter;

public class CategoryServerErrorException extends RuntimeException {
    private static final long serialVersionUID = 1473720273187732458L;
    @Getter
    private final String jobName;

    public CategoryServerErrorException(final String jobName, final Throwable cause) {
        super("HTTP error communicating with Category Server", cause);

        this.jobName = jobName;
    }
}

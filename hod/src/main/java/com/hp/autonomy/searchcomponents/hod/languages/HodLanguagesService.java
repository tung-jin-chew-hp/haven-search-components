/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.languages;

import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.types.idol.LanguageType;

import java.util.HashMap;
import java.util.Map;

public class HodLanguagesService implements LanguagesService {
    public static final String THE_LANGUAGE = "English";

    @Override
    public Map<String, LanguageType> getLanguages() {
        final LanguageType theLanguage = new LanguageType();
        theLanguage.setName(THE_LANGUAGE);
        theLanguage.setLanguage(THE_LANGUAGE);
        theLanguage.setDocuments(10);

        final Map<String, LanguageType> languages = new HashMap<>(1);
        languages.put(THE_LANGUAGE, theLanguage);
        return languages;
    }

    @Override
    public String getDefaultLanguageId() {
        return THE_LANGUAGE;
    }

    @Override
    public boolean isValidLanguage(final String language) {
        return language.equals(THE_LANGUAGE);
    }
}

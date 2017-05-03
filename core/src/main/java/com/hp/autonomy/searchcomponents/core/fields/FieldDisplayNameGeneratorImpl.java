/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldValue;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.core.fields.FieldDisplayNameGenerator.FIELD_DISPLAY_NAME_GENERATOR_BEAN_NAME;

/**
 * Default implementation of {@link FieldDisplayNameGenerator}.
 * Replaces underscores with spaces and capitalises the first letter of each word.
 */
@SuppressWarnings("unused")
@Component(FIELD_DISPLAY_NAME_GENERATOR_BEAN_NAME)
class FieldDisplayNameGeneratorImpl implements FieldDisplayNameGenerator {
    private final ConfigService<? extends HavenSearchCapable> configService;

    @Autowired
    public FieldDisplayNameGeneratorImpl(final ConfigService<? extends HavenSearchCapable> configService) {
        this.configService = configService;
    }

    @Override
    public String generateDisplayName(final FieldPath path) {
        final Optional<FieldInfo<?>> maybeFieldInfo = getFieldInfoFromConfigByName(path);
        return maybeFieldInfo
                .map(fieldInfo -> Optional.ofNullable(fieldInfo.getDisplayName())
                        .orElseGet(() -> defaultGenerateDisplayNameFromId(fieldInfo.getId())))
                .orElseGet(() -> defaultGenerateDisplayName(path));
    }

    @Override
    public String generateDisplayNameFromId(final String id) {
        final Optional<FieldInfo<?>> maybeFieldInfo = getFieldInfoFromConfigById(id);
        return maybeFieldInfo
                .flatMap(fieldInfo -> Optional.ofNullable(fieldInfo.getDisplayName()))
                .orElseGet(() -> defaultGenerateDisplayNameFromId(id));
    }

    @Override
    public <T extends Serializable> String generateDisplayValue(final FieldPath path, final T maybeValue, final FieldType fieldType) {
        return Optional.ofNullable(maybeValue)
                .map(value -> {
                    final Optional<FieldInfo<?>> maybeFieldInfo = getFieldInfoFromConfigByName(path);
                    return maybeFieldInfo
                            .flatMap(fieldInfo -> {
                                final Optional<String> displayValue = fieldInfo.getValues()
                                        .stream()
                                        .filter(fieldValue -> compareValues(value, fieldValue))
                                        .findFirst()
                                        .map(FieldValue::getDisplayValue);

                                return displayValue.isPresent() ? displayValue
                                    : fieldInfo.isWhitelist() ? Optional.of(FieldsInfo.BLACKLISTED_VALUE)
                                    : Optional.empty();
                            })
                            .orElseGet(() -> defaultGenerateDisplayValue(value));
                }).orElse(null);

    }

    @Override
    public <T extends Serializable> String generateDisplayValueFromId(final String id, final T maybeValue, final FieldType fieldType) {
        return Optional.ofNullable(maybeValue)
                .map(value -> {
                    final Optional<FieldInfo<?>> maybeFieldInfo = getFieldInfoFromConfigById(id);
                    return maybeFieldInfo
                            .flatMap(fieldInfo -> fieldInfo.getValues()
                                    .stream()
                                    .filter(fieldValue -> compareValues(value, fieldValue))
                                    .findFirst()
                                    .map(FieldValue::getDisplayValue))
                            .orElseGet(() -> defaultGenerateDisplayValue(value));
                }).orElse(null);
    }

    private <T extends Serializable> boolean compareValues(final T value, final FieldValue<?> fieldValue) {
        return String.valueOf(value).equalsIgnoreCase(String.valueOf(fieldValue.getValue()));
    }

    private Optional<FieldInfo<?>> getFieldInfoFromConfigById(final String id) {
        return Optional.ofNullable(configService.getConfig().getFieldsInfo().getFieldConfig().get(id));
    }

    private Optional<FieldInfo<?>> getFieldInfoFromConfigByName(final FieldPath path) {
        return Optional.ofNullable(configService.getConfig().getFieldsInfo().getFieldConfigByName().get(path));
    }

    private String defaultGenerateDisplayName(final FieldPath fieldPath) {
        final String normalisedFieldName = fieldPath.getNormalisedPath();
        return generateDisplayName(normalisedFieldName);
    }

    private String defaultGenerateDisplayNameFromId(final String id) {
        return generateDisplayName(id);
    }

    private <T extends Serializable> String defaultGenerateDisplayValue(final T value) {
        return String.valueOf(value);
    }

    private String generateDisplayName(final String normalisedFieldName) {
        final String fieldName = normalisedFieldName.contains("/") ? normalisedFieldName.substring(normalisedFieldName.lastIndexOf('/') + 1) : normalisedFieldName;
        return String.join(" ", Arrays.stream(StringUtils.split(fieldName, '_'))
                .filter(StringUtils::isNotBlank)
                .map(WordUtils::capitalizeFully)
                .collect(Collectors.toList()));
    }
}

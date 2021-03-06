/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.hp.autonomy.types.idol.DocContent;
import com.hp.autonomy.types.idol.Hit;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldsParserTest {
    @Mock
    private ConfigService<? extends IdolSearchCapable> configService;

    @Mock
    private IdolSearchCapable config;

    private FieldsParser fieldsParser;

    @Before
    public void setUp() {
        fieldsParser = new FieldsParserImpl(configService);

        final FieldsInfo fieldsInfo = new FieldsInfo.Builder()
                .populateResponseMap("Custom Date", new FieldInfo<DateTime>("Custom Date", Collections.singletonList("CUSTOM_DATE"), FieldType.DATE))
                .populateResponseMap("author", new FieldInfo<String>("author", Collections.singletonList("CUSTOM_ARRAY"), FieldType.STRING))
                .build();
        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
        when(configService.getConfig()).thenReturn(config);
    }

    @Test
    public void parseDocumentFields() {
        final IdolSearchResult.Builder builder = new IdolSearchResult.Builder();
        fieldsParser.parseDocumentFields(mockHit(), builder);
        final IdolSearchResult idolSearchResult = builder.build();
        final Map<String, FieldInfo<?>> fieldMap = idolSearchResult.getFieldMap();
        assertNotNull(fieldMap.get("Custom Date"));
        assertThat(fieldMap.get("author").getValues(), hasSize(2));
    }

    @Test
    public void parseStaticContentPromotionResult() {
        final IdolSearchResult.Builder builder = new IdolSearchResult.Builder();
        final Hit hit = mockHit();
        hit.setPromotionname("SomeName");
        fieldsParser.parseDocumentFields(hit, builder);
        final IdolSearchResult idolSearchResult = builder.build();
        assertEquals(PromotionCategory.STATIC_CONTENT_PROMOTION, idolSearchResult.getPromotionCategory());
    }

    @Test
    public void parseCardinalPlacementPromotionResult() {
        final IdolSearchResult.Builder builder = new IdolSearchResult.Builder();
        final Hit hit = mockInjectedPromotionHit();
        fieldsParser.parseDocumentFields(hit, builder);
        final IdolSearchResult idolSearchResult = builder.build();
        assertEquals(PromotionCategory.CARDINAL_PLACEMENT, idolSearchResult.getPromotionCategory());
    }

    private Hit mockHit() {
        final Hit hit = new Hit();
        hit.setTitle("Some Title");

        final DocContent content = new DocContent();
        final Element element = mock(Element.class);
        content.getContent().add(element);

        when(element.hasChildNodes()).thenReturn(true);
        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(4);
        mockNodeListEntry(childNodes, 0, "CUSTOM_DATE", "2016-02-03T11:42:00");
        mockNodeListEntry(childNodes, 1, "CUSTOM_ARRAY", "a");
        mockNodeListEntry(childNodes, 2, "CUSTOM_ARRAY", "b");
        mockNodeListEntry(childNodes, 3, "UNKNOWN", "c");
        when(element.getChildNodes()).thenReturn(childNodes);

        mockHardCodedField(element, IdolDocumentFieldsService.QMS_ID_FIELD.toUpperCase(), "123");
        when(element.getElementsByTagName(IdolDocumentFieldsService.INJECTED_PROMOTION_FIELD.toUpperCase())).thenReturn(mock(NodeList.class));

        hit.setContent(content);

        return hit;
    }

    private void mockHardCodedField(final Element element, final String name, final String value) {
        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(1);
        mockNodeListEntry(childNodes, 0, name, value);
        when(element.getElementsByTagName(name)).thenReturn(childNodes);
    }

    private void mockNodeListEntry(final NodeList nodes, final int i, final String name, final String value) {
        final Element namedNode = mock(Element.class);
        when(namedNode.getNodeName()).thenReturn(name);
        final Text textNode = mock(Text.class);
        when(textNode.getNodeValue()).thenReturn(value);
        final NodeList textNodes = mock(NodeList.class);
        when(namedNode.getChildNodes()).thenReturn(textNodes);
        when(textNodes.getLength()).thenReturn(1);
        when(textNodes.item(0)).thenReturn(textNode);
        when(namedNode.getFirstChild()).thenReturn(textNode);
        when(nodes.item(i)).thenReturn(namedNode);
    }

    private Hit mockInjectedPromotionHit() {
        final Hit hit = new Hit();
        hit.setTitle("Some Other Title");

        final DocContent content = new DocContent();
        final Element element = mock(Element.class);
        when(element.hasChildNodes()).thenReturn(true);
        when(element.getChildNodes()).thenReturn(mock(NodeList.class));

        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(1);
        final String name = IdolDocumentFieldsService.INJECTED_PROMOTION_FIELD.toUpperCase();
        mockNodeListEntry(childNodes, 0, name, "true");
        when(element.getElementsByTagName(anyString())).thenReturn(mock(NodeList.class));
        when(element.getElementsByTagName(name)).thenReturn(childNodes);
        content.getContent().add(element);
        hit.setContent(content);

        return hit;
    }
}

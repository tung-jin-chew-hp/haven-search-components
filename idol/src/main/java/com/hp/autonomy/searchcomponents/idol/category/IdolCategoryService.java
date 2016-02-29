package com.hp.autonomy.searchcomponents.idol.category;

/*
 * $Id:$
 *
 * Copyright (c) 2016, Autonomy Systems Ltd.
 *
 * Last modified by $Author$ on $Date$ 
 */

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.AciServiceException;
import com.autonomy.aci.client.services.ProcessorException;
import com.autonomy.aci.client.services.impl.AbstractStAXProcessor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.CopyResponseProcessor;
import com.hp.autonomy.searchcomponents.idol.category.configuration.CategoryCapable;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdolCategoryService {
    private final AciService categoryAciService;

    private final ConfigService<? extends CategoryCapable> configService;

    @Autowired
    public IdolCategoryService(final AciService categoryAciService, final ConfigService<? extends CategoryCapable> configService) {
        this.categoryAciService = categoryAciService;
        this.configService = configService;
    }

    public void clusterServe2DMap(final String sourceJobName, final String startDate, final String endDate, final OutputStream outputStream) throws CategoryServerErrorException {
        final AciParameters params = new AciParameters("ClusterServe2DMap");
        params.add("sourceJobName", sourceJobName);

        if (StringUtils.isNotEmpty(startDate)) {
            params.add("startDate", startDate);
        }

        if (StringUtils.isNotEmpty(endDate)) {
            params.add("endDate", endDate);
        }

        try {
            // Note: this fails when using old category, since category returns a Content-Encoding: deflate but
            //   doesn't actually compress the JPG binary response; at least on 7.5.13.0.
            categoryAciService.executeAction(params, new CopyResponseProcessor(outputStream));
        } catch (final AciServiceException e) {
            throw new CategoryServerErrorException(sourceJobName, e);
        }
    }

    public List<Cluster> clusterResults(final String sourceJobName, final String startDate, final String endDate){
        final AciParameters params = new AciParameters("ClusterResults");
        params.add("sourceJobName", sourceJobName);
        params.add("numResults", 0);

        if (StringUtils.isNotEmpty(startDate)) {
            params.add("startDate", startDate);
        }
        if (StringUtils.isNotEmpty(endDate)) {
            params.add("endDate", endDate);
        }

        return categoryAciService.executeAction(params, new AbstractStAXProcessor<List<Cluster>>() {
            @Override
            public List<Cluster> process(final XMLStreamReader xmlStreamReader) {

                try {
                    final ArrayList<Cluster> list = new ArrayList<>();

                    Cluster cluster = null;

                    while(xmlStreamReader.hasNext()) {
                        final int evtType = xmlStreamReader.next();

                        switch (evtType) {
                            case XMLEvent.START_ELEMENT:
                                final String localName = xmlStreamReader.getLocalName();
                                if ("autn:cluster".equals(localName)) {
                                    cluster = new Cluster();
                                }
                                else if (cluster != null) {
                                    if ("autn:x_coord".equals(localName)) {
                                        cluster.setX(NumberUtils.toInt(xmlStreamReader.getElementText()));
                                    }
                                    else if ("autn:y_coord".equals(localName)) {
                                        cluster.setY(NumberUtils.toInt(xmlStreamReader.getElementText()));
                                    }
                                    else if ("autn:title".equals(localName)) {
                                        cluster.setTitle(xmlStreamReader.getElementText());
                                    }
                                    else if ("autn:score".equals(localName)) {
                                        cluster.setScore(NumberUtils.toDouble(xmlStreamReader.getElementText()));
                                    }
                                }
                                break;
                            case XMLEvent.END_ELEMENT:
                                if ("autn:cluster".equals(xmlStreamReader.getLocalName()) && cluster != null) {
                                    list.add(cluster);
                                }
                        }
                    }

                    return list;
                } catch (XMLStreamException e) {
                    throw new ProcessorException("Error reading XML", e);
                }
            }
        });
    }
}

package org.springframework.social.salesforce.api.impl;

import org.springframework.social.salesforce.api.ApexRestOperations;
import org.springframework.social.salesforce.api.Salesforce;
import org.springframework.social.support.URIBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class ApexRestTemplate extends AbstractSalesForceOperations<Salesforce> implements ApexRestOperations {

    private static final String APEX_REST_PATH = "/services/apexrest/";
    private static final String PATH_SEPARATOR = "/";

    private final RestTemplate restTemplate;

    public ApexRestTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> T get(String resource, String identifier, Class<T> resultType) {
        URI uri = URIBuilder.fromUri(api.getInstanceUrl() + APEX_REST_PATH + resource + PATH_SEPARATOR + identifier).build();
        return restTemplate.getForObject(uri, resultType);
    }
}

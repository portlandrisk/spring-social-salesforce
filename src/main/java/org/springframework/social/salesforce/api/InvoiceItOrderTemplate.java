package org.springframework.social.salesforce.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.social.salesforce.api.impl.AbstractSalesForceOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class InvoiceItOrderTemplate extends AbstractSalesForceOperations<Salesforce> implements InvoiceItOperations {

    public static final String INVOICE_IT_ORDERS_API_URI = "/services/apexrest/Invoice_API/invoiceit/orders";
    private RestTemplate restTemplate;

    public InvoiceItOrderTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;

    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Object> createOrder(Map<String, Object> fields) {
        requireAuthorization();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map> entity = new HttpEntity<Map>(fields, headers);

        return restTemplate.postForObject(api.getBaseUrl() + INVOICE_IT_ORDERS_API_URI, entity, Map.class);
    }


}

package org.springframework.social.salesforce.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.social.ApiBinding;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Specifies operations performed on Salesforce.
 *
 * @author Umut Utkan
 */
public interface Salesforce extends ApiBinding {

    String PROVIDER_ID = "salesforce";

    public ApiOperations apiOperations();

    public ApexRestOperations apexRestOperations();

    public ChatterOperations chatterOperations();

    public QueryOperations queryOperations();

    public RecentOperations recentOperations();

    public SearchOperations searchOperations();

    public InvoiceItOperations invoiceItOperations();

    public SObjectOperations sObjectsOperations();

    public <T> List<T> readList(JsonNode jsonNode, Class<T> type);

    public <T> T readObject(JsonNode jsonNode, Class<T> type);

    public String getBaseUrl();

    public String getInstanceUrl();

    public RestTemplate getRestTemplate();

}

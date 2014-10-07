package org.springframework.social.salesforce.api;

import org.springframework.util.MultiValueMap;

import java.util.Map;

public interface ApexRestOperations {

    <T> T get(String resource, String identifier, Class<T> resultType);

    <T> T get(String resource, Map<String, String> parameters, Class<T> resultType);


    <T> T post(String resource, Object requestData, Class<T> resultType);
}

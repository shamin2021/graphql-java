package org.example;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.Map;

public class ProductDetailsDataFetcher implements DataFetcher<Map<String, Object>> {
    @Override
    public Map<String, Object> get(DataFetchingEnvironment environment) {
        // Retrieve the 'id' argument from the query
        String productId = environment.getArgument("id");

        // For demonstration purposes, return dummy product data
            return Map.of(
                    "id", "2",
                    "description", "Sample Product",
                    "category" , "cat1");
    }
}

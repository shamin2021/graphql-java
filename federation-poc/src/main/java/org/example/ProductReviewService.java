package org.example;

import com.google.common.collect.ImmutableMap;
import com.intuit.graphql.orchestrator.ServiceProvider;
import graphql.ExecutionInput;
import graphql.GraphQLContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

class ProductReviewService implements ServiceProvider {

    String filePath1 = "src/main/resources/Reviews.graphql";
    String filePath2 = "src/main/resources/resolverSchema.graphql";

    public  final String schema;
    public  final String resolverSchema;

    {
        try {
            schema = readFileAsString(filePath1);
            resolverSchema = readFileAsString(filePath2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String getNameSpace() { return "PERSON_ADDRESS";}

    @Override
    public Map<String, String> sdlFiles() { return ImmutableMap.of("schema.graphqls", schema, "resolver.graphqls", resolverSchema);}

    @Override
    public ServiceType getSeviceType() {
        return ServiceType.FEDERATION_SUBGRAPH;
    }

    @Override
    public CompletableFuture<Map<String, Object>> query (final ExecutionInput executionInput, final GraphQLContext context) {
//        Map<String, Object> data = ImmutableMap
//                .of("data", ImmutableMap.of("product", ImmutableMap.of("address", ImmutableMap.of("id",2 , "text","CA", "starRating",2))));
//        System.out.println(data);
//        return CompletableFuture.completedFuture(data);
        return null;

    }

    public static String readFileAsString(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes, StandardCharsets.UTF_8);
        return content;
    }

}
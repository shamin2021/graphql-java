package org.example;

import com.google.common.collect.ImmutableMap;
import com.intuit.graphql.orchestrator.ServiceProvider;
import graphql.ExecutionInput;
import graphql.GraphQLContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

class ProductNameService implements ServiceProvider {

    String filePath1 = "src/main/resources/Product.graphql";

    public String schema;
    {
        try {
            schema = readFileAsString(filePath1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String getNameSpace() { return "PERSON_NAME"; }

    @Override
    public ServiceType getSeviceType() {
        return ServiceType.FEDERATION_SUBGRAPH;
    }

    @Override
    public Map<String, String> sdlFiles() { return ImmutableMap.of("schema.graphqls", schema); }

    @Override
    public CompletableFuture<Map<String, Object>> query(final ExecutionInput executionInput,
                                                        final GraphQLContext context) {
        Map<String, Object> data = ImmutableMap.of("product", ImmutableMap.of("id", 1, "name", "Java","description","hjsaj"));
        System.out.println(data);
        return CompletableFuture.completedFuture(data);
    }

    public static String readFileAsString(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes, StandardCharsets.UTF_8);
        return content;
    }
}
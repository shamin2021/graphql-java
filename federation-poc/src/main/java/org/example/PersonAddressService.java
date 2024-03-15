package org.example;

import com.google.common.collect.ImmutableMap;
import com.intuit.graphql.orchestrator.ServiceProvider;
import com.intuit.graphql.orchestrator.batch.QueryExecutor;
import graphql.ExecutionInput;
import graphql.GraphQLContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

class PersonAddressService implements ServiceProvider {

    private String endpoint;
    private QueryExecutor queryExecutor;
    private String schema;

    public PersonAddressService(String endpoint, WebClient webClient) {
        this.endpoint = endpoint;
        this.queryExecutor = (QueryExecutor) new WebClientQueryExecutor(webClient, endpoint);

        try {
            this.schema = readFileAsString("src/main/resources/Address.graphqls");
        } catch (Exception e) {
            throw new RuntimeException("Error reading schema file", e);
        }
    }

    @Override
    public String getNameSpace() { return "address"; }

    @Override
    public ServiceType getSeviceType() {
        return ServiceType.FEDERATION_SUBGRAPH;
    }

    @Override
    public Map<String, String> sdlFiles() { return ImmutableMap.of("schema.graphqls", schema); }

    @Override
    public CompletableFuture<Map<String, Object>> query(final ExecutionInput executionInput,
                                                        final GraphQLContext context) {
        return queryExecutor.query(executionInput, context);
    }

    public static String readFileAsString(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes, StandardCharsets.UTF_8);
        return content;
    }
}
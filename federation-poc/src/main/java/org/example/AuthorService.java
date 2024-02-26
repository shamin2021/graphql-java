package org.example;

import com.google.common.collect.ImmutableMap;
import com.intuit.graphql.orchestrator.ServiceProvider;
import graphql.ExecutionInput;
import graphql.GraphQLContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AuthorService implements ServiceProvider {

    String filePath1 = "src/main/resources/AuthorSchema.graphqls";

    public String authorSchema;
    {
        try {
            authorSchema = readFileAsString(filePath1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNameSpace() { return "authorService"; }

    @Override
    public ServiceType getSeviceType() {
        return ServiceType.FEDERATION_SUBGRAPH;
    }

    @Override
    public Map<String, String> sdlFiles() { return ImmutableMap.of("schema.graphqls", authorSchema); }

    @Override
    public CompletableFuture<Map<String, Object>> query(final ExecutionInput executionInput,
                                                        final GraphQLContext context) {
        // Simulate author response
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Object> entity = new HashMap<>();
        entity.put("firstName", "Charles");
        entity.put("lastName", "Dickens");
        entities.add(entity);

        Map<String, Object> data = ImmutableMap.of(
                "data", ImmutableMap.of(
                        "_entities", entities
                )
        );

        System.out.println("AUTHOR_QUERY");
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

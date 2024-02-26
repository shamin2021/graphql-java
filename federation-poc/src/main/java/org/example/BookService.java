package org.example;
import com.google.common.collect.ImmutableMap;
import com.intuit.graphql.orchestrator.ServiceProvider;
import graphql.ExecutionInput;
import graphql.GraphQLContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BookService implements ServiceProvider {

    String filePath1 = "src/main/resources/BookSchema.graphqls";

    public String bookSchema;
    {
        try {
            bookSchema = readFileAsString(filePath1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNameSpace() { return "bookService"; }

    @Override
    public ServiceType getSeviceType() {
        return ServiceType.FEDERATION_SUBGRAPH;
    }

    @Override
    public Map<String, String> sdlFiles() { return ImmutableMap.of("schema.graphqls", bookSchema); }

    @Override
    public CompletableFuture<Map<String, Object>> query(final ExecutionInput executionInput,
                                                        final GraphQLContext context) {
        Map<String, Object> data = ImmutableMap
                .of("data", ImmutableMap.of("bookById", ImmutableMap.of("id", "book-1","name","GraphQuilt: The future of Schema Stitching","pageCount","100","author", ImmutableMap.of(
                        "id", "author-1"
                ))));
        System.out.println("BOOK_QUERY");

        System.out.println(data);
        return CompletableFuture.completedFuture(data);
        //return null;
    }

    public static String readFileAsString(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes, StandardCharsets.UTF_8);
        return content;
    }
}

package org.example;

import com.apollographql.federation.graphqljava._Entity;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.intuit.graphql.orchestrator.GraphQLOrchestrator;
import com.intuit.graphql.orchestrator.schema.RuntimeGraph;
import com.intuit.graphql.orchestrator.stitching.SchemaStitcher;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;

import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.*;
import com.apollographql.federation.graphqljava.Federation;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import graphql.schema.DataFetcher;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Test {


    public static void main(String[] args) throws Exception {

//        // create a runtimeGraph by stitching service providers
//        RuntimeGraph runtimeGraph = SchemaStitcher.newBuilder()
//                .service(new PersonNameService())
//                .service(new PersonAddressService())
//                .build()
//                .stitchGraph();
//
//
//        // pass the runtime graph to GraphQLOrchestrator
//        GraphQLOrchestrator graphQLOrchestrator = GraphQLOrchestrator.newOrchestrator()
//                .runtimeGraph(runtimeGraph).build();
//
//        //Execute the query
//        CompletableFuture<ExecutionResult> execute = graphQLOrchestrator
//                .execute(
//                        ExecutionInput.newExecutionInput()
//                                .query("query {person {firstName lastName address { state zip}}}")
//                                .build()
//                );
//
        ExecutionResult executionResult = execute.get();
        System.out.println(executionResult.getData().toString());
        // Output:
        // {person={firstName=GraphQL Orchestrator, lastName=Java, address={city=San Diego, state=CA, zip=92129}}}




        String filePath1 = "src/main/resources/sdl1.graphql";
        String filePath2 = "src/main/resources/sdl2.graphql";

        // Your GraphQL SDL (Schema Definition Language) string
        String sdl1 = readFileAsString(filePath1);
        String sdl2 = readFileAsString(filePath2);
        String queryString = readFileAsString(filePath2);

        SchemaParser parser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry1 = parser.parse(sdl1);
        TypeDefinitionRegistry typeDefinitionRegistry2 = parser.parse(sdl2);

        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();


        RuntimeWiring runtimeWiring1 = RuntimeWiring.newRuntimeWiring().type("Query", builder -> builder
                        .dataFetcher("product", new ProductDataFetcher())
                )
                .build();

        RuntimeWiring runtimeWiring2 = RuntimeWiring.newRuntimeWiring().type("Query", builder -> builder
                        .dataFetcher("productDetails", new ProductDetailsDataFetcher())
                )
                .build();

        GraphQLSchema transformedSchema1 = Federation.transform(typeDefinitionRegistry1, runtimeWiring1).build();
        GraphQLSchema transformedSchema2 = Federation.transform(typeDefinitionRegistry1, runtimeWiring2).build();

        RuntimeGraph runtimeGraph = SchemaStitcher.newBuilder()
                .service("https://mocki.io/v1/d4867d8b-b5d5-4a48-a4ab-79131b5809b8")
                .service(new PersonAddressService())
                .build()
                .stitchGraph();

        TypeDefinitionRegistry mergedRegistry = new TypeDefinitionRegistry();


        DataFetcher entityDataFetcher = env -> {
            List<Map<String, Object>> representations = env.getArgument(_Entity.argumentName);
            return representations.stream()
                    .map(representation -> {
                        if ("Product".equals(representation.get("__typename"))) {
                            return new Product((String)representation.get("id"));
                        }
                        return null;
                    })
                    .collect(Collectors.toList());
        };
        TypeResolver entityTypeResolver = env -> {
            final Object src = env.getObject();
            if (src instanceof Product) {
                return env.getSchema()
                        .getObjectType("Product");
            }
            return null;
        };
        GraphQLSchema federatedSchema = Federation.transform(typeDefinitionRegistry, runtimeWiring1)
                .fetchEntities(entityDataFetcher)
                .resolveEntityType(entityTypeResolver)
                .build();

        System.out.print(federatedSchema);


        // Create a GraphQL instance with instrumentation for tracing
        GraphQL graphQL = GraphQL.newGraphQL(federatedSchema)
                .instrumentation(new TracingInstrumentation()) // Enable tracing for the query plan
                .build();

        // Your GraphQL query string
        String query = " query {\n" +
                "  product(id: 2) {\n" +
                "    id\n" +
                "    name\n" +
                "    productDetails {\n" +
                "      id\n" +
                "      description\n" +
                "      category\n" +
                "    }\n" +
                "  }\n" +
                "}\n ";

        // Execute the query and get the result
        ExecutionResult executionResult = graphQL.execute(query);

        ObjectMapper objectMapper = new ObjectMapper();
        String jacksonData = objectMapper.writeValueAsString(executionResult.toSpecification());
        System.out.println(jacksonData);

        // Print the query plan (tracing)
        Map<Object, Object> extensions = executionResult.getExtensions();
        if (extensions != null) {
            Map<String, Object> tracing = (Map<String, Object>) extensions.get("tracing");
            if (tracing != null) {
                System.out.println("Query Plan:");
                System.out.println(tracing.get("execution"));
            }
        }
    }
    private static String readFileAsString(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes, StandardCharsets.UTF_8);
        return content;
    }
}

package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.intuit.graphql.orchestrator.GraphQLOrchestrator;
import com.intuit.graphql.orchestrator.ServiceProvider;
import com.intuit.graphql.orchestrator.schema.RuntimeGraph;
import com.intuit.graphql.orchestrator.stitching.SchemaStitcher;
import com.intuit.graphql.orchestrator.stitching.Stitcher;
import com.intuit.graphql.orchestrator.stitching.XtextStitcher;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import com.intuit.graphql.orchestrator.ServiceProvider.ServiceType;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.util.concurrent.CompletableFuture;

import static org.example.TestHelper.getFileMapFromList;
import static org.springframework.web.reactive.function.client.WebClient.*;

public class Test3 {


    private static final WebClient webClient = create();
    private static final Stitcher stitcher = XtextStitcher.newBuilder().build();
    private ExecutionInput executionInput;
    private static Map<String, Object> executionResult;

    public static void main(String[] args) throws Exception {

//        ServiceProvider provider1 = serviceProvider("http://localhost:9091/graphql", "service1",
//                TestHelper.getFileMapFromList("Address.graphqls"));
//        ServiceProvider provider2 = serviceProvider("http://localhost:9092/graphql", "service2",
//                getFileMapFromList("Person.graphqls"));
//        List<ServiceProvider> serviceContextList = Arrays.asList(provider1, provider2);
//
//        RuntimeGraph runtimeGraph = SchemaStitcher.newBuilder()
//                .services(serviceContextList)
//                .build()
//                .stitchGraph();
//
//        // pass the runtime graph to GraphQLOrchestrator
//        GraphQLOrchestrator graphQLOrchestrator = GraphQLOrchestrator.newOrchestrator()
//                .runtimeGraph(runtimeGraph).build();
//
//        String grastrator = new SchemaPrinter().print(runtimeGraph.getExecutableSchema());
//        System.out.println(grastrator);
////
////        RuntimeGraph runtimeGraph = stitcher.stitch(serviceContextList);
////        final GraphQLSchema graphQLSchema = runtimeGraph.getExecutableSchema();

        PersonNameService personNameService = new PersonNameService("http://localhost:8080/graphql", webClient);
        PersonAddressService personAddressService = new PersonAddressService("http://localhost:8081/graphql", webClient);

        // create a runtimeGraph by stitching service providers
        RuntimeGraph runtimeGraph = SchemaStitcher.newBuilder()
                .service(personNameService)
                .service(personAddressService)
                .build()
                .stitchGraph();
//
        // pass the runtime graph to GraphQLOrchestrator
        GraphQLOrchestrator.Builder graphQLOrchestrator = GraphQLOrchestrator.newOrchestrator()
                .runtimeGraph(runtimeGraph);

        ExecutionStrategy queryExecutionStrategy = new AsyncExecutionStrategy();
        graphQLOrchestrator.queryExecutionStrategy(queryExecutionStrategy);

        GraphQLOrchestrator grapgqh = graphQLOrchestrator.build();

//        String grastrator = new SchemaPrinter().print(runtimeGraph.getExecutableSchema());
//        System.out.println(grastrator);
////
//        //Execute the query
        ExecutionInput.Builder eiBuilder = ExecutionInput.newExecutionInput();
        eiBuilder.query(" query { getPersonById(firstName:\"Tony\"){firstName}}");

        executionResult =  grapgqh.execute(eiBuilder).get().toSpecification();

        System.out.println(executionResult);

//        CompletableFuture<ExecutionResult> execute = graphQLOrchestrator
//                .execute(
//                        ExecutionInput.newExecutionInput()
//                                .query(" query { getPersons(firstName:\"Tony\"){firstName}}")
//                                .build()
//                );
////
//        ExecutionResult executionResult = execute.get();
//        System.out.println(executionResult);
//        // Output:
//        // {person={firstName=GraphQL Orchestrator, lastName=Java, address={city=San Diego, state=CA, zip=92129}}}

    }
    private static ServiceProvider serviceProvider(String url, String namespace, Map<String, String> sdlFiles) {
        return serviceProvider(url, namespace, sdlFiles, ServiceType.FEDERATION_SUBGRAPH);
    }
    private static ServiceProvider serviceProvider(String url, String namespace, Map<String, String> sdlFiles, ServiceType serviceType) {
        return TestServiceProvider.newBuilder().namespace(namespace).sdlFiles(sdlFiles).serviceType(serviceType).build();
    }
}

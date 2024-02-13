package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.intuit.graphql.orchestrator.GraphQLOrchestrator;
import com.intuit.graphql.orchestrator.schema.RuntimeGraph;
import com.intuit.graphql.orchestrator.stitching.SchemaStitcher;
import graphql.ExecutionInput;
import graphql.ExecutionResult;

import graphql.schema.idl.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import java.util.concurrent.CompletableFuture;

public class Test {


    public static void main(String[] args) throws Exception {

        // create a runtimeGraph by stitching service providers
        RuntimeGraph runtimeGraph = SchemaStitcher.newBuilder()
                .service(new ProductNameService())
                .service(new ProductReviewService())
                .build()
                .stitchGraph();

        String schema = new SchemaPrinter().print(runtimeGraph.getExecutableSchema());


        // pass the runtime graph to GraphQLOrchestrator
        GraphQLOrchestrator graphQLOrchestrator = GraphQLOrchestrator.newOrchestrator()
                .runtimeGraph(runtimeGraph).build();

        // Print SDL of supergraph
        String grastrator = new SchemaPrinter().print(graphQLOrchestrator.getSchema());
        System.out.println(grastrator);

        //Execute the query
        CompletableFuture<ExecutionResult> execute = graphQLOrchestrator
                .execute(
                        ExecutionInput.newExecutionInput()
                                .query( "query { getBar (barId: \"1\"){id} }")
                                .build()
                );

        //get execution result and print
        ExecutionResult executionResult = execute.get();
        System.out.println("--------------------------------------");
        System.out.println(executionResult);
    }

}

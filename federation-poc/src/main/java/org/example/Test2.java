package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.intuit.graphql.orchestrator.GraphQLOrchestrator;
import com.intuit.graphql.orchestrator.schema.RuntimeGraph;
import com.intuit.graphql.orchestrator.stitching.SchemaStitcher;
import graphql.ExecutionInput;
import graphql.ExecutionResult;

import graphql.schema.GraphQLDirective;
import graphql.schema.idl.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import java.util.concurrent.CompletableFuture;

public class Test2 {


    public static void main(String[] args) throws Exception {

        // create a runtimeGraph by stitching service providers
        RuntimeGraph runtimeGraph = SchemaStitcher.newBuilder()
                .service(new AuthorService())
                .service(new BookService())
                .build()
                .stitchGraph();

//        String schema = new SchemaPrinter().print(runtimeGraph.getExecutableSchema());


        // pass the runtime graph to GraphQLOrchestrator
        GraphQLOrchestrator graphQLOrchestrator = GraphQLOrchestrator.newOrchestrator()
                .runtimeGraph(runtimeGraph).build();

//        // Print SDL of supergraph
        String grastrator = new SchemaPrinter().print(runtimeGraph.getExecutableSchema());
        System.out.println(grastrator);

//        // Assuming you have a runtimeGraph object with schema directives
//        Map<String, GraphQLDirective> schemaDirectives = runtimeGraph.getSchemaDirectives();
//
//        // Create a SchemaPrinter instance
//        SchemaPrinter schemaPrinter = new SchemaPrinter();
//
//        // Iterate over the map entries and print each directive individually
//        for (Map.Entry<String, GraphQLDirective> entry : schemaDirectives.entrySet()) {
//            String directiveName = entry.getKey();
//            GraphQLDirective directive = entry.getValue();
//            String directiveString = schemaPrinter.print(directive);
//            System.out.println("Directive name: " + directiveName);
//            System.out.println("Directive string: " + directiveString);
//        }

        //Execute the query
        CompletableFuture<ExecutionResult> execute = graphQLOrchestrator
                .execute(
                        ExecutionInput.newExecutionInput()
                                .query("query { bookById(id: \"book-1\") { id author { id firstName } } }")
                                .build()
                );

        //get execution result and print
        ExecutionResult executionResult = execute.get();
        System.out.println("--------------------------------------");
        System.out.println(executionResult);
    }

}

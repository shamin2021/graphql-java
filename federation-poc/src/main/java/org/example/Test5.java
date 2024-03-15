package org.example;

import com.intuit.graphql.orchestrator.GraphQLOrchestrator;
import com.intuit.graphql.orchestrator.schema.RuntimeGraph;
import com.intuit.graphql.orchestrator.stitching.SchemaStitcher;
import graphql.ExecutionInput;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.schema.idl.SchemaPrinter;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.springframework.web.reactive.function.client.WebClient.create;

public class Test5 {


    private static final WebClient webClient = create();
    private ExecutionInput executionInput;
    private static Map<String, Object> executionResult;

    public static void main(String[] args) throws Exception {

        ExecutionStrategy queryExecutionStrategy = new AsyncExecutionStrategy();
        Map<String, Object> executionResult;

        PersonNameService personNameService = new PersonNameService("http://localhost:9091/graphql", webClient);
        PersonAddressService personAddressService = new PersonAddressService("http://localhost:9092/graphql", webClient);

        // create a runtimeGraph by stitching service providers
        RuntimeGraph runtimeGraph = SchemaStitcher.newBuilder()
                .service(personNameService)
                .service(personAddressService)
                .build()
                .stitchGraph();

        GraphQLOrchestrator.Builder builder = GraphQLOrchestrator.newOrchestrator();
        builder.runtimeGraph(runtimeGraph);
        builder.queryExecutionStrategy(queryExecutionStrategy);
        GraphQLOrchestrator graphQLOrchestrator = builder.build();

        String printSchema = new SchemaPrinter().print(runtimeGraph.getExecutableSchema());
        System.out.println(printSchema);

        ExecutionInput.Builder eiBuilder = ExecutionInput.newExecutionInput();
        eiBuilder.query(" query { getPersons{lastName address{state}}}");
        ExecutionInput executionInput = eiBuilder.build();

        executionResult = graphQLOrchestrator.execute(executionInput).get().toSpecification();

        System.out.println(executionResult);
    }
}

//{
//shows {
//    title
//    reviews {
//        starRating
//    }
//}
//}

package org.example;

import com.intuit.graphql.orchestrator.GraphQLOrchestrator;
import com.intuit.graphql.orchestrator.ServiceProvider;
import com.intuit.graphql.orchestrator.ServiceProvider.ServiceType;
import com.intuit.graphql.orchestrator.schema.RuntimeGraph;
import com.intuit.graphql.orchestrator.stitching.SchemaStitcher;
import com.intuit.graphql.orchestrator.stitching.Stitcher;
import com.intuit.graphql.orchestrator.stitching.XtextStitcher;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionIdProvider;
import graphql.execution.ExecutionStrategy;
import graphql.schema.idl.SchemaPrinter;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.springframework.web.reactive.function.client.WebClient.create;

public class Test4 {


    private static final WebClient webClient = create();
    private ExecutionInput executionInput;
    private static Map<String, Object> executionResult;

    public static void main(String[] args) throws Exception {

        ExecutionStrategy queryExecutionStrategy = new AsyncExecutionStrategy();
        Map<String, Object> executionResult;

        ShowsService showsService = new ShowsService("http://localhost:8080/graphql", webClient);
        ReviewsService reviewsService = new ReviewsService("http://localhost:8081/graphql", webClient);

        // create a runtimeGraph by stitching service providers
        RuntimeGraph runtimeGraph = SchemaStitcher.newBuilder()
                .service(showsService)
                .service(reviewsService)
                .build()
                .stitchGraph();

        GraphQLOrchestrator.Builder builder = GraphQLOrchestrator.newOrchestrator();
        builder.runtimeGraph(runtimeGraph);
        builder.queryExecutionStrategy(queryExecutionStrategy);
        GraphQLOrchestrator graphQLOrchestrator = builder.build();

        String printSchema = new SchemaPrinter().print(runtimeGraph.getExecutableSchema());
        System.out.println(printSchema);

        ExecutionInput.Builder eiBuilder = ExecutionInput.newExecutionInput();
        eiBuilder.query(" query { shows{title id reviews {starRating}}}");
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

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

public class Test6 {


    private static final WebClient webClient = create();
    private ExecutionInput executionInput;
    private static Map<String, Object> executionResult;

    public static void main(String[] args) throws Exception {

        ExecutionStrategy queryExecutionStrategy = new AsyncExecutionStrategy();
        Map<String, Object> executionResult;

        WebClient webClient = WebClient.create();

        GenericProvider inventoryService = new GenericProvider("http://localhost:5004/graphql", webClient, "src/main/resources/Inventory.graphqls", "inevntory");
        GenericProvider userService = new GenericProvider("http://localhost:5001/graphql", webClient, "src/main/resources/User.graphqls","User");
        GenericProvider reviewService = new GenericProvider("http://localhost:5002/graphql", webClient, "src/main/resources/ReviewN.graphqls","review");
        GenericProvider productService  = new GenericProvider("http://localhost:5003/graphql", webClient, "src/main/resources/Product.graphqls","product");


        // create a runtimeGraph by stitching service providers
        RuntimeGraph runtimeGraph = SchemaStitcher.newBuilder()
                .service(userService)
                .service(reviewService)
                .service(productService)
                .service(inventoryService)
                .build()
                .stitchGraph();

        GraphQLOrchestrator.Builder builder = GraphQLOrchestrator.newOrchestrator();
        builder.runtimeGraph(runtimeGraph);
        builder.queryExecutionStrategy(queryExecutionStrategy);
        GraphQLOrchestrator graphQLOrchestrator = builder.build();

        String printSchema = new SchemaPrinter().print(runtimeGraph.getExecutableSchema());
        System.out.println(printSchema);

        ExecutionInput.Builder eiBuilder = ExecutionInput.newExecutionInput();
        eiBuilder.query(" query { topProducts { name reviews { author {id name address{city}}}}}");
        ExecutionInput executionInput = eiBuilder.build();

        executionResult = graphQLOrchestrator.execute(executionInput).get().toSpecification();

        System.out.println(executionResult);
    }
}


package org.example;

import com.google.common.collect.ImmutableMap;
import com.intuit.graphql.orchestrator.ServiceProvider;
import graphql.ExecutionInput;
import graphql.GraphQLContext;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

class PersonNameService implements ServiceProvider {

    public static final String schema =
            "type Query { person: Person } "
                    + "type Person { firstName : String lastName: String }";

    @Override
    public String getNameSpace() { return "PERSON_NAME"; }

    @Override
    public Map<String, String> sdlFiles() { return ImmutableMap.of("schema.graphqls", schema); }

    @Override
    public CompletableFuture<Map<String, Object>> query(final ExecutionInput executionInput,
                                                        final GraphQLContext context) {
        //{'data':{'person':{'firstName':'GraphQL Orchestrator', 'lastName': 'Java'}}}"
        Map<String, Object> data = ImmutableMap
                .of("data", ImmutableMap.of("person", ImmutableMap.of("firstName", "Shamin", "lastName", "Fernando")));
        return CompletableFuture.completedFuture(data);
    }
}
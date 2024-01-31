package org.example;

import com.google.common.collect.ImmutableMap;
import com.intuit.graphql.orchestrator.ServiceProvider;
import graphql.ExecutionInput;
import graphql.GraphQLContext;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

class PersonAddressService implements ServiceProvider {

    public static final String schema =
            "type Query { person: Person }"
                    + "type Person { address : Address }"
                    + "type Address { city: String state: String zip: String}";

    @Override
    public String getNameSpace() { return "PERSON_ADDRESS";}

    @Override
    public Map<String, String> sdlFiles() { return ImmutableMap.of("schema.graphqls", schema);}

    @Override
    public CompletableFuture<Map<String, Object>> query(final ExecutionInput executionInput,
                                                        final GraphQLContext context) {
        //{'data':{'person':{'address':{ 'city' 'San Diego', 'state': 'CA', 'zip': '92129' }}}}"
        Map<String, Object> data = ImmutableMap
                .of("data", ImmutableMap.of("person", ImmutableMap.of("address", ImmutableMap.of("city","San Diego", "state","CA", "zip","92129"))));
        return CompletableFuture.completedFuture(data);
    }
}
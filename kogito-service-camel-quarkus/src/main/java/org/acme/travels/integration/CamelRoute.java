package org.acme.travels.integration;


import org.acme.travels.User;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;

public class CamelRoute extends RouteBuilder {
    @Override
    public void configure() {
        onException(HttpOperationFailedException.class)
            .setBody(simple("null"))
            .continued(true);
        
        from("direct:start")
            .setHeader("CamelHttpMethod", constant("GET"))
            .toD("http:petstore.swagger.io/v2/user/${body}")
            .unmarshal().json(JsonLibrary.Jackson, User.class);            
    }
}

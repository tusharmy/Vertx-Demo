package com.myntra.vertx.demo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.util.LinkedHashMap;
import java.util.Map;
public class MyFirstVerticle extends AbstractVerticle {
    // Store our product
    private Map<Integer, Msg> messages = new LinkedHashMap<>();
    // Create some product
    private void createSomeData() {
        Msg first = new Msg("Hello World!", "localhost");
        messages.put(first.getId(), first);
        Msg second = new Msg("The person who coined the term coined the term coined the term coined the term.", "localhost");
        messages.put(second.getId(), second);
    }
    // On  verticle start -> this method gets called.
    @Override
    public void start(Future<Void> fut) {
        createSomeData();
        // Create a router object.
        Router router = Router.router(vertx);
        // Bind "/" to our hello message - so we are still compatible.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Hello from my first Vert.x 4 application</h1>");
        });
        // Serve static resources from the /assets directory
        router.route("/assets/*").handler(StaticHandler.create("assets"));
        router.get("/api/msgs").handler(this::getAll);
        router.route("/api/msgs*").handler(BodyHandler.create());
        router.post("/api/msgs").handler(this::addOne);
        router.delete("/api/msgs/:id").handler(this::deleteOne);
        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }
    private void getAll(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(messages.values()));
    }
    private void addOne(RoutingContext routingContext) {
        final Msg message = Json.decodeValue(routingContext.getBodyAsString(),
                Msg.class);
        messages.put(message.getId(), message);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(message));
    }
    private void deleteOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            messages.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }
}
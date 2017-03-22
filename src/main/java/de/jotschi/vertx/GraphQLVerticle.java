package de.jotschi.vertx;

import static graphql.GraphQL.newGraphQL;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.http.HttpMethod.GET;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gentics.ferma.NoTrx;

import de.jotschi.vertx.data.StarWarsData;
import de.jotschi.vertx.data.StarWarsSchema;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class GraphQLVerticle extends AbstractVerticle {

	private static final Logger log = LoggerFactory.getLogger(GraphQLVerticle.class);

	private StarWarsData demoData = new StarWarsData(vertx);

	@Override
	public void start() throws Exception {
		Router router = Router.router(vertx);
		router.route("/")
				.handler(rc -> {
					rc.request()
							.bodyHandler(rh -> {
								String query = rh.toString();
								handleQuery(rc, query);
							});
				});

		StaticHandler staticHandler = StaticHandler.create("graphiql");
		staticHandler.setDirectoryListing(false);
		staticHandler.setCachingEnabled(false);
		staticHandler.setIndexPage("index.html");
		router.route("/browser/*")
				.method(GET)
				.handler(staticHandler);

		// Redirect handler
		router.route("/browser")
				.method(GET)
				.handler(rc -> {
					if ("/browser".equals(rc.request()
							.path())) {
						rc.response()
								.setStatusCode(302);
						rc.response()
								.headers()
								.set("Location", rc.request()
										.path() + "/");
						rc.response()
								.end();
					} else {
						rc.next();
					}
				});

		vertx.createHttpServer()
				.requestHandler(router::accept)
				.listen(3000);

	}

	private void handleQuery(RoutingContext rc, String json) {
		log.info("Handling query {" + json + "}");

		ExecutionResult result = null;
		try (NoTrx noTrx = demoData.getGraph()
				.noTrx()) {
			JsonObject queryJson = new JsonObject(json);
			String query = queryJson.getString("query");
			GraphQL graphQL = newGraphQL(new StarWarsSchema().getStarWarsSchema()).build();
			result = graphQL.execute(query, demoData.getRoot());
			List<GraphQLError> errors = result.getErrors();
			JsonObject response = new JsonObject();
			if (!errors.isEmpty()) {
				log.error("Could not execute query {" + query + "}");
				JsonArray jsonErrors = new JsonArray();
				response.put("errors", jsonErrors);
				for (GraphQLError error : errors) {
					JsonObject jsonError = new JsonObject();
					jsonError.put("message", error.getMessage());
					jsonError.put("type", error.getErrorType());
					if (error.getLocations() != null || !error.getLocations()
							.isEmpty()) {
						JsonArray errorLocations = new JsonArray();
						jsonError.put("locations", errorLocations);
						for (SourceLocation location : error.getLocations()) {
							JsonObject errorLocation = new JsonObject();
							errorLocation.put("line", location.getLine());
							errorLocation.put("column", location.getLine());
							errorLocations.add(errorLocation);
						}
					}
					jsonErrors.add(jsonError);
				}
			}
			if (result.getData() != null) {
				Map<String, Object> data = (Map<String, Object>) result.getData();
				response.put("data", new JsonObject(Json.encode(data)));
			}
			HttpResponseStatus statusCode = result.getErrors() != null ? BAD_REQUEST : OK;

			rc.response()
					.putHeader("Content-Type", "application/json");
			rc.response()
					.setStatusCode(statusCode.code());
			rc.response()
					.end(response.toString());
		}

	}
}

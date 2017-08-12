package de.jotschi.vertx;

import static graphql.GraphQL.newGraphQL;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.http.HttpMethod.GET;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.jotschi.vertx.data.StarWarsData;
import de.jotschi.vertx.data.StarWarsSchema;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.schema.GraphQLSchema;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
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

	private StarWarsData demoData;

	private GraphQLSchema schema = new StarWarsSchema().getStarWarsSchema();

	@Override
	public void start() throws Exception {
		Router router = Router.router(vertx);
		router.route("/").handler(rc -> {
			rc.request().bodyHandler(rh -> {
				String query = rh.toString();
				handleQuery(rc, query);
			});
		});

		StaticHandler staticHandler = StaticHandler.create("graphiql");
		staticHandler.setDirectoryListing(false);
		staticHandler.setCachingEnabled(false);
		staticHandler.setIndexPage("index.html");
		router.route("/browser/*").method(GET).handler(staticHandler);

		// Redirect handler
		router.route("/browser").method(GET).handler(rc -> {
			if ("/browser".equals(rc.request().path())) {
				rc.response().setStatusCode(302);
				rc.response().headers().set("Location", rc.request().path() + "/");
				rc.response().end();
			} else {
				rc.next();
			}
		});

		vertx.createHttpServer().requestHandler(router::accept).listen(3000);
		demoData = new StarWarsData(vertx);

	}

	/**
	 * Handle the graphql query.
	 * 
	 * @param rc
	 * @param json
	 */
	private void handleQuery(RoutingContext rc, String json) {
		log.info("Handling query {" + json + "}");
		// The graphql query is transmitted within a JSON string
		JsonObject queryJson = new JsonObject(json);
		String query = queryJson.getString("query");
		demoData.getGraph().asyncTx((tx) -> {
			// Invoke the query and handle the resulting JSON
			GraphQL graphQL = newGraphQL(schema).build();
			tx.complete(graphQL.execute(query, demoData.getRoot(), extractVariables(queryJson)));
		}, (AsyncResult<ExecutionResult> rh) -> {
			if (rh.failed()) {
				rc.fail(rh.cause());
				return;
			}

			ExecutionResult result = rh.result();
			List<GraphQLError> errors = result.getErrors();
			JsonObject response = new JsonObject();
			// Check whether the query has returned any errors. We need to add those to the response as well.
			if (!errors.isEmpty()) {
				log.error("Could not execute query {" + query + "}");
				JsonArray jsonErrors = new JsonArray();
				response.put("errors", jsonErrors);
				for (GraphQLError error : errors) {
					JsonObject jsonError = new JsonObject();
					jsonError.put("message", error.getMessage());
					jsonError.put("type", error.getErrorType());
					if (error.getLocations() != null || !error.getLocations().isEmpty()) {
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
			HttpResponseStatus statusCode = (result.getErrors() != null && !result.getErrors().isEmpty()) ? BAD_REQUEST : OK;

			rc.response().putHeader("Content-Type", "application/json");
			rc.response().setStatusCode(statusCode.code());
			rc.response().end(response.toString());
		});
	}

	/**
	 * Extracts the variables of a query as a map. Returns empty map if no variables are found.
	 *
	 * @param request
	 *            The request body
	 * @return GraphQL variables
	 */
	private Map<String, Object> extractVariables(JsonObject request) {
		JsonObject variables = request.getJsonObject("variables");
		if (variables == null) {
			return Collections.emptyMap();
		} else {
			return variables.getMap();
		}
	}
}

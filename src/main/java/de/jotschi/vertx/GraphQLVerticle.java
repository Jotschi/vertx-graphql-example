package de.jotschi.vertx;

import static io.vertx.core.http.HttpMethod.GET;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gentics.ferma.NoTrx;

import de.jotschi.vertx.data.StarWarsData;
import de.jotschi.vertx.data.StarWarsSchema;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import io.vertx.core.AbstractVerticle;
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
			result = new GraphQL(new StarWarsSchema().getStarWarsSchema()).execute(query, demoData.getRoot());
		}
		List<GraphQLError> errors = result.getErrors();
		if (!errors.isEmpty()) {
			log.error("Could not execute query {" + json + "}");
			for (GraphQLError error : errors) {
				if (error.getLocations() == null || error.getLocations()
						.isEmpty()) {
					log.error(error.getErrorType() + " " + error.getMessage());
				} else {
					for (SourceLocation location : error.getLocations()) {
						log.error(error.getErrorType() + " " + error.getMessage() + " " + location.getColumn() + ":" + location.getLine());
					}
				}
			}
			rc.response()
					.setStatusCode(400)
					.end("Query could not be executed");
		} else {
			Map<String, Object> data = (Map<String, Object>) result.getData();
			JsonObject response = new JsonObject();
			try {
				response.put("data", new JsonObject(new ObjectMapper().writeValueAsString(data)));
				rc.response()
						.putHeader("Content-Type", "application/json");
				rc.response()
						.end(response.toString());
				;
			} catch (JsonProcessingException e) {
				log.error("Error while handling response data", e);
				rc.response()
						.setStatusCode(500)
						.end("Query could not be executed");
			}
		}
	}
}

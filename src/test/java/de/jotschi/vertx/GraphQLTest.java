package de.jotschi.vertx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonObject;

public class GraphQLTest {

	private static Vertx vertx = null;

	private static HttpClient client;

	@BeforeClass
	public static void setup() {
		vertx = Vertx.vertx();
		vertx.deployVerticle(new GraphQLVerticle());
		client = vertx.createHttpClient();
	}

	@Test
	public void testQuery() throws InterruptedException, IOException, ExecutionException {
		String query = readQuery("full-query");
		invokeQuery(query);
	}

	@Test
	public void testBogusQuery() throws InterruptedException, ExecutionException {
		String query = "{bogus}";
		JsonObject json = invokeQuery(query);
		assertNotNull(json.getJsonArray("errors"));
		JsonObject error = json.getJsonArray("errors")
				.getJsonObject(0);
		assertEquals(1, error.getJsonArray("locations")
				.getJsonObject(0)
				.getInteger("line")
				.intValue());
		assertEquals(1, error.getJsonArray("locations")
				.getJsonObject(0)
				.getInteger("column")
				.intValue());
		System.out.println(error.encodePrettily());
	}

	private JsonObject invokeQuery(String query) throws InterruptedException, ExecutionException {
		HttpClientRequest request = client.post(3000, "localhost", "/");

		CompletableFuture<JsonObject> fut = new CompletableFuture<>();
		request.handler(rh -> {
			rh.bodyHandler(bh -> {
				fut.complete(new JsonObject(bh.toString()));
			});
		});
		request.end(new JsonObject().put("query", query)
				.toString());
		return fut.get();
	}

	private String readQuery(String queryName) throws IOException {
		return IOUtils.toString(getClass().getResourceAsStream("/" + queryName), StandardCharsets.UTF_8);
	}
}

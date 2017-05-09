package de.jotschi.vertx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;

import de.jotschi.vertx.util.QueryException;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonObject;

public abstract class AbstractTest {

	protected static Vertx vertx = null;

	protected static HttpClient client;

	@BeforeClass
	public static void setup() throws InterruptedException {
		vertx = Vertx.vertx();
		CountDownLatch latch = new CountDownLatch(1);
		vertx.deployVerticle(new GraphQLVerticle(), (e) -> {
			latch.countDown();
		});
		latch.await();
		client = vertx.createHttpClient();
	}

	/**
	 * Invoke the query and return the result JSON.
	 * 
	 * @param query
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	protected JsonObject invokeQuery(String query) throws InterruptedException, ExecutionException, QueryException {
		return invokeFullQuery(new JsonObject().put("query", query));
	}

	/**
	 * Invoke a full graphql query which may also contain fragments and variables.
	 * 
	 * @param body
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	protected JsonObject invokeFullQuery(JsonObject body) throws InterruptedException, ExecutionException {
		HttpClientRequest request = client.post(3000, "localhost", "/");

		CompletableFuture<JsonObject> fut = new CompletableFuture<>();
		request.handler(rh -> {

			rh.bodyHandler(bh -> {
				if (rh.statusCode() == 200) {
					fut.complete(new JsonObject(bh.toString()));
				} else {
					fut.completeExceptionally(new QueryException(rh.statusCode(), bh.toString()));
				}
			});
		});
		request.end(body.toString());
		return fut.get();

	}

	/**
	 * Load the query with the given name from the test resources.
	 * 
	 * @param queryName
	 * @return
	 * @throws IOException
	 */
	protected String readQuery(String queryName) throws IOException {
		InputStream ins = getClass().getResourceAsStream("/graphql/" + queryName);
		Objects.requireNonNull(ins, "The query {" + queryName + "} could not be found");
		return IOUtils.toString(ins, StandardCharsets.UTF_8);
	}

}

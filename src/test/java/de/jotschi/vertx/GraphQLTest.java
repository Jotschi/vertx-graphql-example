package de.jotschi.vertx;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonObject;

public class GraphQLTest {

	@Test
	public void testQuery() throws InterruptedException, IOException {
		VertxOptions options = new VertxOptions();
		Vertx vertx = Vertx.vertx(options);
		vertx.deployVerticle(new GraphQLVerticle());

		HttpClient client = vertx.createHttpClient();
		HttpClientRequest request = client.post(3000, "localhost", "/query");
		CountDownLatch latch = new CountDownLatch(1);
		request.handler(rh -> {
			rh.bodyHandler(bh -> {
				JsonObject jsonObject = new JsonObject(bh.toString());
				System.out.println(jsonObject.encodePrettily());
				latch.countDown();
			});
		});
		String query = readQuery("full-query");
		request.end(query);
		latch.await();
	}

	private String readQuery(String queryName) throws IOException {
		return IOUtils.toString(getClass().getResourceAsStream("/" + queryName), StandardCharsets.UTF_8);
	}
}

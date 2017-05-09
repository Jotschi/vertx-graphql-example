package de.jotschi.vertx;

import static de.jotschi.vertx.util.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import de.jotschi.vertx.util.QueryException;
import io.vertx.core.json.JsonObject;

public class BasicGraphQLTest extends AbstractTest {

	@Test
	public void testBogusQuery() throws InterruptedException, ExecutionException, QueryException {
		String query = "{bogus}";
		try {
			invokeQuery(query);
			fail("The query should fail");
		} catch (ExecutionException e) {
			QueryException q = (QueryException) e.getCause();
			JsonObject json = q.getJson();
			assertNotNull(json.getJsonArray("errors"));
			JsonObject error = json.getJsonArray("errors").getJsonObject(0);
			assertEquals(1, error.getJsonArray("locations").getJsonObject(0).getInteger("line").intValue());
			assertEquals(1, error.getJsonArray("locations").getJsonObject(0).getInteger("column").intValue());
			System.out.println(error.encodePrettily());
		}
	}

	@Test
	public void testQueryVariable() throws IOException, InterruptedException, ExecutionException {
		String query = readQuery("variable-query");
		JsonObject json = new JsonObject();
		json.put("query", query);
		json.put("variables", new JsonObject().put("id", 1001));
		JsonObject response = invokeFullQuery(json);
		System.out.println(response.encodePrettily());
		assertThat(response).compliesToAssertions("variable-query");

	}
}

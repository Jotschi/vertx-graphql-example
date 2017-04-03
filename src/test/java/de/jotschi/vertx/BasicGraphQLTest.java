package de.jotschi.vertx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import io.vertx.core.json.JsonObject;

public class BasicGraphQLTest extends AbstractTest {

	@Test
	public void testBogusQuery() throws InterruptedException, ExecutionException {
		String query = "{bogus}";
		JsonObject json = invokeQuery(query);
		assertNotNull(json.getJsonArray("errors"));
		JsonObject error = json.getJsonArray("errors").getJsonObject(0);
		assertEquals(1, error.getJsonArray("locations").getJsonObject(0).getInteger("line").intValue());
		assertEquals(1, error.getJsonArray("locations").getJsonObject(0).getInteger("column").intValue());
		System.out.println(error.encodePrettily());
	}
}

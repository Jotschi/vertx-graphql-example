package de.jotschi.vertx;

import static de.jotschi.vertx.util.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.vertx.core.json.JsonObject;

@RunWith(Parameterized.class)
public class GraphQLTest extends AbstractTest {

	private final String queryName;

	public GraphQLTest(String queryName) {
		this.queryName = queryName;
	}

	@Parameters(name = "query={0}")
	public static List<String> paramData() {
		List<String> testQueries = new ArrayList<>();
		testQueries.add("full-query");
		testQueries.add("simple-query");
		return testQueries;
	}

	@Test
	public void testQuery() throws Exception {
		String query = readQuery(queryName);
		JsonObject response = invokeQuery(query);
		System.out.println(response.encodePrettily()); 
		assertThat(response).compliesToAssertions(queryName);
	}

}

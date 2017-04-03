package de.jotschi.vertx.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.assertj.core.api.AbstractAssert;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import io.vertx.core.json.JsonObject;

public class JsonObjectAssert extends AbstractAssert<JsonObjectAssert, JsonObject> {
	/**
	 * Key to check
	 */
	protected String key;

	public JsonObjectAssert(JsonObject actual) {
		super(actual, JsonObjectAssert.class);
	}

	public JsonObjectAssert key(String key) {
		this.key = key;
		return this;
	}

	public JsonObjectAssert matches(Object expected) {
		assertNotNull(descriptionText() + " cannot be matched without specifying key first", key);
		assertNotNull(descriptionText() + " JsonObject must not be null", actual);
		assertEquals(descriptionText() + " key " + key, expected, actual.getValue(key));
		return this;
	}

	public JsonObjectAssert has(String path, String value, String msg) {
		try {
			Object actualValue = getByPath(path);
			String actualStringRep = String.valueOf(actualValue);
			assertEquals("Value for property on path {" + path + "} did notmatch: " + msg, value, actualStringRep);
		} catch (PathNotFoundException e) {
			fail("Could not find property for path {" + path + "} - Json is:\n--snip--\n" + actual.encodePrettily() + "\n--snap--\n" + msg);
		}
		return this;
	}

	/**
	 * Resolve the given JSON path to load the value.
	 *
	 * @param jsonPath
	 *            the JSON path
	 * @param <T>
	 *            expected return type
	 * @return list of objects matched by the given path
	 */
	private <T> T getByPath(String jsonPath) {
		return JsonPath.read(actual.toString(), jsonPath);
	}

	public JsonObjectAssert hasNullValue(String key) {
		assertTrue("The json object should contain a map entry for key {" + key + "}", actual.containsKey(key));
		assertNull("The json object for key {" + key + "} should be null", actual.getJsonObject(key));
		return this;
	}

	/**
	 * Assert that the JSON object complies to the assertions which are stored in the comments of the query with the given name.
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public JsonObjectAssert compliesToAssertions(String name) throws IOException {
		String path = "/graphql/" + name;
		InputStream ins = getClass().getResourceAsStream(path);
		if (ins == null) {
			fail("Could not find query file {" + path + "}");
		}
		Scanner scanner = new Scanner(ins);
		try {
			int lineNr = 1;
			// Parse the query and extract comments which include assertions. Directly evaluate these assertions.
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				line = line.trim();
				if (line.startsWith("# [")) {
					int start = line.indexOf("# [") + 3;
					int end = line.lastIndexOf("]");
					String assertion = line.substring(start, end);
					evaluteAssertion(assertion, lineNr);
				}
				lineNr++;
			}
		} finally {
			scanner.close();
		}

		return this;
	}

	private void evaluteAssertion(String assertion, int lineNr) {
		String[] parts = assertion.split("=");
		if (parts.length <= 1) {
			fail("Assertion on line {" + lineNr + "} is not complete {" + assertion + "}");
		}
		String path = parts[0];
		String value = parts[1];

		String msg = "Failure on line {" + lineNr + "}";
		if ("<not-null>".equals(value)) {
			pathIsNotNull(path, msg);
		} else if ("<is-null>".equals(value)) {
			pathIsNull(path, msg);
		} else {
			has(path, value, msg);
		}
	}

	public JsonObjectAssert pathIsNotNull(String path) {
		return pathIsNotNull(path, null);
	}

	public JsonObjectAssert pathIsNotNull(String path, String msg) {
		if (msg == null) {
			msg = "";
		}
		Object value = JsonPath.read(actual.toString(), path);
		assertNotNull("Value on the path {" + path + "} was expected to be non-null: " + msg, value);
		return this;
	}

	public JsonObjectAssert pathIsNull(String path) {
		return pathIsNull(path, null);
	}

	public JsonObjectAssert pathIsNull(String path, String msg) {
		if (msg == null) {
			msg = "";
		}
		Object value = JsonPath.read(actual.toString(), path);
		assertNull("Value on the path {" + path + "} was expected to be null but was {" + value + "}: " + msg, value);
		return this;
	}
}

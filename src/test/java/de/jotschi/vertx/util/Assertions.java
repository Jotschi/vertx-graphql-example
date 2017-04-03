package de.jotschi.vertx.util;

import io.vertx.core.json.JsonObject;

public class Assertions extends org.assertj.core.api.Assertions {

	public static JsonObjectAssert assertThat(JsonObject actual) {
		return new JsonObjectAssert(actual);
	}

}

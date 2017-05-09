package de.jotschi.vertx.util;

import io.vertx.core.json.JsonObject;

public class QueryException extends Exception {

	private static final long serialVersionUID = 4376945756832139011L;
	private int statusCode;
	private String body;

	public QueryException(int statusCode, String body) {
		this.statusCode = statusCode;
		this.body = body;
	}

	/**
	 * Tries to parse the body string and return a json object. This may fail if the server response was not JSON.
	 * 
	 * @return
	 */
	public JsonObject getJson() {
		// TODO handle contenttype of the error response
		return new JsonObject(body);
	}

	@Override
	public String getMessage() {
		return "Query failed with status code {" + statusCode + "} and body {" + body + "}";
	}

}

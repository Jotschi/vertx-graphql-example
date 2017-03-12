package de.jotschi.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class GraphQLServer {

	public static void main(String[] args) {
		VertxOptions options = new VertxOptions();
		Vertx vertx = Vertx.vertx(options);
		vertx.deployVerticle(new GraphQLVerticle());
	}
}

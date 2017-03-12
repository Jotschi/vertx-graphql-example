package de.jotschi.vertx.data.graph;

import com.syncleus.ferma.ElementFrame;

public interface Named extends ElementFrame {

	public static final String NAME = "name";

	default void setName(String name) {
		setProperty(NAME, name);
	}

	default String getName() {
		return getProperty(NAME);
	}
}

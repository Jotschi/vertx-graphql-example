package de.jotschi.vertx.data.graph;

import com.syncleus.ferma.ElementFrame;

public interface ElementId extends ElementFrame {

	public static final String ID = "elementId";

	default Integer getElementId() {
		return getProperty(ID);
	}

	default void setElementId(int id) {
		setProperty(ID, id);
	}
}

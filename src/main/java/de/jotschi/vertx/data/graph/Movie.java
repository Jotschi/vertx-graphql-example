package de.jotschi.vertx.data.graph;

import com.gentics.ferma.annotation.GraphElement;
import com.gentics.ferma.orientdb.AbstractInterceptingVertexFrame;

@GraphElement
public class Movie extends AbstractInterceptingVertexFrame implements Named, ElementId {

	public final static String DESCRIPTION = "description";

	public void setDescription(String text) {
		setProperty(DESCRIPTION, text);
	}

	public String getDescription() {
		return getProperty(DESCRIPTION);
	}
}

package de.jotschi.vertx.data.graph;

import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.ext.AbstractInterceptingVertexFrame;

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

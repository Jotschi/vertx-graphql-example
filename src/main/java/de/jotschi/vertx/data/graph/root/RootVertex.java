package de.jotschi.vertx.data.graph.root;

import com.gentics.ferma.orientdb.AbstractInterceptingVertexFrame;
import com.syncleus.ferma.VertexFrame;

public class RootVertex extends AbstractInterceptingVertexFrame {

	public void addItem(VertexFrame frame) {
		linkOut(frame, "HAS_ITEM");
	}

}

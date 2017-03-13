package de.jotschi.vertx.data.graph.root;

import java.util.List;

import com.gentics.ferma.orientdb.AbstractInterceptingVertexFrame;
import com.syncleus.ferma.VertexFrame;

public class RootVertex extends AbstractInterceptingVertexFrame {

	public void addItems(VertexFrame... frames) {
		for (VertexFrame frame : frames) {
			linkOut(frame, "HAS_ITEM");
		}
	}

	public List<? extends VertexFrame> getItems() {
		return out("HAS_ITEM").toList();
	}

}

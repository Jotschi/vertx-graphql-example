package de.jotschi.vertx.data.graph.root;

import java.util.List;

import com.gentics.ferma.orientdb.AbstractInterceptingVertexFrame;
import com.syncleus.ferma.VertexFrame;

public abstract class RootVertex<T extends VertexFrame> extends AbstractInterceptingVertexFrame {

	public void addItems(T...items) {
		for (T item : items) {
			linkOut(item, "HAS_ITEM");
		}
	}

	public List<? extends T> getItems() {
		return out("HAS_ITEM").toList(getItemType());
	}

	abstract Class<T> getItemType();

}

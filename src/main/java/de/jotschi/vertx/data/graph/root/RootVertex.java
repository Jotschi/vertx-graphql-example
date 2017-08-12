package de.jotschi.vertx.data.graph.root;

import java.util.List;

import com.syncleus.ferma.VertexFrame;
import com.syncleus.ferma.ext.AbstractInterceptingVertexFrame;

/**
 * Abstract class for root vertices which are used to group/aggregate various other vertices.
 * 
 * @param <T>
 */
public abstract class RootVertex<T extends VertexFrame> extends AbstractInterceptingVertexFrame {

	/**
	 * Add the elements to the root element.
	 * 
	 * @param items
	 */
	public void addItems(T... items) {
		for (T item : items) {
			linkOut(item, "HAS_ITEM");
		}
	}

	/**
	 * Return a list of elements that are connected to this root element.
	 * 
	 * @return
	 */
	public List<? extends T> getItems() {
		return out("HAS_ITEM").toList(getItemType());
	}

	/**
	 * Find the element with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public T findById(int id) {
		return out("HAS_ITEM").has("elementId", id)
				.nextOrDefaultExplicit(getItemType(), null);
	}

	/**
	 * Return the element type for this root vertex.
	 * 
	 * @return
	 */
	abstract Class<T> getItemType();

}

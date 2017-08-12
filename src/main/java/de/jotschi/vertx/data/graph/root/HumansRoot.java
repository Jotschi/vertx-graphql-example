package de.jotschi.vertx.data.graph.root;

import com.syncleus.ferma.annotations.GraphElement;

import de.jotschi.vertx.data.graph.Human;

@GraphElement
public class HumansRoot extends RootVertex<Human> {

	@Override
	Class<Human> getItemType() {
		return Human.class;
	}
}

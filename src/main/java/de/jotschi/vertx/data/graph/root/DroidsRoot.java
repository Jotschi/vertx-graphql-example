package de.jotschi.vertx.data.graph.root;

import com.syncleus.ferma.annotations.GraphElement;

import de.jotschi.vertx.data.graph.Droid;

@GraphElement
public class DroidsRoot extends RootVertex<Droid> {

	@Override
	Class<Droid> getItemType() {
		return Droid.class;
	}

}

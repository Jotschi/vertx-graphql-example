package de.jotschi.vertx.data.graph.root;

import com.gentics.ferma.annotation.GraphElement;

import de.jotschi.vertx.data.graph.Planet;

@GraphElement
public class PlanetsRoot extends RootVertex<Planet> {

	@Override
	Class<Planet> getItemType() {
		return Planet.class;
	}

}

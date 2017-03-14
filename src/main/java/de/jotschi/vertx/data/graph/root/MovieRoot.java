package de.jotschi.vertx.data.graph.root;

import com.gentics.ferma.annotation.GraphElement;

import de.jotschi.vertx.data.graph.Movie;

@GraphElement
public class MovieRoot extends RootVertex<Movie> {

	@Override
	Class<Movie> getItemType() {
		return Movie.class;
	}

}

package de.jotschi.vertx.data.graph;

import java.util.List;

import com.syncleus.ferma.ext.AbstractInterceptingVertexFrame;

public class MovieCharacter extends AbstractInterceptingVertexFrame implements Named, ElementId {

	public static final String HAS_PLANET = "HAS_PLANET";

	public static final String APPEARS_IN = "APPEARS_IN";

	public static final String HAS_FRIEND = "HAS_FRIEND";

	public static final String HAS_HOME = "HAS_HOME";

	@Override
	protected void init() {
		super.init();
	}

	public void setHome(Planet planet) {
		setLinkOutTo(planet, HAS_HOME);
	}

	public Planet getHome() {
		return traverse((g) -> g.out(HAS_HOME)).nextOrDefaultExplicit(Planet.class, null);
	}

	public void addFriends(MovieCharacter... friends) {
		for (MovieCharacter friend : friends) {
			linkOut(friend, HAS_FRIEND);
		}
	}

	public List<? extends MovieCharacter> getFriends() {
		return traverse((g) -> g.out(HAS_FRIEND)).toList(MovieCharacter.class);
	}

	public void addAppearances(Movie... movies) {
		for (Movie movie : movies) {
			linkOut(movie, APPEARS_IN);
		}
	}

	public List<? extends Movie> getAppearances() {
		return traverse((g) -> g.out(APPEARS_IN)).toList(Movie.class);
	}
}

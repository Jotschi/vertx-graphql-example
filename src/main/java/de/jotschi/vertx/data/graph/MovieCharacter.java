package de.jotschi.vertx.data.graph;

import java.util.List;

import com.gentics.ferma.orientdb.AbstractInterceptingVertexFrame;

public class MovieCharacter extends AbstractInterceptingVertexFrame implements Named {

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
		return out(HAS_HOME).nextOrDefaultExplicit(Planet.class, null);
	}

	public void addFriend(MovieCharacter friend) {
		linkOut(friend, HAS_FRIEND);
	}

	public List<? extends MovieCharacter> getFriends() {
		return out(HAS_FRIEND).toList(MovieCharacter.class);
	}

	public void addAppearance(Movie movie) {
		linkOut(movie, APPEARS_IN);
	}
}

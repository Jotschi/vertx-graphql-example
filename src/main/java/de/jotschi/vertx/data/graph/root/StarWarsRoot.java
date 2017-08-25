package de.jotschi.vertx.data.graph.root;

import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.ext.AbstractInterceptingVertexFrame;

import de.jotschi.vertx.data.graph.Droid;

@GraphElement
public class StarWarsRoot extends AbstractInterceptingVertexFrame {

	public static final String HAS_CHARACTER = "HAS_CHARACTER";

	public static final String HAS_HUMANS_ROOT = "HAS_HUMANS_ROOT";
	public static final String HAS_DROIDS_ROOT = "HAS_DROIDS_ROOT";
	public static final String HAS_PLANETS_ROOT = "HAS_PLANET_ROOT";
	public static final String HAS_MOVIES_ROOT = "HAS_MOVIES_ROOT";

	public static final String HAS_HERO = "HAS_HERO";

	public void setHumansRoot(HumansRoot humans) {
		setLinkOutTo(humans, HAS_HUMANS_ROOT);
	}

	public HumansRoot getHumansRoot() {
		return traverse((g) -> g.out(HAS_HUMANS_ROOT)).nextOrDefaultExplicit(HumansRoot.class, null);
	}

	public void setDroidsRoot(DroidsRoot droids) {
		setLinkOutTo(droids, HAS_DROIDS_ROOT);
	}

	public DroidsRoot getDroidsRoot() {
		return traverse((g) -> g.out(HAS_DROIDS_ROOT)).nextOrDefaultExplicit(DroidsRoot.class, null);
	}

	public void setPlanetsRoot(PlanetsRoot planets) {
		setLinkOutTo(planets, HAS_PLANETS_ROOT);
	}

	public MovieRoot getMovieRoot() {
		return traverse((g) -> g.out(HAS_MOVIES_ROOT)).nextOrDefaultExplicit(MovieRoot.class, null);
	}

	public void setMovieRoot(MovieRoot movies) {
		setLinkOutTo(movies, HAS_MOVIES_ROOT);
	}

	public void setHero(Droid droid) {
		setLinkOutTo(droid, HAS_HERO);
	}

	public Droid getHero() {
		return traverse((g) -> g.out(HAS_HERO)).nextOrDefaultExplicit(Droid.class, null);
	}

}

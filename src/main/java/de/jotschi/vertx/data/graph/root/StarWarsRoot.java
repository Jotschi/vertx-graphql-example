package de.jotschi.vertx.data.graph.root;

import com.gentics.ferma.annotation.GraphElement;

import de.jotschi.vertx.data.graph.Droid;

@GraphElement
public class StarWarsRoot extends RootVertex {

	public static final String HAS_CHARACTER = "HAS_CHARACTER";

	public static final String HAS_HUMANS_ROOT = "HAS_HUMANS_ROOT";
	public static final String HAS_DROIDS_ROOT = "HAS_DROIDS_ROOT";
	public static final String HAS_PLANETS_ROOT = "HAS_PLANET_ROOT";

	public static final String HAS_HERO = "HAS_HERO";

	public void setHumansRoot(HumansRoot humans) {
		setLinkOutTo(humans, HAS_HUMANS_ROOT);
	}

	public void setDroidsRoot(DroidsRoot droids) {
		setLinkOutTo(droids, HAS_DROIDS_ROOT);
	}

	public void setPlanetsRoot(PlanetsRoot planets) {
		setLinkOutTo(planets, HAS_PLANETS_ROOT);
	}

	public void setHero(Droid droid) {
		setLinkOutTo(droid, HAS_HERO);
	}

	public Droid getHero() {
		return out(HAS_HERO).nextOrDefaultExplicit(Droid.class, null);
	}

}

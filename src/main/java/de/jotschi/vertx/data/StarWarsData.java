package de.jotschi.vertx.data;

import com.gentics.ferma.Trx;
import com.gentics.ferma.orientdb.OrientDBTrxFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import de.jotschi.vertx.data.graph.Droid;
import de.jotschi.vertx.data.graph.Human;
import de.jotschi.vertx.data.graph.Movie;
import de.jotschi.vertx.data.graph.Planet;
import de.jotschi.vertx.data.graph.root.DroidsRoot;
import de.jotschi.vertx.data.graph.root.HumansRoot;
import de.jotschi.vertx.data.graph.root.PlanetsRoot;
import de.jotschi.vertx.data.graph.root.StarWarsRoot;
import io.vertx.core.Vertx;

public class StarWarsData {

	private OrientGraphFactory graphFactory = new OrientGraphFactory("memory:tinkerpop").setupPool(4, 10);
	private OrientDBTrxFactory graph;

	private StarWarsRoot root;

	public StarWarsData(Vertx vertx) {
		graph = new OrientDBTrxFactory(graphFactory, vertx, "de.jotschi.vertx.data.graph");
		root = createDemoDataGraph();
	}

	private StarWarsRoot createDemoDataGraph() {
		try (Trx tx = graph.trx()) {

			Movie movie1 = tx.addVertex(Movie.class);
			movie1.setName("A New Hope");

			Movie movie2 = tx.addVertex(Movie.class);
			movie2.setName("The Empire Strikes Back");

			Movie movie3 = tx.addVertex(Movie.class);
			movie3.setName("Return of the Jedi");

			Movie movie4 = tx.addVertex(Movie.class);
			movie4.setName("The Phantom Menance");

			Movie movie5 = tx.addVertex(Movie.class);
			movie5.setName("Attack of the Clones");

			Movie movie6 = tx.addVertex(Movie.class);
			movie6.setName("Revenge of the Sith");

			Movie movie7 = tx.addVertex(Movie.class);
			movie7.setName("The Force Awakens");

			Movie movie8 = tx.addVertex(Movie.class);
			movie8.setName("The Last Jedi");

			Movie movie9 = tx.addVertex(Movie.class);
			movie9.setName("Episode IX");

			Movie movie10 = tx.addVertex(Movie.class);
			movie10.setName("Rogue One");

			// Tatooine
			Planet tatooine = tx.addVertex(Planet.class);
			tatooine.setName("Tatooine");

			//Alderaan
			Planet alderaan = tx.addVertex(Planet.class);
			alderaan.setName("Alderaan");

			// Luke Skywalker
			Human luke = tx.addVertex(Human.class);
			luke.setName("Luke Skywalker");
			luke.setHome(tatooine);
			luke.addAppearance(movie1);
			luke.addAppearance(movie2);
			luke.addAppearance(movie3);
			luke.addAppearance(movie9);
			luke.setHome(tatooine);

			// Darth Vader
			Human vader = tx.addVertex(Human.class);
			vader.setName("Darth Vader");
			vader.addAppearance(movie1);
			vader.addAppearance(movie2);
			vader.addAppearance(movie3);
			vader.addAppearance(movie10);
			vader.setHome(tatooine);

			// Han Solo
			Human han = tx.addVertex(Human.class);
			han.setName("Han Solo");
			han.addAppearance(movie1);
			han.addAppearance(movie2);
			han.addAppearance(movie3);
			han.addAppearance(movie9);

			// Princess Leia
			Human leia = tx.addVertex(Human.class);
			leia.setName("Leia Organa");
			leia.setHome(alderaan);
			leia.addAppearance(movie1);
			leia.addAppearance(movie2);
			leia.addAppearance(movie3);

			// Tarkin
			Human tarkin = tx.addVertex(Human.class);
			tarkin.setName("Wilhuff Tarkin");
			tarkin.addAppearance(movie1);
			tarkin.addAppearance(movie9);

			// C3P0
			Droid c3p0 = tx.addVertex(Droid.class);
			c3p0.setName("C-3P0");
			c3p0.setPrimaryFunction("Protocol");
			c3p0.addAppearance(movie1);
			c3p0.addAppearance(movie2);
			c3p0.addAppearance(movie3);
			c3p0.addAppearance(movie9);

			// R2D2
			Droid r2d2 = tx.addVertex(Droid.class);
			r2d2.setName("R2-D2");
			r2d2.setPrimaryFunction("Astromech");

			r2d2.addAppearance(movie1);
			r2d2.addAppearance(movie2);
			r2d2.addAppearance(movie3);
			r2d2.addAppearance(movie9);

			leia.addFriend(luke);
			leia.addFriend(han);
			leia.addFriend(r2d2);
			leia.addFriend(c3p0);

			han.addFriend(luke);
			han.addFriend(leia);
			han.addFriend(r2d2);

			luke.addFriend(han);
			luke.addFriend(leia);
			luke.addFriend(c3p0);
			luke.addFriend(r2d2);

			r2d2.addFriend(luke);
			r2d2.addFriend(han);
			r2d2.addFriend(leia);

			c3p0.addFriend(luke);
			c3p0.addFriend(han);
			c3p0.addFriend(leia);
			c3p0.addFriend(r2d2);

			tarkin.addFriend(vader);
			vader.addFriend(tarkin);

			DroidsRoot droids = tx.addVertex(DroidsRoot.class);
			droids.addItem(c3p0);
			droids.addItem(r2d2);

			HumansRoot humans = tx.addVertex(HumansRoot.class);
			humans.addItem(leia);
			humans.addItem(han);
			humans.addItem(vader);
			humans.addItem(tarkin);
			humans.addItem(luke);

			PlanetsRoot planets = tx.addVertex(PlanetsRoot.class);
			planets.addItem(tatooine);
			planets.addItem(alderaan);

			StarWarsRoot root = tx.addVertex(StarWarsRoot.class);
			root.setHumansRoot(humans);
			root.setDroidsRoot(droids);
			root.setPlanetsRoot(planets);

			root.setHero(r2d2);

			tx.success();
			return root;
		}
	}

	public StarWarsRoot getRoot() {
		return root;
	}

	public OrientDBTrxFactory getGraph() {
		return graph;
	}
}

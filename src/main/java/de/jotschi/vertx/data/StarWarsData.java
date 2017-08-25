package de.jotschi.vertx.data;

import org.apache.tinkerpop.gremlin.orientdb.OrientGraphFactory;

import com.gentics.ferma.ext.orientdb.vertx.OrientDBTxVertexFactory;
import com.syncleus.ferma.tx.Tx;

import de.jotschi.vertx.data.graph.Droid;
import de.jotschi.vertx.data.graph.Human;
import de.jotschi.vertx.data.graph.Movie;
import de.jotschi.vertx.data.graph.Planet;
import de.jotschi.vertx.data.graph.root.DroidsRoot;
import de.jotschi.vertx.data.graph.root.HumansRoot;
import de.jotschi.vertx.data.graph.root.MovieRoot;
import de.jotschi.vertx.data.graph.root.PlanetsRoot;
import de.jotschi.vertx.data.graph.root.StarWarsRoot;
import io.vertx.core.Vertx;

/**
 * Class which provides the star wars graph. 
 */
public class StarWarsData {

	private OrientGraphFactory graphFactory = new OrientGraphFactory("memory:tinkerpop").setupPool(4, 10);
	private OrientDBTxVertexFactory graph;

	private StarWarsRoot root;

	public StarWarsData(Vertx vertx) {
		graph = new OrientDBTxVertexFactory(graphFactory, vertx, "de.jotschi.vertx.data.graph");
		root = createDemoDataGraph();
	}

	private StarWarsRoot createDemoDataGraph() {
		try (Tx tx = graph.tx()) {

			Movie movie1 = tx.addVertex(Movie.class);
			movie1.setName("A New Hope");
			movie1.setElementId(200);
			movie1.setDescription("Released in 1977");

			Movie movie2 = tx.addVertex(Movie.class);
			movie2.setName("The Empire Strikes Back");
			movie2.setElementId(201);
			movie2.setDescription("Released in 1980");

			Movie movie3 = tx.addVertex(Movie.class);
			movie3.setName("Return of the Jedi");
			movie3.setElementId(202);
			movie3.setDescription("Released in 1983");

			Movie movie4 = tx.addVertex(Movie.class);
			movie4.setName("The Phantom Menance");
			movie4.setElementId(203);
			movie4.setDescription("Released in 1999");

			Movie movie5 = tx.addVertex(Movie.class);
			movie5.setName("Attack of the Clones");
			movie5.setElementId(204);
			movie5.setDescription("Released in 2002");

			Movie movie6 = tx.addVertex(Movie.class);
			movie6.setName("Revenge of the Sith");
			movie6.setElementId(205);
			movie6.setDescription("Released in 2005");

			Movie movie7 = tx.addVertex(Movie.class);
			movie7.setName("The Force Awakens");
			movie7.setElementId(206);
			movie7.setDescription("Released in 2015");

			Movie movie8 = tx.addVertex(Movie.class);
			movie8.setName("Rogue One");
			movie8.setElementId(207);
			movie8.setDescription("Released in 2016");

			Movie movie9 = tx.addVertex(Movie.class);
			movie9.setName("The Last Jedi");
			movie9.setElementId(208);
			movie9.setDescription("Release in 2017");

			Movie movie10 = tx.addVertex(Movie.class);
			movie10.setName("Episode IX");
			movie10.setElementId(209);
			movie10.setDescription("Release in 2019");

			MovieRoot movies = tx.addVertex(MovieRoot.class);
			movies.addItems(movie1, movie2, movie3, movie4, movie5, movie6, movie7, movie9, movie10);

			// Tatooine
			Planet tatooine = tx.addVertex(Planet.class);
			tatooine.setElementId(400);
			tatooine.setName("Tatooine");

			// Alderaan
			Planet alderaan = tx.addVertex(Planet.class);
			alderaan.setElementId(401);
			alderaan.setName("Alderaan");

			// Luke Skywalker
			Human luke = tx.addVertex(Human.class);
			luke.setElementId(1000);
			luke.setName("Luke Skywalker");
			luke.setHome(tatooine);
			luke.addAppearances(movie1, movie2, movie3, movie9);
			luke.setHome(tatooine);

			// Darth Vader
			Human vader = tx.addVertex(Human.class);
			vader.setElementId(1001);
			vader.setName("Darth Vader");
			vader.addAppearances(movie1, movie2, movie3, movie10);
			vader.setHome(tatooine);

			// Han Solo
			Human han = tx.addVertex(Human.class);
			han.setElementId(1002);
			han.setName("Han Solo");
			han.addAppearances(movie1, movie2, movie3, movie9);

			// Princess Leia
			Human leia = tx.addVertex(Human.class);
			leia.setElementId(1003);
			leia.setName("Leia Organa");
			leia.setHome(alderaan);
			leia.addAppearances(movie1, movie2, movie3, movie9);

			// Tarkin
			Human tarkin = tx.addVertex(Human.class);
			tarkin.setElementId(1004);
			tarkin.setName("Wilhuff Tarkin");
			tarkin.addAppearances(movie1, movie9);

			// C3P0
			Droid c3p0 = tx.addVertex(Droid.class);
			c3p0.setElementId(1005);
			c3p0.setName("C-3PO");
			c3p0.setPrimaryFunction("Protocol");
			c3p0.addAppearances(movie1, movie2, movie3, movie9);

			// R2D2
			Droid r2d2 = tx.addVertex(Droid.class);
			r2d2.setElementId(1006);
			r2d2.setName("R2-D2");
			r2d2.setPrimaryFunction("Astromech");
			r2d2.addAppearances(movie1, movie2, movie3, movie9);

			leia.addFriends(luke, han, r2d2, c3p0);
			han.addFriends(luke, leia, r2d2);
			luke.addFriends(han, leia, c3p0, r2d2);
			r2d2.addFriends(luke, han, leia);

			c3p0.addFriends(luke, han, leia, r2d2);

			tarkin.addFriends(vader);
			vader.addFriends(tarkin);

			DroidsRoot droids = tx.addVertex(DroidsRoot.class);
			droids.addItems(c3p0, r2d2);

			HumansRoot humans = tx.addVertex(HumansRoot.class);
			humans.addItems(leia, han, vader, tarkin, luke);

			PlanetsRoot planets = tx.addVertex(PlanetsRoot.class);
			planets.addItems(tatooine, alderaan);

			StarWarsRoot root = tx.addVertex(StarWarsRoot.class);
			root.setHumansRoot(humans);
			root.setDroidsRoot(droids);
			root.setPlanetsRoot(planets);
			root.setMovieRoot(movies);
			root.setHero(r2d2);

			tx.success();
			return root;
		}
	}

	public StarWarsRoot getRoot() {
		return root;
	}

	public OrientDBTxVertexFactory getGraph() {
		return graph;
	}
}

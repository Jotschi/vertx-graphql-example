package de.jotschi.vertx;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.syncleus.ferma.tx.Tx;

import de.jotschi.vertx.data.StarWarsData;
import de.jotschi.vertx.data.graph.Human;
import de.jotschi.vertx.data.graph.Movie;
import de.jotschi.vertx.data.graph.MovieCharacter;
import de.jotschi.vertx.data.graph.root.HumansRoot;
import de.jotschi.vertx.data.graph.root.StarWarsRoot;

public class StarWarsDataTest {

	@Test
	public void testData() {
		StarWarsData data = new StarWarsData(null);
		try (Tx tx = data.getGraph().tx()) {
			StarWarsRoot root = data.getRoot();
			assertNotNull(root);
			HumansRoot humanRoot = root.getHumansRoot();
			assertNotNull(humanRoot);
			Human human = humanRoot.findById(1001);
			assertNotNull(human);
			for (MovieCharacter friend : human.getFriends()) {
				System.out.println(friend.getName());
			}
			for (Movie movie : data.getRoot().getMovieRoot().getItems()) {
				System.out.println(movie.getName());
			}
		}
	}
}

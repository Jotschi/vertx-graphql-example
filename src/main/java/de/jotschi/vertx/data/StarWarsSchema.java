package de.jotschi.vertx.data;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;

import de.jotschi.vertx.data.graph.Movie;
import de.jotschi.vertx.data.graph.MovieCharacter;
import de.jotschi.vertx.data.graph.root.StarWarsRoot;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.TypeResolver;

public class StarWarsSchema {

	private GraphQLInterfaceType characterInterface;
	private GraphQLObjectType queryType;
	private GraphQLObjectType droidType;
	private GraphQLObjectType humanType;
	private GraphQLObjectType movieType;
	private GraphQLSchema starwarsSchema;

	public StarWarsSchema() {
		this.movieType = createMovieType();
		this.characterInterface = createCharacterInterface();
		this.humanType = createHumanType();
		this.droidType = createDroidType();
		this.queryType = createQueryType();
		this.starwarsSchema = createStarWarsSchema();
	}

	private GraphQLSchema createStarWarsSchema() {
		return GraphQLSchema.newSchema()
				.query(queryType)
				.build();
	}

	public TypeResolver characterTypeResolver = (obj) -> {
		System.out.println("type:" + obj.getClass()
				.getName());
		//			int id = object.id;
		//			if (humanData[id] != null)
		//				return StarWarsSchema.humanType;
		//			if (droidData[id] != null)
		return droidType;
	};

	private DataFetcher friendsDataFetcher = (env) -> {
		Object source = env.getSource();
		if (source instanceof MovieCharacter) {
			MovieCharacter character = (MovieCharacter) source;
			return character.getFriends();
		}
		return null;
	};

	private DataFetcher movieDataFetcher = (env) -> {
		Object source = env.getSource();
		if (source instanceof MovieCharacter) {
			MovieCharacter character = (MovieCharacter) source;
			return character.getAppearances();
		}
		return null;
	};

	private DataFetcher droidDataFetcher = (env) -> {
		System.out.println("DroidFetcher: " + env.getSource()
				.getClass()
				.getName());
		return null;
	};

	public DataFetcher r2d2Fetcher = (env) -> {
		Object source = env.getSource();
		if (source instanceof StarWarsRoot) {
			StarWarsRoot root = (StarWarsRoot) source;
			return root.getHero();
		}
		return null;
	};

	private GraphQLInterfaceType createCharacterInterface() {
		return newInterface().name("Character")
				.description("A character in the Star Wars Trilogy")

				// .id
				.field(newFieldDefinition().name("id")
						.description("The id of the character.")
						.type(new GraphQLNonNull(GraphQLString)))

				// .name
				.field(newFieldDefinition().name("name")
						.description("The name of the character.")
						.type(GraphQLString))

				// .friends
				.field(newFieldDefinition().name("friends")
						.description("The friends of the character, or an empty list if they have none.")
						.type(new GraphQLList(new GraphQLTypeReference("Character"))))

				// .appearsIn
				.field(newFieldDefinition().name("appearsIn")
						.description("Which movies they appear in.")
						.type(new GraphQLList(movieType))
						.dataFetcher(movieDataFetcher))

				.typeResolver(characterTypeResolver)
				.build();
	}

	private GraphQLObjectType createHumanType() {
		return newObject().name("Human")
				.description("A humanoid creature in the Star Wars universe.")
				.withInterface(characterInterface)

				// .id
				.field(newFieldDefinition().name("id")
						.description("The id of the human.")
						.type(new GraphQLNonNull(GraphQLString)))

				// .name
				.field(newFieldDefinition().name("name")
						.description("The name of the human.")
						.type(GraphQLString))

				// .friends
				.field(newFieldDefinition().name("friends")
						.description("The friends of the human, or an empty list if they have none.")
						.type(new GraphQLList(characterInterface))
						.dataFetcher(friendsDataFetcher))

				// .appearsIn
				.field(newFieldDefinition().name("appearsIn")
						.description("Which movies they appear in.")
						.type(new GraphQLList(movieType)))

				// .homePlanet
				.field(newFieldDefinition().name("homePlanet")
						.description("The home planet of the human, or null if unknown.")
						.type(GraphQLString))
				.build();
	}

	private GraphQLObjectType createMovieType() {

		return newObject().name("Movie")
				.description("One of the films in the Star Wars universe.")

				// .title
				.field(newFieldDefinition().name("title")
						.description("Title of the episode.")
						.type(GraphQLString)
						.dataFetcher((env) -> {
							if (env.getSource() instanceof Movie) {
								return ((Movie) env.getSource()).getName();
							}
							return null;
						}))

				// .description
				.field(newFieldDefinition().name("description")
						.description("Description of the episode.")
						.type(GraphQLString))

				.build();
	}

	private GraphQLObjectType createDroidType() {
		return newObject().name("Droid")
				.description("A mechanical creature in the Star Wars universe.")
				.withInterface(characterInterface)

				// .id
				.field(newFieldDefinition().name("id")
						.description("The id of the droid.")
						.type(new GraphQLNonNull(GraphQLString)))

				// .name
				.field(newFieldDefinition().name("name")
						.description("The name of the droid.")
						.type(GraphQLString))

				// .friends
				.field(newFieldDefinition().name("friends")
						.description("The friends of the droid, or an empty list if they have none.")
						.type(new GraphQLList(characterInterface))
						.dataFetcher(friendsDataFetcher))

				// .appearsIn
				.field(newFieldDefinition().name("appearsIn")
						.description("Which movies they appear in.")
						.type(new GraphQLList(movieType))
						.dataFetcher(movieDataFetcher))

				// .primaryFunction
				.field(newFieldDefinition().name("primaryFunction")
						.description("The primary function of the droid.")
						.type(GraphQLString))
				.build();
	}

	private GraphQLObjectType createQueryType() {
		return newObject().name("QueryType")

				// .hero
				.field(newFieldDefinition().name("hero")
						.type(characterInterface)
						.argument(newArgument().name("episode")
								.description(
										"If omitted, returns the hero of the whole saga. If provided, returns the hero of that particular episode.")
								.type(GraphQLString))
						.dataFetcher(r2d2Fetcher))

				// .movies
				.field(newFieldDefinition().name("movies")
						.type(new GraphQLList(movieType))
						.dataFetcher((env -> {
							Object source = env.getSource();
							if (source instanceof StarWarsRoot) {
								StarWarsRoot root = (StarWarsRoot) source;
								return root.getMovieRoot()
										.getItems();
							}
							return null;
						})))

				// .human
				.field(newFieldDefinition().name("human")
						.type(humanType)
						.argument(newArgument().name("id")
								.description("id of the human")
								.type(new GraphQLNonNull(GraphQLString)))
						.dataFetcher(friendsDataFetcher))

				// .droid
				.field(newFieldDefinition().name("droid")
						.type(createDroidType())
						.argument(newArgument().name("id")
								.description("id of the droid")
								.type(new GraphQLNonNull(GraphQLString)))
						.dataFetcher(droidDataFetcher))
				.build();
	}

	public GraphQLSchema getStarWarsSchema() {
		return starwarsSchema;
	}
}
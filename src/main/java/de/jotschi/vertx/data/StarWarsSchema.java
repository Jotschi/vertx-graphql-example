package de.jotschi.vertx.data;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.List;

import de.jotschi.vertx.data.graph.ElementId;
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

/**
 * GraphQL Schema for the star wars data.
 */
public class StarWarsSchema {

	private GraphQLInterfaceType characterInterface;
	private GraphQLObjectType queryType;
	private GraphQLObjectType droidType;
	private GraphQLObjectType humanType;
	private GraphQLObjectType movieType;
	private GraphQLSchema starwarsSchema;
//	private GraphQLEnumType factionType;

	public StarWarsSchema() {
//		this.factionType = createFactionType();
		this.movieType = createMovieType();
		this.characterInterface = createCharacterInterface();
		this.humanType = createHumanType();
		this.droidType = createDroidType();
		this.queryType = createQueryType();
		this.starwarsSchema = createStarWarsSchema();
	}

//	private GraphQLEnumType createFactionType() {
//		return newEnum().name("Faction")
//				.description("Type of faction")
//				.value("REBELS", "Rebel Alliance",
//						"A resistance movement formed by Bail Organa and Mon Mothma to oppose the reign of the Galactic Empire.")
//				.value("REPUBLIC", "Galactic Republic",
//						"The democratic union that governed the galaxy for a thousand years prior to the rise of the Galactic Empire.")
//				.value("EMPIRE", "Galactic Empire",
//						"The galactic, constitutional monarchy and fascist government that replaced the Galactic Republic in the aftermath of the Clone Wars")
//				.build();
//	}

	private GraphQLSchema createStarWarsSchema() {
		return GraphQLSchema.newSchema()
				.query(queryType)
				.build();
	}

	private TypeResolver characterTypeResolver = (obj) -> {
		return droidType;
	};

	private DataFetcher<List<? extends Movie>> movieDataFetcher = (env) -> {
		MovieCharacter character = env.getSource();
		return character.getAppearances();
	};

	private DataFetcher<Integer> idDataFetcher = (env) -> {
		ElementId element = env.getSource();
		return element.getElementId();
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
						.dataFetcher((env) -> {
							MovieCharacter character = env.getSource();
							return character.getFriends();
						}))

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
							Movie movie = env.getSource();
							return movie.getName();
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
						.type(new GraphQLNonNull(GraphQLString))
						.dataFetcher(idDataFetcher))

				// .name
				.field(newFieldDefinition().name("name")
						.description("The name of the droid.")
						.type(GraphQLString))

				// .friends
				.field(newFieldDefinition().name("friends")
						.description("The friends of the droid, or an empty list if they have none.")
						.type(new GraphQLList(characterInterface))
						.dataFetcher((env) -> {
							MovieCharacter character = env.getSource();
							return character.getFriends();
						}))

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
								.description("If omitted, returns the hero. If provided, returns the hero of that particular episode.")
								.type(GraphQLString))
						.dataFetcher((env) -> {
							StarWarsRoot root = env.getSource();
							return root.getHero();
						}))

				// .movies
				.field(newFieldDefinition().name("movies")
						.type(new GraphQLList(movieType))
						.dataFetcher((env -> {
							StarWarsRoot root = env.getSource();
							return root.getMovieRoot()
									.getItems();
						})))

				// .human
				.field(newFieldDefinition().name("human")
						.type(humanType)
						.argument(newArgument().name("id")
								.description("id of the human")
								.type(new GraphQLNonNull(GraphQLInt)))
						.dataFetcher((env) -> {
							StarWarsRoot root = env.getSource();
							int id = env.getArgument("id");
							return root.getHumansRoot()
									.findById(id);
						}))

				// .droid
				.field(newFieldDefinition().name("droid")
						.type(createDroidType())
						.argument(newArgument().name("id")
								.description("id of the droid")
								.type(new GraphQLNonNull(GraphQLInt)))
						.dataFetcher((env) -> {
							StarWarsRoot root = env.getSource();
							int id = env.getArgument("id");
							return root.getDroidsRoot()
									.findById(id);
						}))
				.build();
	}

	public GraphQLSchema getStarWarsSchema() {
		return starwarsSchema;
	}
}
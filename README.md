# Vert.x GraphQL Example

This example shows how to build a basic GraphQL endpoint using [Vert.x](http://vertx.io/) and [OrientDB](http://orientdb.com/orientdb/)


## Getting Started

* Clone example project
```
git clone git@github.com:jotschi/vertx-graphql-example.git
cd vertx-graphql-example
```

* Import the project into your IDE 

* Start the Vert.x web server by running GraphQLServer.java
Access http://localhost:3000/browser/ in your browser or use this 
[Example Query](http://localhost:3000/browser/?query=%7B%0A%20%20movies%20%7B%0A%20%20%20%20title%0A%20%20%7D%0A%20%20hero%20%7B%0A%20%20%20%20name%0A%20%20%20%20friends%20%7B%0A%20%20%20%20%20%20name%0A%20%20%20%20%20%20friends%20%7B%0A%20%20%20%20%20%20%20%20name%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%20%20%7D%0A%7D%0A)

## Contents

The example shows:

### GraphiQL browser

The [GraphiQL browser](https://github.com/graphql/graphiql) is served via a StaticHandler. No further configuration is needed.

### Data setup

The `StarWarsData` class contains the demo data. The demo data is structured in a very simple graph. A central root element is used to reference the aggregation vertices which list all the basic elements (humans, droids, planets, movies).
Additional edges exist between those elements.

This example does not use SQL to interface with the GraphDB. Instead it uses makes use of the Object Graph Mapper library [Ferma 2.x](https://github.com/Syncleus/Ferma). This library allowed me to create basic Java Classes which map to the vertices which I later used within my Graph. Ferma helps a lot if you plan to setup your graph domain model. 
Using a tinkerpop based native API is in most cases the fastest way to interact with the GraphDB. OrientDB nativly supports this API and thus the overall overhead is minimal compared to SQL.


NOTE: Ferma 2.x uses Tinkerpop 2.x - Ferma 3.x makes use of Tinkerpop 3.x and works differently. I choose Ferma 2.x for this example because the Tinkerpop 3.x support for OrientDB is still in development. (as of 04/2017)

### GraphQL schema

The GraphQL schema defines which fields and objects can be resolved via a query.
I use [graphql-java](https://github.com/graphql-java/graphql-java) to setup the schema and process the query.

### Executing queries

The `GraphQLVerticle` contains the whole query execution logic.

The query is wrapped in an JSON string and thus needs to unwrapped first.

The `handleQuery` method processes the query. It is important to note that the query 
has a starting point. In our case this is the root vertex.

```
tx.complete(graphQL.execute(query, demoData.getRoot()));
```

All GraphQL schema data fetchers can access this root vertex and use it as a starting point for the graph traversals.

### Testing GraphQL

There are many ways to test a GraphQL API. You could write unit tests which individually test each graphql type or just test the data fetchers.
I often opt for tests which cover a big portion of application stack. Those tests have the advantage that they have the potential to uncover bugs which reside in the areas between application parts. The downside is that they often take much longer to execute.

The `GraphQLTest` which is part of the example loads a predefined GraphQL query and executes it by posting it to the graphql endpoint. The graphQL query also contains special comments which are parsed. These parsed comments are used to perform assertions against the response query.

```
{
    vader: human(id: 1001) {
		# [$.data.vader.name=Darth Vader]
		name
    }
}

```

The comment contains a basic json-path and expected value for that path.

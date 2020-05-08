# Reactive Spring Playground

This is a playground project to showcase non-blocking reactive REST interaction using [Spring Reactor](https://projectreactor.io).
Don't expect too much here, I just played around with the features and data models a bit.

The application consists of a client and a server component to illustrate non-blocking client/server interaction.

## How to run

Build and run as follows:

1. Start up MongoDB (Yup, you might need to [install it first](https://docs.mongodb.com/manual/tutorial/). I'm assuming the standard port 27017)
2. Build & start up the server:  `cd server && mvn clean package && java -jar -Dspring.profiles.active=dev target/reactive-spring-server*.jar && cd ..`
3. Build & start up the client:  `cd client && mvn clean package && java -jar -Dspring.profiles.active=dev target/reactive-spring-client*.jar && cd ..`

Please find more specific information on the client and the server app below.

## Server

The server application consists of three parts:

1. I coded general usage scenarios as JUnit test cases, so feel free to explore `src/test` for examples
2. There is a general API that shows general usage examples available - see _General API_ for more information
3. There is a stock management API which showcases general CRUD operations - see _Inventory API_ for more information

### General API - Fluxes and Monos

The general API emits fluxes and monos. Nothing fancy, very basic:

* `GET http://localhost:8080/flux` - emits a flux upon the `onComplete` event
* `GET http://localhost:8080/fluxstream` - emits a flux upon every `onNext` event (cold publisher)
* `GET http://localhost:8080/mono` - emits a mono upon the `onComplete` event
* `GET http://localhost:8080/functional/flux` - same as `/flux`, but implemented using functional-style routers and handlers
* `GET http://localhost:8080/functional/mono` - same as `/mono`, but implemented using functional-style routers and handlers

### Inventory API - stock management

The stock management API provides CRUD operations stock records. Two versions are available:

* `v1`: Classic Spring REST controller
* `v2`: Functional-style REST router/handler setup

Exposed endpoints:

* `GET http://localhost:8080/v[1|2]/items` - emits all items in the stock database
* `POST http://localhost:8080/v[1|2]/items` - creates a new item
* `GET http://localhost:8080/v[1|2]/items/<some-id>` - emits a specific item from the stock database
* `DELETE http://localhost:8080/v[1|2]/items/<some-id>` - deletes a specific item from the stock database
* `PUT http://localhost:8080/v[1|2]/items/<some-id>` - updates a specific item from the stock database

The persistence backend uses a MongoDB, so you will need to have at least a MongoDB community installed on your machine -
please refer to [the docs](https://docs.mongodb.com/manual/tutorial/) for more information.

## Client

The client provides a number of REST endpoints that use the server application as a backend to perform non-blocking calls. This is basic CRUD stuff:

* `GET http://localhost:8081/items?useRetrieve=[true|false]` - retrieves all items from the server backend. As this can be done using `retrieve` or `exchange` it's on you to decide. Defaults to `true`.
* `GET http://localhost:8081/items/<some-id>` - retrieves the specified item
* `POST http://localhost:8081/items` - creates the specified item
* `PUT http://localhost:8081/items/<some-id>` - updates the specified item
* `DELETE http://localhost:8081/items/<some-id>` - deletes the specified item

## Further reading

* The [Spring Reactor docs](https://projectreactor.io/docs) are a good place to start!
* [Another example](https://github.com/hantsy/spring-reactive-sample/blob/master/boot-routes/src/main/java/com/example/demo/DemoApplication.java) for reactive REST endpoint setups with Spring

Unsure which operator to use? See https://projectreactor.io/docs/core/release/reference/#which-operator

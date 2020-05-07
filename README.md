# Reactive Spring Playground

This is a playground project I set up for getting familiar with non-blocking backends using Spring Reactor.
There is no underlying business use case I constructed, I just played around with the features and data models a bit.

## How to run

A coded quite a few usage scenarios as JUnit test cases, so feel free to explore `src/test` for examples.

You will also find a few minimalistic rest endpoints returning Fluxes and Monos, as well as a minimal stock management API.
Build/run it as follows:

```
`mvn clean package && java -jar -Dspring.profiles.active=dev target/reactive-spring*.jar`.
```

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

## Further reading

* The [Spring Reactor docs](https://projectreactor.io/docs) are a good place to start!
* [Another example](https://github.com/hantsy/spring-reactive-sample/blob/master/boot-routes/src/main/java/com/example/demo/DemoApplication.java) for reactive REST endpoint setups with Spring 

Unsure which operator to use? See https://projectreactor.io/docs/core/release/reference/#which-operator

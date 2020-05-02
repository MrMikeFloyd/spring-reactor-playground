# Reactive Spring Playground

Playground project for getting familiar with non-blocking backends using Spring Reactor.
This isn't necessarily usable code, I'm just fiddling around with things a bit get a basic understanding of this stuff.

## How to run

A coded quite a few scenarios as JUnit test cases, so feel free to explore `src/test` for examples.

You will also find a few minimalistic rest endpoints returning Fluxes and Monos:

* `http://localhost:8080/flux` - emits a flux upon the `onComplete` event
* `http://localhost:8080/fluxstream` - emits a flux upon every `onNext` event (cold publisher)
* `http://localhost:8080/mono` - emits a mono upon the `onComplete` event
* `http://localhost:8080/functional/flux` - same as `/flux`, but implemented using functional-style routers and handlers
* `http://localhost:8080/functional/mono` - same as `/mono`, but implemented using functional-style routers and handlers

Run it with `mvn spring-boot:run`.

## Further reading

The Reactor docs are a good place to start!

Which operator do I need? https://projectreactor.io/docs/core/release/reference/#which-operator

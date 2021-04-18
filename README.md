# CQRS - Command Query Responsibility Segregation

This project contains the following CQRS components:

* Interfaces for  - Event Bus, Event Store, Command Bus, Aggregate Repository
* Kafka implementation for Event Bus
* rabbitMQ implementation of event bus
* MySQL implementation for Event Store
* Jackson serializer to save events to event store and publish to kafka

* example spring boot project to test CQRS workflow.
* docker-compose file to test the end to end workflow in docker environment.




# CQRS - Command Query Responsibility Segregation

This project contains the following CQRS components:

* Interfaces for  - Event Bus, Event Store, Command Bus, Aggregate Repository
* utility to find command handler from command name, and event handler from event name.
* Kafka implementation for Event Bus
* rabbitMQ implementation of event bus
* MySQL implementation for Event Store
* Jackson serializer to save events to event store and publish to kafka

* example spring boot command and query service to test CQRS workflow.
* docker-compose file to test the end to end workflow in docker environment.




# CQRS - Command Query Responsibility Segregation

This project contains the following CQRS components:

* Interfaces for  - Event Bus, Event Store, Command Bus, Aggregate Repository
* utility to find command handler from command name, and event handler from event name.
* command dispatcher component to dispatch commands
* Aggregate repository component to load and save events to event store and publish events to event bus 
* Kafka implementation for event bus - Kafka publisher and consumer 
* rabbitMQ implementation of event bus
* MySQL implementation for event store
* Jackson serializer to save events to event store and publish to event bus

* example spring boot command and query service to test CQRS workflow.
* REST controller with REST API endpoints to invoke the POST, PATCH and GET operations.
* command handlers and event handlers for the task manager example
* docker-compose file to test the end to end workflow in docker environment.




# Task Manager Example

This example implements an example on how to utilize the cqrs libraries to implement an event sourced task manager.

In the domain we use Domain Driven Design principles to do our domain validation and model the behavior in the domain.

**Books on DDD:**

- [Domain Driven Design: Tackling Complexity in the Heart of Software - Eric Evans](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215)
- [Implementing Domain Driven Design - Vaugh Vernon](https://www.amazon.com/dp/0321834577)

## Run

To run the project in Docker simply run the following commands to boot up a docker environment with all related dependencies.

```bash
./gradlew bootJar unpack
docker-compose up --build
```
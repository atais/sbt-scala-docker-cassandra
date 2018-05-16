# Cassandra docker ScalaTests

This projects aims to create an example integration test 
case scenario for Cassandra.

For this purpose I am using:
1. <a href="https://hub.docker.com/r/spotify/cassandra/" target="_blank">spotify/cassandra image</a>
2. https://github.com/whisklabs/docker-it-scala
3. https://github.com/testcontainers/testcontainers-scala

# General tips

A quite efficient ready-check for a docker image is a log checker.

To easily find the line, that you could treat as ready status, fire up the test on debug and stop in the middle.
Next, check the docker logs with:

1. Find the container ID: `docker ps -a`
2. Check the logs: `docker logs <ID>`


# docker-it-scala

Working container definition

```scala
val cassandraContainer: DockerContainer = DockerContainer("spotify/cassandra:latest")
    .withPorts(9042 -> Some(9042), 9060 -> Some(9060))
    .withReadyChecker(DockerReadyChecker.LogLineContains("Listening for thrift clients"))
```

#### Tips

Ports `9042` and `9060` are bound to the same ports on host.

# testcontainers-scala

Working container definition

```scala
override val container = GenericContainer(
    "spotify/cassandra:latest",
    exposedPorts = Seq(9042, 9160),
    waitStrategy = new LogMessageContainsStrategy("Listening for thrift clients")
)
```

#### Tips

`testcontainers` do not allow to define ports redirection. Instead, 
one must use `getMappedPort` method to obtain the port on host.

Also, current `LogMessageWaitStrategy` uses Java regexp check of `string.matches(regEx)` 
that I have found difficult to fulfill. Since checking if `string.contains(regEx)` 
is much easier I created my own `LogMessageContainsStrategy` to wait for during startup.




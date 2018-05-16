package com.github.atais

import java.util.concurrent.{TimeUnit, TimeoutException}
import java.util.function.Predicate

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import org.scalatest.{FlatSpec, Matchers}
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.ContainerLaunchException
import org.testcontainers.containers.output.{OutputFrame, WaitingConsumer}
import org.testcontainers.containers.wait.strategy.{AbstractWaitStrategy, Wait}
import org.testcontainers.utility.LogUtils

import scala.collection.JavaConverters._

class CassandraContainerTest extends FlatSpec with Matchers
  with ForAllTestContainer {

  override val container = GenericContainer(
    "spotify/cassandra:latest",
    exposedPorts = Seq(9042, 9160),
    waitStrategy = new LogMessageContainsStrategy("Listening for thrift clients")
  )

  "Cassandra container" should "start and respond" in {
    assert(container.container.isRunning)
    println(container.container.getBinds.asScala.mkString(","))
    println(container.container.getEnv.asScala.mkString(","))

    println("Test begins")

    val cc = container.container
    val client = new SimpleClient()
    client.connect(cc.getContainerIpAddress, cc.getMappedPort(9042))
    client.getSession()
    client.createSchema()
    client.loadData()
    client.querySchema()
    client.closeSession()
    client.close()
  }

}

class LogMessageContainsStrategy(val regexp: String) extends AbstractWaitStrategy {
  override def waitUntilReady(): Unit = {
    val waitingConsumer = new WaitingConsumer
    LogUtils.followOutput(DockerClientFactory.instance.client, waitStrategyTarget.getContainerId, waitingConsumer)

    val waitPredicate = new Predicate[OutputFrame] {
      override def test(t: OutputFrame): Boolean = t.getUtf8String.contains(regexp)
    }

    try
      waitingConsumer.waitUntil(waitPredicate, 30, TimeUnit.SECONDS, 1)
    catch {
      case e: TimeoutException =>
        throw new ContainerLaunchException("Timed out waiting for log output matching '" + regexp + "'")
    }
  }
}
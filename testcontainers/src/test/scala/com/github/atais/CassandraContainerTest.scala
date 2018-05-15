package com.github.atais

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._

class CassandraContainerTest extends FlatSpec with Matchers
  with ForAllTestContainer {

  override val container = GenericContainer(
    "spotify/cassandra:latest",
    exposedPorts = Seq(9042, 9160)
  )

  "Cassandra container" should "start and respond" in {
    assert(container.container.isRunning)
    println(container.container.getBinds.asScala.mkString(","))
    println(container.container.getEnv.asScala.mkString(","))

    println("Test begins")

    val client = new SimpleClient()
    client.connect("127.0.0.1")
    client.getSession()
    client.createSchema()
    client.loadData()
    client.querySchema()
    client.closeSession()
    client.close()
  }


}

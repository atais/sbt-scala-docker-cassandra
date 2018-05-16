package com.github.atais

import com.whisk.docker.impl.dockerjava.DockerKitDockerJava
import com.whisk.docker.scalatest.DockerTestKit
import com.whisk.docker.{DockerContainer, DockerKit, DockerReadyChecker}
import org.scalatest.{FlatSpec, Matchers}

class CassandraDockerTest extends FlatSpec with Matchers
  with DockerCassandraService with DockerTestKit with DockerKitDockerJava {

  "Cassandra container" should "start and respond" in {
    isContainerReady(cassandraContainer).futureValue shouldBe true
    cassandraContainer.getPorts().futureValue should not be empty
    cassandraContainer.getIpAddresses().futureValue should not be Seq.empty

    println("Test begins")

    val client = new SimpleClient()
    client.connect("127.0.0.1", 9042)
    client.getSession()
    client.createSchema()
    client.loadData()
    client.querySchema()
    client.closeSession()
    client.close()
  }

}

trait DockerCassandraService extends DockerKit {

  val cassandraContainer: DockerContainer = DockerContainer("spotify/cassandra:latest")
    .withPorts(9042 -> Some(9042), 9060 -> Some(9060))
    .withReadyChecker(DockerReadyChecker.LogLineContains("Listening for thrift clients..."))

  abstract override def dockerContainers = cassandraContainer :: super.dockerContainers
}

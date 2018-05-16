package com.github.atais

import com.whisk.docker.impl.dockerjava.DockerKitDockerJava
import com.whisk.docker.scalatest.DockerTestKit
import com.whisk.docker.{DockerContainer, DockerKit, DockerReadyChecker}
import org.scalatest.{FlatSpec, Matchers}

class CassandraDynamicDockerTest extends FlatSpec with Matchers
  with DockerDynamicCassandraService with DockerTestKit with DockerKitDockerJava {

  "Cassandra container" should "start and respond" in {
    isContainerReady(cassandraContainer).futureValue shouldBe true
    cassandraContainer.getPorts().futureValue should not be empty
    cassandraContainer.getIpAddresses().futureValue should not be Seq.empty

    println("Test begins")
    println(cassandraContainer.getPorts().futureValue.apply(9042))

    val client = new SimpleClient()
    client.connect("127.0.0.1", cassandraContainer.getPorts().futureValue.apply(9042))
    client.getSession()
    client.createSchema()
    client.loadData()
    client.querySchema()
    client.closeSession()
    client.close()
  }

}

trait DockerDynamicCassandraService extends DockerKit {

  val cassandraContainer: DockerContainer = DockerContainer("spotify/cassandra:latest")
    .withPorts(9042 -> None, 9060 -> None)
    .withReadyChecker(DockerReadyChecker.LogLineContains("Listening for thrift clients..."))

  abstract override def dockerContainers = cassandraContainer :: super.dockerContainers
}

package com.github.atais

import com.whisk.docker.impl.dockerjava.DockerKitDockerJava
import com.whisk.docker.scalatest.DockerTestKit
import com.whisk.docker.{DockerContainer, DockerKit}
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

class CassandraDockerTest extends FlatSpec with Matchers
  with DockerCassandraService with DockerTestKit with DockerKitDockerJava {

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))

  "Cassandra container" should "start and respond" in {
    isContainerReady(cassandraContainer).futureValue shouldBe true
    cassandraContainer.getPorts().futureValue should not be empty
    cassandraContainer.getIpAddresses().futureValue should not be Seq.empty

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

trait DockerCassandraService extends DockerKit {

  val cassandraContainer: DockerContainer = DockerContainer("spotify/cassandra:latest")
    .withPorts(9042 -> None, 9060 -> None)

  abstract override def dockerContainers = cassandraContainer :: super.dockerContainers
}

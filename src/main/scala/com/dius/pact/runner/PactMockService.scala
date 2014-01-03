package com.dius.pact.runner

import spray.routing.SimpleRoutingApp
import akka.actor.ActorSystem
import com.dius.pact.model.{Interaction, Pact}

object PactMockService extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("pack-mock-system")

  def loadPacts(dir:String):Seq[Pact] = PactFileSource.loadFiles(dir)

  def start(dir:String = "src/test/resources/pacts", interface:String = "localhost", port:Int = 8080) {
  startServer(interface, port) {

    path(Segments) { segs =>
      val path = segs.mkString("/","/","")
      val interactions: Seq[Interaction] = loadPacts(dir).head.interactions.filter(_.request.path == path)

      get {
        val body: String = interactions.head.response.body.getOrElse("{}")
        complete(body)
      } ~
      post {
        entity(as[Option[String]]) { requestBody =>
          val interaction = interactions.filter(_.request.body == requestBody).head
          val body: String = interaction.response.body.getOrElse("{}")
          complete(body)
        }
      }

    }
  }
  }

  start()

}


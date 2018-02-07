package server.echo


import akka.actor.ActorSystem
import colossus.core.{IOSystem, InitContext, ServerContext}
import colossus.protocols.http.{Http, HttpRequest, HttpResponse, HttpServer, Initializer, RequestHandler}
import colossus.protocols.http.HttpMethod._
import colossus.protocols.http.UrlParsing._
import colossus.service.Callback
import colossus.service.GenRequestHandler.PartialHandler

object Main extends App {

    implicit val actorSystem = ActorSystem()
    implicit val ioSystem    = IOSystem()

    HttpServer.start("echo", 9000) { context =>
      new HelloInitializer(context)
    }

  }

class HelloInitializer(context: InitContext) extends Initializer(context) {

  override def onConnect: RequestHandlerFactory = context => new HelloRequestHandler(context)

}

class HelloRequestHandler(context: ServerContext) extends RequestHandler(context) {

  def withBody (req: HttpRequest)(f: String => Callback[HttpResponse]): Callback[HttpResponse] = {
    val bytes = req.body.bytes
    if (bytes.isEmpty) {
      Callback.successful(req.ok("Ok"))
    } else {
      f(bytes.utf8String)
    }
  }

  override def handle: PartialHandler[Http] = {
    case request @ Get on Root => {
      withBody(request) { body =>
        Callback.successful(request.ok(body))
      }
    }
    case request @ Post on Root => {
      withBody(request) { body =>
        Callback.successful(request.ok(body))
      }
    }
  }

}

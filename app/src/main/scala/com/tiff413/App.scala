package com.tiff413

import cats.effect.*
import io.circe.parser.*
import tyrian.*
import tyrian.Html.*
import tyrian.http.*

import scala.scalajs.js
import scala.scalajs.js.annotation.*

enum Msg {
  case NoMsg
  case Error(e: String)
  case TestMsg(s: String)
}

case class Model(shouldICycle: Option[Boolean] = None, testMsg: String)

@JSExportTopLevel("ShouldICycleApp")
object App extends TyrianApp[Msg, Model] {

  def backendCall: Cmd[IO, Msg] =
    Http.send(
      Request.get("http://localhost:4041"),
      Decoder[Msg](
        resp =>
          parse(resp.body).flatMap(_.as[String]) match {
            case Left(e)  => Msg.Error(e.getMessage)
            case Right(s) => Msg.TestMsg(s)
          },
        err => Msg.Error(err.toString)
      )
    )

  override def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    (Model(None, ""), backendCall)

  override def view(model: Model): Html[Msg] =
    div(
      div(s"testMsg: ${model.testMsg}"),
      div (s"Should I Cycle? ${model.shouldICycle}")
    )

  override def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = {
    case Msg.NoMsg      => (model, Cmd.None)
    case Msg.Error(e)   => (model, Cmd.None) // TODO: handle error
    case Msg.TestMsg(s) => (model.copy(testMsg = s), Cmd.None)
  }

  override def subscriptions(model: Model): Sub[IO, Msg] = Sub.None
}

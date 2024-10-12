package com.tiff413.weather

import cats.implicits.*
import cats.effect.{Concurrent, Resource}
import com.tiff413.weather.models.WeatherRequest
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class WeatherRoutes[F[_]: Concurrent] private (weatherService: WeatherService[F]) extends Http4sDsl[F] {
  val routes: HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root =>
    Ok("ye BE server is up")

  //    case req @ POST -> Root / "weather" =>
  //      for {
  //        weatherReq <- req.as[WeatherRequest]
  //      } yield Ok
  }
}

object WeatherRoutes {
  def resource[F[_]: Concurrent](weatherService: WeatherService[F]): Resource[F, WeatherRoutes[F]] =
    Resource.pure(new WeatherRoutes[F](weatherService))
}

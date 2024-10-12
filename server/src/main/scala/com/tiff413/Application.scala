package com.tiff413

import cats.effect.kernel.Resource
import cats.effect.{IO, IOApp}
import cats.implicits.*
import com.comcast.ip4s.{host, port}
import com.tiff413.weather.models.{ForecastHours, Location}
import com.tiff413.weather.{WeatherRoutes, WeatherService}
import eu.timepit.refined.api.Refined
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.CORS
import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.*


object Application extends IOApp.Simple {
  private def makeServer: Resource[IO, Server] = {
    for {
      client         <- EmberClientBuilder.default[IO].build
      weatherService <- WeatherService.resource(client)
      weatherRoutes  <- WeatherRoutes.resource[IO](weatherService)
      corsRoutes = CORS.policy.withAllowOriginAll(weatherRoutes.routes.orNotFound)
      server <- EmberServerBuilder
        .default[IO]
        .withHost(host"0.0.0.0")
        .withPort(port"4041")
        .withHttpApp(corsRoutes)
        .build
    } yield server
  }

  override def run: IO[Unit] =
//    makeServer.use(_ => IO.println("Server ready!") *> IO.never)

    val forecastHours: ForecastHours = ForecastHours(6).toOption.get

    val locations: List[Location] = List(
      Location(52.52, 13.41).toOption.get,
      Location(51.5085, -0.1257).toOption.get,
    )

    val s = for {
      client         <- EmberClientBuilder.default[IO].build
      weatherService <- WeatherService.resource(client)
    } yield weatherService

    for {
      resp <- s.use(ws => ws.getHourlyWeather(locations, forecastHours))
      _ = println(resp)
    } yield ()

}

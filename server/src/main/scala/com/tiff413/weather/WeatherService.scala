package com.tiff413.weather

import cats.syntax.all.*
import cats.Applicative
import cats.data.EitherT
import cats.effect.{Concurrent, IO, Resource}
import com.tiff413.weather.models.WeatherError.{FailedOpenMeteoRequest, InvalidForecastHours}
import com.tiff413.weather.models.{
  ForecastHours,
  Location,
  OpenMeteoHourlyWeatherResponse,
  WeatherError,
  WeatherRequest
}
import org.http4s.*
import org.http4s.client.*
import org.http4s.implicits.*

import java.time.{Duration, Instant}

trait WeatherService[F[_]] {
  def processWeatherRequest(weatherRequest: WeatherRequest): F[Either[WeatherError, Boolean]]
  def getHourlyWeather(locations: List[Location], forecastHours: ForecastHours): F[OpenMeteoHourlyWeatherResponse]
}

object WeatherService {
  private val baseUri: Uri = uri"https://api.open-meteo.com"

  class WeatherServiceImpl[F[_]: Concurrent](client: Client[F]) extends WeatherService[F] {
    override def processWeatherRequest(weatherRequest: WeatherRequest): F[Boolean] = {
      // for now assume waypoints are a reasonable distance apart
      val locations: List[Location] = weatherRequest.route.map(w => Location(w.latitude, w.longitude))

      val timestamps: List[Instant] = weatherRequest.route.map(_.timestampMillis)
      val maxHoursFromNow: Int      = Math.ceil(Duration.between(timestamps.max, timestamps.min).toHours).toInt

      for {
        forecastHours <- EitherT.fromEither(
          ForecastHours(if (maxHoursFromNow == 0) 1 else maxHoursFromNow)
            .leftMap(s => InvalidForecastHours(s))
        )
        resp <- EitherT(getHourlyWeather(locations, forecastHours))
      } yield ()

      val resp =
        // TODO: implement
        Applicative[F].pure(true)
    }

    override def getHourlyWeather(
        locations: List[Location],
        forecastHours: ForecastHours
    ): F[Either[WeatherError, OpenMeteoHourlyWeatherResponse]] = {
      val uri: Uri = baseUri
        .withPath(path"/v1/forecast")
        .withQueryParam("latitude", locations.map(_.latitude).mkString(","))
        .withQueryParam("longitude", locations.map(_.longitude).mkString(","))
        .withQueryParam("forecast_hours", forecastHours.toString)
        .withQueryParam("hourly", "weather_code")
        .withQueryParam("timezone", "UTC")
        .withQueryParam("timeformat", "unixtime")

      println(s"uri: $uri")

      val req = Request[F](Method.GET, uri)

      client
        .expect[OpenMeteoHourlyWeatherResponse](req)
        .attempt
        .map(_.leftMap(t => FailedOpenMeteoRequest(t.getMessage)))
    }
  }

  def resource(client: Client[IO]): Resource[IO, WeatherService[IO]] = Resource.pure(new WeatherServiceImpl(client))
}

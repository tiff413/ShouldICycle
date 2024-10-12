package com.tiff413.weather.models

import cats.effect.Concurrent
import com.tiff413.weather.models.*
import com.tiff413.weather.models.Latitude.*
import com.tiff413.weather.models.Longitude.*
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.circe.jsonOf
import org.http4s.{EntityDecoder, circe}

import java.time.Instant

case class Hourly(
    time: List[Instant],
    weatherCode: List[WeatherCode]
)

object Hourly {
  import com.tiff413.weather.models.InstantUnixTimestampCodecs.*

  implicit val decoder: Decoder[Hourly] = Decoder.forProduct2("time", "weather_code")(Hourly.apply)
}

case class WeatherForLocation(
    latitude: Latitude,
    longitude: Longitude,
    hourly: Hourly
)

object WeatherForLocation {
  import com.tiff413.weather.models.InstantUnixTimestampCodecs.*

  implicit val decoder: Decoder[WeatherForLocation] = deriveDecoder
}

case class OpenMeteoHourlyWeatherResponse(weatherData: List[WeatherForLocation])

object OpenMeteoHourlyWeatherResponse {
  implicit val decoder: Decoder[OpenMeteoHourlyWeatherResponse] =
    Decoder[List[WeatherForLocation]].map(OpenMeteoHourlyWeatherResponse.apply)

  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, OpenMeteoHourlyWeatherResponse] =
    jsonOf[F, OpenMeteoHourlyWeatherResponse]

}

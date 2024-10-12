package com.tiff413.weather.models

import cats.effect.Concurrent
import com.tiff413.weather.models.Latitude.*
import com.tiff413.weather.models.Longitude.*
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import java.time.Instant

case class WeatherRequest(
    route: List[Waypoint]
)

object WeatherRequest {
  implicit val decoder: Decoder[WeatherRequest] = deriveDecoder

  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, WeatherRequest] =
    jsonOf[F, WeatherRequest]
}

case class Waypoint(
    latitude: Latitude,
    longitude: Longitude,
    timestampMillis: Instant
)

object Waypoint {
  import com.tiff413.weather.models.InstantMillisCodecs._

  implicit val decoder: Decoder[Waypoint] = deriveDecoder

}

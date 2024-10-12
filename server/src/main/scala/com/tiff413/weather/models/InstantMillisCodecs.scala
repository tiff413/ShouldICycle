package com.tiff413.weather.models

import io.circe.{Decoder, Encoder}

import java.time.Instant
import scala.util.Try

object InstantMillisCodecs {
  implicit val encodeInstantToEpochMillis: Encoder[Instant] =
    Encoder.encodeLong.contramap[Instant](_.toEpochMilli)

  implicit val decodeInstantFromEpochMillis: Decoder[Instant] = Decoder.decodeLong.emapTry { ts =>
    Try(Instant.ofEpochMilli(ts))
  }
}

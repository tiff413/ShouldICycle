package com.tiff413.weather.models

import io.circe.{Decoder, Encoder}

import java.time.Instant
import scala.util.Try

object InstantUnixTimestampCodecs {
  implicit val encodeInstantToUnixTimestamp: Encoder[Instant] =
    Encoder.encodeLong.contramap[Instant](_.getEpochSecond)

  implicit val decodeInstantFromUnixTimestamp: Decoder[Instant] = Decoder.decodeLong.emapTry { ts =>
    Try(Instant.ofEpochSecond(ts))
  }
}

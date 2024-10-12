package com.tiff413.weather.models

import enumeratum.*
import io.circe.{Decoder, Encoder}

// https://www.nodc.noaa.gov/archive/arc0021/0002199/1.1/data/0-data/HTML/WMO-CODE/WMO4677.HTM
// https://gist.github.com/stellasphere/9490c195ed2b53c707087c8c2db4ec0c
sealed trait WeatherCode extends EnumEntry {
  val value: Int
}

object WeatherCode extends Enum[WeatherCode] {
  override def values: IndexedSeq[WeatherCode] = findValues

  case object SUNNY                         extends WeatherCode { override val value: Int = 0  }
  case object MAINLY_SUNNY                  extends WeatherCode { override val value: Int = 1  }
  case object PARTLY_CLOUDY                 extends WeatherCode { override val value: Int = 2  }
  case object CLOUDY                        extends WeatherCode { override val value: Int = 3  }
  case object FOGGY                         extends WeatherCode { override val value: Int = 45 }
  case object RIME_FOG                      extends WeatherCode { override val value: Int = 48 }
  case object LIGHT_DRIZZLE                 extends WeatherCode { override val value: Int = 51 }
  case object DRIZZLE                       extends WeatherCode { override val value: Int = 53 }
  case object HEAVY_DRIZZLE                 extends WeatherCode { override val value: Int = 55 }
  case object LIGHT_FREEZING_DRIZZLE        extends WeatherCode { override val value: Int = 56 }
  case object FREEZING_DRIZZLE              extends WeatherCode { override val value: Int = 57 }
  case object LIGHT_RAIN                    extends WeatherCode { override val value: Int = 61 }
  case object RAIN                          extends WeatherCode { override val value: Int = 63 }
  case object HEAVY_RAIN                    extends WeatherCode { override val value: Int = 65 }
  case object LIGHT_FREEZING_RAIN           extends WeatherCode { override val value: Int = 66 }
  case object FREEZING_RAIN                 extends WeatherCode { override val value: Int = 67 }
  case object LIGHT_SNOW                    extends WeatherCode { override val value: Int = 71 }
  case object SNOW                          extends WeatherCode { override val value: Int = 73 }
  case object HEAVY_SNOW                    extends WeatherCode { override val value: Int = 75 }
  case object SNOW_GRAINS                   extends WeatherCode { override val value: Int = 77 }
  case object LIGHT_SHOWERS                 extends WeatherCode { override val value: Int = 80 }
  case object SHOWERS                       extends WeatherCode { override val value: Int = 81 }
  case object HEAVY_SHOWERS                 extends WeatherCode { override val value: Int = 82 }
  case object LIGHT_SNOW_SHOWERS            extends WeatherCode { override val value: Int = 85 }
  case object SNOW_SHOWERS                  extends WeatherCode { override val value: Int = 86 }
  case object THUNDERSTORM                  extends WeatherCode { override val value: Int = 95 }
  case object LIGHT_THUNDERSTORMS_WITH_HAIL extends WeatherCode { override val value: Int = 96 }
  case object THUNDERSTORM_WITH_HAIL        extends WeatherCode { override val value: Int = 99 }

  implicit val decoder: Decoder[WeatherCode] =
    Decoder[Int].emap(i => values.find(_.value == i).toRight(s"Invalid WeatherCode: $i"))
  
  implicit val encoder: Encoder[WeatherCode] = Encoder[Int].contramap(_.value)
}

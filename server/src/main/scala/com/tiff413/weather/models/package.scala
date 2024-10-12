package com.tiff413.weather

import cats.implicits.*
import eu.timepit.refined.*
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.*
import eu.timepit.refined.predicates.all.{And, Not}
import io.circe.Decoder

package object models {
  type ForecastHours = Int Refined (Positive And Not[Greater[24]])

  object ForecastHours {
    def apply(int: Int): Either[String, ForecastHours] = refineV[Positive And Not[Greater[24]]](int)
  }
  

}

package com.tiff413.weather.models

import cats.data.NonEmptyList
import cats.implicits.*
import com.tiff413.weather.models.Latitude.*
import com.tiff413.weather.models.Longitude.*
import eu.timepit.refined.*
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

object Latitude {
  // -90 to 90
  type Latitude = Double Refined Interval.Open[-90.0, 90.0]

  def apply(latitude: Double): Either[String, Latitude] =
    // It's annoying that if I want to leftMap, I need to give the refinement predicate in refineV
    // Without leftMap it can just infer from the type annotation
    refineV[Interval.Open[-90.0, 90.0]](latitude).leftMap(e => s"Invalid latitude: $latitude")

  def unsafe(latitude: Double): Latitude = Latitude(latitude).toOption.get

  // Can use import io.circe.refined._ but I like to use my own error message
  implicit def latitudeDecoder: Decoder[Latitude] = Decoder[Double].emap(Latitude.apply)
}

object Longitude {
  // -180 to 180
  type Longitude = Double Refined Interval.Open[-180.0, 180.0]

  def apply(longitude: Double): Either[String, Longitude] = {
    refineV[Interval.Open[-180.0, 180.0]](longitude).leftMap(e => s"Invalid longitude: $longitude")
  }

  def unsafe(longitude: Double): Longitude = Longitude(longitude).toOption.get

  implicit def longitudeDecoder: Decoder[Longitude] = Decoder[Double].emap(Longitude.apply)
}

case class Location(latitude: Latitude, longitude: Longitude)

object Location {
  def apply(latitude: Double, longitude: Double): Either[NonEmptyList[String], Location] =
    val lat: Either[String, Latitude]   = Latitude(latitude)
    val long: Either[String, Longitude] = Longitude(longitude)

    (lat.toValidatedNel, long.toValidatedNel)
      .mapN(Location.apply)
      .toEither

  def unsafe(latitude: Double, longitude: Double): Location = Location(latitude, longitude).toOption.get

  implicit def decoder: Decoder[Location] = deriveDecoder[Location]
}

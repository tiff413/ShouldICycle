package com.tiff413.weather.models

import cats.data.NonEmptyList
import com.tiff413.weather.models.Latitude.Latitude
import com.tiff413.weather.models.Longitude.Longitude
import eu.timepit.refined.*
import eu.timepit.refined.numeric.*

class LocationTest extends munit.FunSuite {
  test("Valid longitude and latitude") {
    val latitude: Latitude   = refineV[Interval.Open[-90.0, 90.0]](10.0).getOrElse(fail("Unexpected"))
    val longitude: Longitude = refineV[Interval.Open[-180.0, 180.0]](20.0).getOrElse(fail("unexpected"))

    assertEquals(Location(10, 20), Right(Location(latitude, longitude)))
  }

  test("Invalid longitude and latitude surfaces both errors") {
    assertEquals(
      Location(-100, 190),
      Left(NonEmptyList.of("Invalid latitude: -100.0", "Invalid longitude: 190.0"))
    )
  }
}

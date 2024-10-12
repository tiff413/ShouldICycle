package com.tiff413.weather

import cats.effect.{IO, Resource}
import com.tiff413.weather.WeatherService.WeatherServiceImpl
import com.tiff413.weather.models.{ForecastHours, Hourly, Location, OpenMeteoHourlyWeatherResponse, WeatherCode, WeatherForLocation}
import io.circe.Json
import io.circe.literal.*
import org.http4s.*
import org.http4s.client.Client
import org.http4s.implicits.*
import org.http4s.circe.*

import java.time.Instant

class WeatherServiceTests extends munit.CatsEffectSuite with munit.Http4sMUnitSyntax {
  val forecastHours: ForecastHours = ForecastHours(6).getOrElse(fail("Unexpected invalid ForecastHours"))

  val locations: List[Location] = List(
    Location(52.52, 13.41).getOrElse(fail("Unexpected invalid location")),
    Location(51.5085, -0.1257).getOrElse(fail("Unexpected invalid location"))
  )

  val expectedTimeStamps: List[Instant] = List(
    Instant.ofEpochSecond(1724605200),
    Instant.ofEpochSecond(1724608800),
    Instant.ofEpochSecond(1724612400),
    Instant.ofEpochSecond(1724616000),
    Instant.ofEpochSecond(1724619600),
    Instant.ofEpochSecond(1724623200)
  )

  val expectedWeatherCodes1: List[WeatherCode] = List.fill(6)(WeatherCode.CLOUDY)
  val expectedWeatherCodes2: List[WeatherCode] = List.fill(5)(WeatherCode.CLOUDY) :+ WeatherCode.PARTLY_CLOUDY

  val expectedBody: Json =
    json"""
[
  {
    "latitude": ${locations(0).latitude.value},
    "longitude": ${locations(0).longitude.value},
    "generationtime_ms": 0.0209808349609375,
    "utc_offset_seconds": 0,
    "timezone": "UTC",
    "timezone_abbreviation": "UTC",
    "elevation": 38,
    "hourly_units": {
      "time": "unixtime",
      "weather_code": "wmo code"
    },
    "hourly": {
      "time": ${expectedTimeStamps.map(_.getEpochSecond)},
      "weather_code": [3, 3, 3, 3, 3, 3]
    }
  },
  {
    "latitude": ${locations(1).latitude.value},
    "longitude": ${locations(1).longitude.value},
    "generationtime_ms": 0.00798702239990234,
    "utc_offset_seconds": 0,
    "timezone": "UTC",
    "timezone_abbreviation": "UTC",
    "elevation": 23,
    "location_id": 1,
    "hourly_units": {
      "time": "unixtime",
      "weather_code": "wmo code"
    },
    "hourly": {
      "time": ${expectedTimeStamps.map(_.getEpochSecond)},
      "weather_code": [3, 3, 3, 3, 3, 2]
    }
  }
]"""

  val expectedResponse: Response[IO] = Response[IO](status = Status.Ok)
    .withEntity(expectedBody)

  val testUri: Uri = Uri
    .fromString(
      "https://api.open-meteo.com/v1/forecast" +
        "?latitude=52.52%2C51.5085" +
        "&longitude=13.41%2C-0.1257" +
        "&forecast_hours=6" +
        "&hourly=weather_code" +
        "&timezone=UTC" +
        "&timeformat=unixtime"
    )
    .getOrElse(fail("Unexpected invalid testUri"))

  val mockClient: Client[IO] = Client[IO] { req =>
    if (req.uri == testUri) Resource.pure(expectedResponse)
    else NotFound("Unexpected uri").toResource
  }

  val service = new WeatherServiceImpl[IO](mockClient)

  test("OpenMeteoClient") {
    val expectedWeatherResponse = OpenMeteoHourlyWeatherResponse(
      List(
        WeatherForLocation(
          locations(0).latitude,
          locations(0).longitude,
          Hourly(expectedTimeStamps, expectedWeatherCodes1)
        ),
        WeatherForLocation(
          locations(1).latitude,
          locations(1).longitude,
          Hourly(expectedTimeStamps, expectedWeatherCodes2)
        )
      )
    )

    for {
      weatherResp <- service.getHourlyWeather(locations, forecastHours)
    } yield assertEquals(weatherResp, expectedWeatherResponse)

  }

}

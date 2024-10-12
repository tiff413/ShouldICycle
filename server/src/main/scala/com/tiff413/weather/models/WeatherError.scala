package com.tiff413.weather.models

sealed trait WeatherError

object WeatherError {
  case class InvalidForecastHours(msg: String) extends WeatherError
  case class FailedOpenMeteoRequest(msg: String) extends WeatherError
}
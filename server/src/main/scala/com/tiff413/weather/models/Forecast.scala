package com.tiff413.weather.models

import java.time.Instant

case class WeatherRecord(ts: Instant, weatherCode: WeatherCode)

type Forecast = List[WeatherRecord]
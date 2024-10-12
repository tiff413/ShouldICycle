import com.tiff413.weather.models.Location.Latitude
import eu.timepit.refined.*
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto.*
import eu.timepit.refined.numeric.*
import eu.timepit.refined.predicates.all.{And, Not}

type ForecastHours = Int Refined (Positive And Not[Greater[24]])

val a: Either[String, ForecastHours] = refineV[Positive And Not[Greater[24]]](1)

package kornell.repository.slick

import javax.ws.rs.core.SecurityContext
import scala.slick.session.PositionedParameters
import scala.slick.jdbc.SetParameter
import java.util.Date
import java.sql.Timestamp
import scala.slick.session.PositionedResult

package object plain {
  implicit def toPrincipal(implicit sc: SecurityContext) = sc.getUserPrincipal
}

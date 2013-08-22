package kornell.server.api

import javax.ws.rs.core.SecurityContext

trait Resource {
	implicit def toUsername(implicit sc: SecurityContext): String = sc.getUserPrincipal.getName
}
package kornell.server.dev

import java.util.UUID

object UUIDGenerator extends App {
	for {_ <- 1 to 10} println(UUID.randomUUID)
}
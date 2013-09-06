package kornell.server.repository

import java.sql.Connection
package object jdbc {
  type ConnectionFactory = () => Connection
}
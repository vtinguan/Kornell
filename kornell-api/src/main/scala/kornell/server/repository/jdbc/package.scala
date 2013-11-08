package kornell.server.repository

import java.sql.Connection
package object jdbc {
  type UUID = String
  type ConnectionFactory = () => Connection
}
package kornell.server.repository.jdbc
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import kornell.core.event.ActomEntered

object Events {
  def logActomEntered(event: ActomEntered) = sql"""
    insert into ActomEntered(uuid,person_uuid,actom_key,eventFiredAt)
    values(${event.getUUID()},
           ${event.getFromPersonUUID()},
		   ${event.getActomKey()},
		   ${event.getEventFiredAt()});
	""".executeUpdate

  /*
  def findActomsEntered(p: Person) = sql"""
  	
  """
  */
}
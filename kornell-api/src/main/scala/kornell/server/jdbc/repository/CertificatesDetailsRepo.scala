package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.jdbc.SQL._ 
import kornell.core.entity.CertificateDetails
import kornell.core.util.UUID
import kornell.core.entity.CourseDetailsEntityType

object CertificatesDetailsRepo {
  
   def create(certificateDetails: CertificateDetails): CertificateDetails = {
    if (certificateDetails.getUUID == null){
      certificateDetails.setUUID(UUID.random)
    }    
    sql"""
    | insert into CertificateDetails (uuid,bgImage,entityType,entityUUID) 
    | values(
    | ${certificateDetails.getUUID},
    | ${certificateDetails.getBgImage},
    | ${certificateDetails.getEntityType.toString}, 
    | ${certificateDetails.getEntityUUID})""".executeUpdate
    
    certificateDetails
  }
  
  def listForEntity(entityUUIDs: List[String], entityType: CourseDetailsEntityType): Map[String, List[CertificateDetails]] = {
    sql"""
      select * from CertificateDetails where entityUUID in (${entityUUIDs mkString ","}) and entityType = ${entityType.toString}
    """.map[CertificateDetails].groupBy { x => x.getEntityUUID }
  }
}
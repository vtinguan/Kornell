package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.core.entity.CourseVersion
import kornell.server.jdbc.SQL._
import kornell.core.to.CourseVersionTO
import kornell.core.to.CourseClassesTO
import kornell.server.repository.TOs

class CourseVersionRepo(uuid: String) {

  val finder = sql"select * from CourseVersion where uuid=$uuid"

  def get = finder.get[CourseVersion]
  def first = finder.first[CourseVersion]
  
  def getWithCourse: CourseVersionTO = {
    sql"""
    | select 
    | cv.uuid as courseVersionUUID,
    | cv.name as courseVersionName,
    | cv.repository_uuid as repositoryUUID,
    | cv.course_uuid as courseUUID,
    | cv.versionCreatedAt as versionCreatedAt,
    | cv.distributionPrefix as distributionPrefix,
    | cv.contentSpec as contentSpec,
    | cv.disabled as courseVersionDisabled,
    | c.uuid as courseUUID,
    | c.code as courseCode,
    | c.title as courseTitle,
    | c.description as courseDescription,
    | c.infoJson as infoJson,
    | c.institutionUUID as institutionUUID
    | from CourseVersion cv 
    | join Course c on cv.course_uuid = c.uuid
    | where cv.uuid = ${uuid}""".map[CourseVersionTO](toCourseVersionTO).head
  }
  
  def update(courseVersion: CourseVersion): CourseVersion = {
    sql"""
    | update CourseVersion c
    | set c.name = ${courseVersion.getName},
    | c.repository_uuid = ${courseVersion.getRepositoryUUID},
    | c.course_uuid = ${courseVersion.getCourseUUID}, 
    | c.versionCreatedAt = ${courseVersion.getVersionCreatedAt},
    | c.distributionPrefix = ${courseVersion.getDistributionPrefix},
    | c.disabled = ${courseVersion.isDisabled}
    | where c.uuid = ${courseVersion.getUUID}""".executeUpdate
    courseVersion
  }
  
}

object CourseVersionRepo {
  def apply(uuid: String) = new CourseVersionRepo(uuid: String)
}
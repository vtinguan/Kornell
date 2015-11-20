package kornell.server.api

import org.junit.runner.RunWith
import kornell.server.helper.GenInstitutionAdmin
import kornell.server.test.UnitSpec
import kornell.server.helper.GenPlatformAdmin
import org.scalatest.junit.JUnitRunner
import kornell.server.helper.GenCourseClass
import kornell.server.repository.Entities
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import kornell.server.util.RequirementNotMet
import kornell.server.jdbc.repository.CourseVersionsRepo

@RunWith(classOf[JUnitRunner])
class CourseVersionSpec extends UnitSpec 
    with GenPlatformAdmin
    with GenInstitutionAdmin
    with GenCourseClass {
  
  "The platformAdmin" should "be able to get a courseVersion" in asPlatformAdmin {
    val courseVersionTO = CourseVersionResource(courseVersionUUID).get
    assert(courseVersionTO.getCourseVersion.getUUID == courseVersionUUID)
  }
  
  "The institutionAdmin" should "be able to get a courseVersion" in asInstitutionAdmin {
    val courseVersionTO = CourseVersionResource(courseVersionUUID).get
    assert(courseVersionTO.getCourseVersion.getUUID == courseVersionUUID)
  }
  
  "A person" should "be able to get a courseVersion" in asPlatformAdmin {
    val createdCourseVersionUUID = courseVersionUUID
    asPerson {
      try {
        val courseVersionTO = CourseVersionResource(createdCourseVersionUUID).get
        throw new Throwable
      } catch {
        case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
        case default:Throwable => fail() 
      }
    }
  }
  
  "The platformAdmin" should "be able to create a courseVersion" in asPlatformAdmin {
    courseVersionUUID.size should be > 0
  }
  
  "The institutionAdmin" should "be able to create a courseVersion" in asInstitutionAdmin {
    courseVersionUUID.size should be > 0
  }
  
  "A person" should "not be able to create a courseVersion" in asPlatformAdmin {
    val repositoryUUID = ContentRepositoriesRepo().createS3Repository("", "", "", institutionUUID = institutionUUID, region = "us-east-1").getUUID
    asPerson {
      try {
        CourseVersionsResource().create(Entities.newCourseVersion(repositoryUUID=repositoryUUID, courseUUID = courseUUID))
        throw new Throwable
      } catch {
        case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
        case default:Throwable => fail() 
      }
    }
  }
  
  "The platformAdmin" should "be able to update a courseVersion" in asPlatformAdmin {
    val repositoryUUID = ContentRepositoriesRepo().createS3Repository("", "", "", institutionUUID = institutionUUID, region = "us-east-1").getUUID
    val name = "test name"
    val updatedCourseVersion = CourseVersionResource(courseVersionUUID).update(Entities.newCourseVersion(uuid = courseVersionUUID, repositoryUUID=repositoryUUID, courseUUID = courseUUID, name = name))
    assert(name == updatedCourseVersion.getName)
  }
  
  "The institutionAdmin" should "be able to update a courseVersion" in asInstitutionAdmin {
    val repositoryUUID = ContentRepositoriesRepo().createS3Repository("", "", "", institutionUUID = institutionUUID, region = "us-east-1").getUUID
    val name = "test name1"
    val updatedCourseVersion = CourseVersionResource(courseVersionUUID).update(Entities.newCourseVersion(uuid = courseVersionUUID, repositoryUUID=repositoryUUID, courseUUID = courseUUID, name = name))
    assert(name == updatedCourseVersion.getName)
  }

  "A person" should "not be able to update a courseVersion" in asPlatformAdmin {
    val repositoryUUID = ContentRepositoriesRepo().createS3Repository("", "", "", institutionUUID = institutionUUID, region = "us-east-1").getUUID
    val name = "test name1"
      asPerson {
        try {
          val updatedCourseVersion = CourseVersionResource(courseVersionUUID).update(Entities.newCourseVersion(uuid = courseVersionUUID, repositoryUUID=repositoryUUID, courseUUID = courseUUID, name = name))
          throw new Throwable
        } catch {
          case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
          case default:Throwable => fail() 
        }
    }
  }
}
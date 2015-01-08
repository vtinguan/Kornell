package kornell.server.test.api

import scala.collection.JavaConverters._
import org.junit.runner.RunWith
import org.jboss.arquillian.junit.Arquillian
import kornell.server.api.InstitutionsResource
import javax.inject.Inject
import org.junit.Test
import kornell.server.test.Mocks
import kornell.server.test.KornellSuite
import kornell.server.repository.Entities
import kornell.server.util.Err
import kornell.core.entity.Institution
import kornell.core.entity.Person

@RunWith(classOf[Arquillian])
class InstitutionResourceSuite extends KornellSuite {
  @Inject var ittsRes: InstitutionsResource = _
  @Inject var mocks: Mocks = _

  def createInstitution = ittsRes.create(Entities.newInstitution(
    name = randStr,
    baseURL = randURL))

  @Test def platfAdmCanCreateInstitution =
    runAs(mocks.platfAdm) { createInstitution }

  //TODO: Expect specific err type
  @Test(expected = classOf[Err]) def institutionAdminCanNotCreateInstitution =
    runAs(mocks.ittAdm) { createInstitution }

  @Test(expected = classOf[Err]) def studentCanNotCreateInstitution =
    runAs(mocks.student) { createInstitution }

  @Test def platfAdmCanUpdateInstitution = runAs(mocks.platfAdm) {
    val newInstitution = createInstitution
    val newName = randStr
    newInstitution.setFullName(newName);
    val modifiedInstitution = ittsRes.get(newInstitution.getUUID).update(newInstitution)
    assert(newName == modifiedInstitution.getFullName)
  }

  @Test(expected = classOf[Err]) def ittAdmCanNotUpdateInstitution = {
    var newInstitution: Institution = null
    runAs(mocks.platfAdm) {
      newInstitution = createInstitution
    }
    runAs(mocks.ittAdm) {
      newInstitution.setFullName("test");
      ittsRes.get(newInstitution.getUUID).update(newInstitution)
    }
  }

  @Test(expected = classOf[Err]) def studentCanNotModifyAnInstitution = {
    var newInstitution: Institution = null
    runAs(mocks.platfAdm) {
      newInstitution = createInstitution
      newInstitution.setFullName("test");
    }
    runAs(mocks.student) {
      ittsRes.get(newInstitution.getUUID).update(newInstitution)
    }
  }

  @Test def platfAdmnCanGetAnInstitution = runAs(mocks.platfAdm) {
    val newInstitution = createInstitution
    val fetchedInstitution = ittsRes.get(newInstitution.getUUID).get
    assert(newInstitution.getFullName == fetchedInstitution.getFullName)
  }

  def createAndGetInstitution(getAs: Person) = {
    var newInstitution: Institution = null
    runAs(mocks.platfAdm) {
      newInstitution = createInstitution
    }
    runAs(getAs) {
      ittsRes.get(newInstitution.getUUID).get
    }
  }

  @Test(expected = classOf[Err]) def institutionAdminCanNotGetInstitution =
    createAndGetInstitution(mocks.ittAdm)

  @Test(expected = classOf[Err]) def studentShouldNotGetInstitution = 
    createAndGetInstitution(mocks.student)
  
}
  
  /*
  "The platformAdmin" should "be able to get the list of institution admins" in asPlatformAdmin { 
    val institutionAdmin = institutionAdminUUID
    val admins = ittsRes.get(institutionUUID).getAdmins(institutionAdmin)
    
    assert(admins.getRoleTOs.get(0).getRole().getPersonUUID== institutionAdmin)
  }
  
  "The institutionAdmin" should "not be able to get the list of institution admins" in asInstitutionAdmin { 
    val institutionAdmin = institutionAdminUUID
    try {
      val admins = ittsRes.get(institutionUUID).getAdmins(institutionAdmin)
      throw new Throwable
    } catch {
      case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
      case default:Throwable => fail()
    }
  }
  
  "A person" should "not be able to get the list of institution admins" in asPlatformAdmin { 
    val institutionAdmin = institutionAdminUUID
    asPerson {
      try {
          val admins = ittsRes.get(institutionUUID).getAdmins(institutionAdmin)
          throw new Throwable
      } catch {
          case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
          case default:Throwable => fail()
      }
    }
  }
  
  "The platformAdmin" should "be able to update the list of institution admins" in asPlatformAdmin { 
    val institutionAdmin = institutionAdminUUID
    val admins = Entities.newRoles(List(Entities.newRoleAsPlatformAdmin(institutionAdmin)))
    val updatedAdmins = ittsRes.get(institutionUUID).updateAdmins(admins)
    
    assert(updatedAdmins.getRoles.get(0).getPersonUUID == institutionAdmin)
  }
  
  "The institutionAdmin" should "not be able to update the list of institution admins" in asInstitutionAdmin { 
    val institutionAdmin = institutionAdminUUID
    val admins = Entities.newRoles(List(Entities.newRoleAsPlatformAdmin(institutionAdmin)))
    try {
      val updatedAdmins = ittsRes.get(institutionUUID).updateAdmins(admins)
      throw new Throwable
    } catch {
      case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
      case default:Throwable => fail()
    }
  }
  
  "A person" should "not be able to update the list of institution admins" in asPlatformAdmin { 
    val institutionAdmin = institutionAdminUUID
    val admins = Entities.newRoles(List(Entities.newRoleAsPlatformAdmin(institutionAdmin)))
    asPerson {
      try {
          val updatedAdmins = ittsRes.get(institutionUUID).updateAdmins(admins)
          throw new Throwable
      } catch {
          case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
          case default:Throwable => fail()
      }
    }
  }
  
  "The platformAdmin" should "be able to update the list of hostanmes" in asPlatformAdmin { 
    val hostname = randStr(10)
    val createdHostnames = ittsRes.get(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
    
    assert(createdHostnames.getInstitutionHostNames.get(0) == hostname)
  }
  
  "The platformAdmin" should "be able to get the list of hostanmes" in asPlatformAdmin { 
    val hostname = randStr(10)
    ittsRes.get(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
    val fetchedHostnames = ittsRes.get(institutionUUID).getHostnames
    
    assert(fetchedHostnames.getInstitutionHostNames.get(0) == hostname)
  }
  
  "The institutionAdmin" should "not be able to update the list of hostanmes" in asInstitutionAdmin { 
    val hostname = randStr(10)
    try {
      val createdHostnames = ittsRes.get(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
      throw new Throwable
    } catch {
      case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
      case default:Throwable => fail()
    }
  }
  
  "The institutionAdmin" should "not be able to get the list of hostanmes" in asPlatformAdmin { 
    val hostname = randStr(10)
    ittsRes.get(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
    
    asInstitutionAdmin {
      try {
          val fetchedHostnames = ittsRes.get(institutionUUID).getHostnames
          throw new Throwable
      } catch {
          case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
          case default:Throwable => fail()
      }
    }
  }
  
  "A person" should "not be able to update the list of hostanmes" in asPerson { 
    val hostname = randStr(10)
    try {
      val createdHostnames = ittsRes.get(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
      throw new Throwable
    } catch {
      case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
      case default:Throwable => fail()
    }
  }
  
  "A person" should "not be able to get the list of hostanmes" in asPlatformAdmin { 
    val hostname = randStr(10)
    ittsRes.get(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
    
    asPerson {
      try {
          val fetchedHostnames = ittsRes.get(institutionUUID).getHostnames
          throw new Throwable
      } catch {
          case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
          case default:Throwable => fail()
      }
    }
  }
  
  */

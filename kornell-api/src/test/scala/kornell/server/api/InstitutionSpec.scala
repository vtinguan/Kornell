package kornell.server.api

import org.junit.runner.RunWith
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import kornell.server.helper.GenInstitutionAdmin
import kornell.server.helper.GenPlatformAdmin
import kornell.server.repository.Entities
import kornell.server.test.UnitSpec
import kornell.server.util.AccessDeniedErr
import kornell.server.util.RequirementNotMet
import org.scalatest.junit.JUnitRunner
import kornell.server.util.KornellErr
import scala.collection.JavaConverters._

//TODO need tests for GET/PUT hostnames
@RunWith(classOf[JUnitRunner])
class InstitutionSpec extends UnitSpec 
    with GenPlatformAdmin
    with GenInstitutionAdmin {

  "The platformAdmin" should 
  "be able to create a new institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, false, false, false, null, ""))
  }
  
  "The institutionAdmin" should 
  "not be able to create a new institution" in asInstitutionAdmin {
    try {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, false, false, false, null, ""))
    } catch {
      case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
      case default:Throwable => fail() 
    }
  }
  
  "A person" should 
  "not be able to create a new institution" in asPerson {
    try {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, false, false, false, null, ""))
    } catch {
      case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
      case default:Throwable => fail() 
    }
  }
  
  "The platformAdmin" should 
  "be able to modify an institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, false, false, false, null, ""))
    newInstitution.setFullName("test");
    val modifiedInstitution = InstitutionResource(newInstitution.getUUID).update(newInstitution)
    assert("test" == modifiedInstitution.getFullName)
  }
  
  "The institutionAdmin" should 
  "not be able to modify an institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, false, false, false, null, ""))
    newInstitution.setFullName("test");
    asInstitutionAdmin {
      try {
        val modifiedInstitution = InstitutionResource(newInstitution.getUUID).update(newInstitution)
      } catch {
        case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
        case default:Throwable => fail() 
      }
    }
  }
  
  "A person" should 
  "not be able to modify an institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, false, false, false, null, ""))
    newInstitution.setFullName("test");
    asPerson {
      try {
        val modifiedInstitution = InstitutionResource(newInstitution.getUUID).update(newInstitution)
      } catch {
        case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
        case default:Throwable => fail() 
      }
    }
  }
  
  "The platformAdmin" should 
  "be able to get an institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, false, false, false, null, ""))

    val fetchedInstitution = InstitutionResource(newInstitution.getUUID).get
    assert(newInstitution.getFullName == fetchedInstitution.getFullName)
    
  }
  
  "The institutionAdmin" should 
  "not be able to get an institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, false, false, false, null, ""))

    asInstitutionAdmin{
        try {
            val fetchedInstitution = InstitutionResource(newInstitution.getUUID).get
        } catch {
          case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
          case default:Throwable => fail() 
        }
    }
  }
  
  "The person" should 
  "not be able to get an institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, false, false, false, null, ""))

    asPerson{
        try {
            val fetchedInstitution = InstitutionResource(newInstitution.getUUID).get
        } catch {
          case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
          case default:Throwable => fail() 
        }
    }
  }
  
  "The platformAdmin" should "be able to get the list of institution admins" in asPlatformAdmin { 
    val institutionAdmin = institutionAdminUUID
    val admins = InstitutionResource(institutionUUID).getAdmins(institutionAdmin)
    
    assert(admins.getRoleTOs.get(0).getRole().getPersonUUID== institutionAdmin)
  }
  
  "The institutionAdmin" should "not be able to get the list of institution admins" in asInstitutionAdmin { 
    val institutionAdmin = institutionAdminUUID
    try {
      val admins = InstitutionResource(institutionUUID).getAdmins(institutionAdmin)
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
          val admins = InstitutionResource(institutionUUID).getAdmins(institutionAdmin)
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
    val updatedAdmins = InstitutionResource(institutionUUID).updateAdmins(admins)
    
    assert(updatedAdmins.getRoles.get(0).getPersonUUID == institutionAdmin)
  }
  
  "The institutionAdmin" should "not be able to update the list of institution admins" in asInstitutionAdmin { 
    val institutionAdmin = institutionAdminUUID
    val admins = Entities.newRoles(List(Entities.newRoleAsPlatformAdmin(institutionAdmin)))
    try {
      val updatedAdmins = InstitutionResource(institutionUUID).updateAdmins(admins)
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
          val updatedAdmins = InstitutionResource(institutionUUID).updateAdmins(admins)
          throw new Throwable
      } catch {
          case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
          case default:Throwable => fail()
      }
    }
  }
}
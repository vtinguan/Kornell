package kornell.server.api

import scala.collection.JavaConverters._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import kornell.server.helper.GenInstitutionAdmin
import kornell.server.helper.GenPlatformAdmin
import kornell.server.repository.Entities
import kornell.server.repository.TOs
import kornell.server.test.UnitSpec
import kornell.server.util.KornellErr
import kornell.core.entity.BillingType
import kornell.core.entity.InstitutionType
import kornell.server.util.RequirementNotMet

//TODO need tests for GET/PUT hostnames
@RunWith(classOf[JUnitRunner])
class InstitutionSpec extends UnitSpec 
    with GenPlatformAdmin
    with GenInstitutionAdmin {

  "The platformAdmin" should 
  "be able to create a new institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, false, false, false, false, null, "", BillingType.enrollment, InstitutionType.DEFAULT, null, false, false, null, "America/Sao_Paulo"))
  }
  
  "The institutionAdmin" should 
  "not be able to create a new institution" in asInstitutionAdmin {
    try {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, false, false, false, false, null, "", BillingType.enrollment, InstitutionType.DEFAULT, null, false, false, null, "America/Sao_Paulo"))
    } catch {
      case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
      case default:Throwable => fail() 
    }
  }
  
  "A person" should 
  "not be able to create a new institution" in asPerson {
    try {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, false, false, false, false, null, "", BillingType.enrollment, InstitutionType.DEFAULT, null, false, false, null, "America/Sao_Paulo"))
    } catch {
      case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
      case default:Throwable => fail() 
    }
  }
  
  "The platformAdmin" should 
  "be able to modify an institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, false, false, false, false, null, "", BillingType.enrollment, InstitutionType.DEFAULT, null, false, false, null, "America/Sao_Paulo"))
    newInstitution.setFullName("test");
    val modifiedInstitution = InstitutionResource(newInstitution.getUUID).update(newInstitution)
    assert("test" == modifiedInstitution.getFullName)
  }
  
  "The institutionAdmin" should 
  "not be able to modify an institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, false, false, false, false, null, "", BillingType.enrollment, InstitutionType.DEFAULT, null, false, false, null, "America/Sao_Paulo"))
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
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, false, false, false, false, null, "", BillingType.enrollment, InstitutionType.DEFAULT, null, false, false, null, "America/Sao_Paulo"))
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
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, false, false, false, false, null, "", BillingType.enrollment, InstitutionType.DEFAULT, null, false, false, null, "America/Sao_Paulo"))

    val fetchedInstitution = InstitutionResource(newInstitution.getUUID).get
    assert(newInstitution.getFullName == fetchedInstitution.getFullName)
    
  }
  
  "The institutionAdmin" should 
  "not be able to get an institution" in asPlatformAdmin {
    val newInstitution = InstitutionsResource().create( 
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, false, false, false, false, null, "", BillingType.enrollment, InstitutionType.DEFAULT, null, false, false, null, "America/Sao_Paulo"))

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
        Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, false, false, false, false, null, "", BillingType.enrollment, InstitutionType.DEFAULT, null, false, false, null, "America/Sao_Paulo"))

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
    val admins = Entities.newRoles(List(Entities.newRoleAsPlatformAdmin(institutionAdmin, institutionUUID)))
    val updatedAdmins = InstitutionResource(institutionUUID).updateAdmins(admins)
    
    assert(institutionAdmin == institutionAdmin)
  }
  
  "The institutionAdmin" should "not be able to update the list of institution admins" in asInstitutionAdmin { 
    val institutionAdmin = institutionAdminUUID
    val admins = Entities.newRoles(List(Entities.newRoleAsPlatformAdmin(institutionAdmin, institutionUUID)))
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
    val admins = Entities.newRoles(List(Entities.newRoleAsPlatformAdmin(institutionAdmin, institutionUUID)))
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
  
  "The platformAdmin" should "be able to update the list of hostanmes" in asPlatformAdmin { 
    val hostname = randStr(10)
    val createdHostnames = InstitutionResource(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
    
    assert(createdHostnames.getInstitutionHostNames.get(0) == hostname)
  }
  
  "The platformAdmin" should "be able to get the list of hostanmes" in asPlatformAdmin { 
    val hostname = randStr(10)
    InstitutionResource(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
    val fetchedHostnames = InstitutionResource(institutionUUID).getHostnames
    
    assert(fetchedHostnames.getInstitutionHostNames.get(0) == hostname)
  }
  
  "The institutionAdmin" should "not be able to update the list of hostanmes" in asInstitutionAdmin { 
    val hostname = randStr(10)
    try {
      val createdHostnames = InstitutionResource(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
      throw new Throwable
    } catch {
      case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
      case default:Throwable => fail()
    }
  }
  
  "The institutionAdmin" should "not be able to get the list of hostanmes" in asPlatformAdmin { 
    val hostname = randStr(10)
    InstitutionResource(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
    
    asInstitutionAdmin {
      try {
          val fetchedHostnames = InstitutionResource(institutionUUID).getHostnames
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
      val createdHostnames = InstitutionResource(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
      throw new Throwable
    } catch {
      case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
      case default:Throwable => fail()
    }
  }
  
  "A person" should "not be able to get the list of hostanmes" in asPlatformAdmin { 
    val hostname = randStr(10)
    InstitutionResource(institutionUUID).updateHostnames(TOs.newInstitutionHostNamesTO(List(hostname)))
    
    asPerson {
      try {
          val fetchedHostnames = InstitutionResource(institutionUUID).getHostnames
          throw new Throwable
      } catch {
          case ade: KornellErr => assert(ade.getCode == "403_ACCESSDENIED")
          case default:Throwable => fail()
      }
    }
  }
}
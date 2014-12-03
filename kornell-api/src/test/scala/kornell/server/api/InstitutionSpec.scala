package kornell.server.api

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import kornell.server.test.UnitSpec
import kornell.server.helper.GenPlatformAdmin
import kornell.server.repository.Entities
import kornell.server.helper.GenInstitutionAdmin
import kornell.server.util.RequirementNotMet

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
}
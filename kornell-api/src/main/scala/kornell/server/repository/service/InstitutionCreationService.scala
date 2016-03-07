package kornell.server.repository.service

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.identitymanagement.model.CreateUserRequest
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest
import com.amazonaws.services.identitymanagement.model.PutUserPolicyRequest
import org.apache.commons.io.FileUtils
import java.io.File

object InstitutionCreationService {
	def createInstitutionStuff() = {
	  val client = new AmazonIdentityManagementClient(new BasicAWSCredentials("", ""))
    //create user and key
    val newUser = client.createUser(new CreateUserRequest("test_gg"))
    val newKey = client.createAccessKey(new CreateAccessKeyRequest("test_gg"))
    println("ID: " + newKey.getAccessKey().getAccessKeyId() + " Secret: " + newKey.getAccessKey().getSecretAccessKey())
    
    //create bucket
    val s3Client = new AmazonS3Client(new BasicAWSCredentials("", ""))
    val bucket = s3Client.createBucket("test-bucket")
    
    //create user policy
    val putPolicyReq = new PutUserPolicyRequest().withPolicyName("NewPolicyGG-20160306").withPolicyDocument(FileUtils.readFileToString(new File("/Users/gravelg/policy.txt"))).withUserName("test_gg")
    client.putUserPolicy(putPolicyReq)
	}
}
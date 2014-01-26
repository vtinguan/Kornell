package kornell.server.api

import javax.servlet.http.HttpServletResponse
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import kornell.server.util.ReportGenerator
import kornell.core.to.S3PolicyTO
import kornell.server.util.Base64
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac
import java.security.Signature
import kornell.core.to.TOFactory
import kornell.server.repository.TOs
import javax.ws.rs.core.SecurityContext
import kornell.server.util.HmacSHA1
import kornell.server.repository.s3.S3
import kornell.server.jdbc.repository.AuthRepo

@Path("/s3")
class S3Resource {

	@GET
	@Produces(Array(S3PolicyTO.TYPE))
	def get(implicit @Context sc:SecurityContext) = AuthRepo.withPerson{ 
	  person => {
	    //TODO
		val s3 = S3("840e93aa-2373-4fb5-ba4a-999bb3f43888")
		
		val accessKeyId = s3.accessKey
		val secretAccesKey = s3.secretKey
		val bucketName = "eduvemfiles"//s3.bucket
		val key = "profilepictures/"
		val successActionRedirect = "http://"+bucketName+".s3.amazonaws.com"
		
		  
	  
	    val x = """{ \"expiration\": \"2030-12-01T12:00:00.000Z\",\"conditions\": [
		      {\"bucket\": \" """+bucketName+""" \"},
		      [\"starts-with\", \"$key\", \" """+key+""" \"],
		      {\"acl\": \"public-read\"},
		      {\"success_action_redirect\": \" """+successActionRedirect+""" \"},
		      [\"starts-with\", \"$Content-Type\", \"image/\"],
		      {\"x-amz-meta-uuid\": \"14365123651275\"},
		      [\"starts-with\", \"$x-amz-meta-tag\", \"\"]
	      ]}"""
		println(x)
		
	    val policy = Base64.encode(x.getBytes)
		println(policy)
		
		val signature = HmacSHA1.calculateRFC2104HMAC(policy, secretAccesKey)
		println(signature)
		
	    val s3PolicyTO = TOs.newS3PolicyTO
	    
	    s3PolicyTO.setAWSAccessKeyId(accessKeyId)
	    s3PolicyTO.setPolicy(policy)
	    s3PolicyTO.setSignature(signature)	    
	    s3PolicyTO.setBucketName(bucketName)
	    s3PolicyTO.setKey(key)
	    s3PolicyTO.setSuccessActionRedirect(successActionRedirect)
	    
	    s3PolicyTO
	  }
	}
}
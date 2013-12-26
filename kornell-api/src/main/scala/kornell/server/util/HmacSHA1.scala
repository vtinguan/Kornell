package kornell.server.util

import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac

object HmacSHA1 {
  
    def HMAC_SHA1_ALGORITHM = "HmacSHA1"

	def calculateRFC2104HMAC(data: String, key: String) = {
		
		// get an hmac_sha1 key from the raw key bytes
		val signingKey = new SecretKeySpec(key.getBytes, HMAC_SHA1_ALGORITHM)

		// get an hmac_sha1 Mac instance and initialize with the signing key
		val mac = Mac.getInstance(HMAC_SHA1_ALGORITHM)
		mac.init(signingKey)

		// compute the hmac on input data bytes
		val rawHmac = mac.doFinal(data.getBytes)

		// base64-encode the hmac
		Base64.encode(rawHmac)
	}
	
}
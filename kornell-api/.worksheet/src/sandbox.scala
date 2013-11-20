import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.security.KeyFactory

object sandbox {
/*
  val keyGen = KeyPairGenerator.getInstance("DSA", "SUN")
  val random = SecureRandom.getInstance("SHA1PRNG", "SUN")
  keyGen.initialize(1024, random);

  val pair = keyGen.generateKeyPair();
  val priv = pair.getPrivate();
  val pub = pair.getPublic();
  val dsa = Signature.getInstance("SHA1withDSA", "SUN");
	dsa.initSign(priv);
	
	val data = "SECRET".getBytes
	
	dsa.update(data)
	
	val sign = dsa.sign()
	/// Verify
	val encKey = pub.getEncoded()
	val pubKeySpec = new X509EncodedKeySpec(encKey)
	
  val ver = Signature.getInstance("SHA1withDSA", "SUN")
  val keyFactory = KeyFactory.getInstance("DSA", "SUN")
  
  val pubKey = keyFactory.generatePublic(pubKeySpec);
  ver.initVerify(pubKey)
  ver.update(data)
	val verifies = ver.verify(sign);
	data(0) = 0
	ver.update(data)
	val falsifies = ver.verify(sign);
	*/
}

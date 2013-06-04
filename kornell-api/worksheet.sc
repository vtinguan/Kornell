

object worksheet {

  import java.lang.Byte

  val stringToSignBytes = "50 55 54 0a 0a 61 70 70 6c 69 63 61 74 69 6f 6e 2f 78 2d 77 77 77 2d 66 6f 72 6d 2d 75 72 6c 65 6e 63 6f 64 65 64 0a 0a 78 2d 61 6d 7a 2d 64 61 74 65 3a 54 75 65 2c 20 32 38 20 4d 61 79 20 32 30 31 33 20 31 39 3a 31 36 3a 31 31 20 47 4d 54 0a 2f 63 64 66 2d 63 69 2f 73 33 70 75 74 2e 73 68"
                                                  //> stringToSignBytes  : String = 50 55 54 0a 0a 61 70 70 6c 69 63 61 74 69 6f 6
                                                  //| e 2f 78 2d 77 77 77 2d 66 6f 72 6d 2d 75 72 6c 65 6e 63 6f 64 65 64 0a 0a 78
                                                  //|  2d 61 6d 7a 2d 64 61 74 65 3a 54 75 65 2c 20 32 38 20 4d 61 79 20 32 30 31 
                                                  //| 33 20 31 39 3a 31 36 3a 31 31 20 47 4d 54 0a 2f 63 64 66 2d 63 69 2f 73 33 7
                                                  //| 0 75 74 2e 73 68
  val provided = "PUT\n\napplication/x-www-form-urlencoded\n\nx-amz-date: Tue, 28 May 2013 19:16:11 GMT\n/cdf-ci/s3put.sh"
                                                  //> provided  : String = PUT
                                                  //| 
                                                  //| application/x-www-form-urlencoded
                                                  //| 
                                                  //| x-amz-date: Tue, 28 May 2013 19:16:11 GMT
                                                  //| /cdf-ci/s3put.sh

  val canonical = new String(
    stringToSignBytes
      .split(" ")
      .map(_.toUpperCase)
      .map(Byte.parseByte(_, 16)))                //> canonical  : String = PUT
                                                  //| 
                                                  //| application/x-www-form-urlencoded
                                                  //| 
                                                  //| x-amz-date:Tue, 28 May 2013 19:16:11 GMT
                                                  //| /cdf-ci/s3put.sh

  println("-----")                                //> -----
  println(canonical.getBytes)                     //> [B@3ec1eecd
  println("-----")                                //> -----
  println(provided.getBytes)                      //> [B@2e24f4eb
  println("-----")                                //> -----

  val eq = canonical.equals(provided)             //> eq  : Boolean = false

  canonical.length()                              //> res0: Int = 97
  provided.length()                               //> res1: Int = 98

  canonical.getBytes()                            //> res2: Array[Byte] = Array(80, 85, 84, 10, 10, 97, 112, 112, 108, 105, 99, 97
                                                  //| , 116, 105, 111, 110, 47, 120, 45, 119, 119, 119, 45, 102, 111, 114, 109, 45
                                                  //| , 117, 114, 108, 101, 110, 99, 111, 100, 101, 100, 10, 10, 120, 45, 97, 109,
                                                  //|  122, 45, 100, 97, 116, 101, 58, 84, 117, 101, 44, 32, 50, 56, 32, 77, 97, 1
                                                  //| 21, 32, 50, 48, 49, 51, 32, 49, 57, 58, 49, 54, 58, 49, 49, 32, 71, 77, 84, 
                                                  //| 10, 47, 99, 100, 102, 45, 99, 105, 47, 115, 51, 112, 117, 116, 46, 115, 104)
                                                  //| 
  provided.getBytes()                             //> res3: Array[Byte] = Array(80, 85, 84, 10, 10, 97, 112, 112, 108, 105, 99, 97
                                                  //| , 116, 105, 111, 110, 47, 120, 45, 119, 119, 119, 45, 102, 111, 114, 109, 45
                                                  //| , 117, 114, 108, 101, 110, 99, 111, 100, 101, 100, 10, 10, 120, 45, 97, 109,
                                                  //|  122, 45, 100, 97, 116, 101, 58, 32, 84, 117, 101, 44, 32, 50, 56, 32, 77, 9
                                                  //| 7, 121, 32, 50, 48, 49, 51, 32, 49, 57, 58, 49, 54, 58, 49, 49, 32, 71, 77, 
                                                  //| 84, 10, 47, 99, 100, 102, 45, 99, 105, 47, 115, 51, 112, 117, 116, 46, 115, 
                                                  //| 104)

  println(canonical)                              //> PUT
                                                  //| 
                                                  //| application/x-www-form-urlencoded
                                                  //| 
                                                  //| x-amz-date:Tue, 28 May 2013 19:16:11 GMT
                                                  //| /cdf-ci/s3put.sh
  println("-----")                                //> -----
  println(provided)                               //> PUT
                                                  //| 
                                                  //| application/x-www-form-urlencoded
                                                  //| 
                                                  //| x-amz-date: Tue, 28 May 2013 19:16:11 GMT
                                                  //| /cdf-ci/s3put.sh

}
 
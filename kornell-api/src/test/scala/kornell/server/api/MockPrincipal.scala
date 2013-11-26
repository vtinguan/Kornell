package kornell.server.api

import java.security.Principal

class MockPrincipal(username:String) extends Principal{
    
    override def equals(another:Any):Boolean = username.equals(another.toString)


    override def toString = username


    override def hashCode = username.hashCode

    override def getName = username
}
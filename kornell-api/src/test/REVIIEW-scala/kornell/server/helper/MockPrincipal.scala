package kornell.server.helper

import java.security.Principal

class MockPrincipal(username:String) extends Principal{
    


    override def toString = username


    override def hashCode = username.hashCode

    override def getName = username
}
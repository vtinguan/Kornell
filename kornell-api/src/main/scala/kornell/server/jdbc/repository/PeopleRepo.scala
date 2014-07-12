package kornell.server.jdbc.repository

import kornell.core.entity.Person
import kornell.server.repository.Entities
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import java.sql.ResultSet
import kornell.core.util.UUID
import kornell.server.repository.TOs
import com.google.common.cache.CacheLoader
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit._
import kornell.core.util.StringUtils._

object PeopleRepo {

  implicit def toString(rs: ResultSet): String = rs.getString(1)

  val usernameLoader = new CacheLoader[String, Option[Person]]() {
    override def load(username: String): Option[Person] = lookupByUsername(username)
  }

  val cpfLoader = new CacheLoader[String, Option[Person]]() {
    override def load(cpf: String): Option[Person] = lookupByCPF(cpf)
  }

  val emailLoader = new CacheLoader[String, Option[Person]]() {
    override def load(email: String): Option[Person] = lookupByEmail(email)
  }

  val uuidLoader = new CacheLoader[String, Option[Person]]() {
    override def load(uuid: String): Option[Person] = PersonRepo(uuid).first
  }

  val DEFAULT_CACHE_SIZE = 1000

  val cacheBuilder = CacheBuilder
    .newBuilder()
    .expireAfterAccess(5, MINUTES)
    .maximumSize(1000)

  val usernameCache = cacheBuilder.build(usernameLoader)

  val cpfCache = cacheBuilder.build(cpfLoader)

  val emailCache = cacheBuilder.build(emailLoader)

  val uuidCache = cacheBuilder.build(uuidLoader)

  def getByUsername(username: String) = Option(username) flatMap usernameCache.get
  def getByEmail(email: String) = Option(email) flatMap emailCache.get
  def getByCPF(cpf: String) = Option(cpf) flatMap cpfCache.get
  def getByUUID(uuid: String) = uuidCache.get(uuid)

  def lookupByUsername(username: String) = sql"""
		select p.* from Person p
		join Password pwd
		on p.uuid = pwd.person_uuid
		where pwd.username = $username
	""".first[Person]

  def lookupByCPF(cpf: String) = sql"""
		select p.* from Person p	
		where p.cpf = $cpf
	""".first[Person]

  def lookupByEmail(email: String) = sql"""
		select p.* from Person p	
		where p.email = $email
	""".first[Person]

  def get(any: String): Option[Person] = get(any, any, any)

  def get(cpf: String, email: String): Option[Person] = getByCPF(cpf)
    .orElse(getByEmail(email))

  def get(username: String, cpf: String, email: String): Option[Person] =
    getByUsername(username)
      .orElse(getByCPF(cpf))
      .orElse(getByEmail(email))

  def findBySearchTerm(search: String, institutionUUID: String) = {
    newPeople(
      sql"""
      	| select p.* from Person p 
    		| join Registration r on r.person_uuid = p.uuid
      	| where (p.email like ${search + "%"}
      	| or p.cpf like ${search + "%"})
      	| and r.institution_uuid = ${institutionUUID}
      	| order by p.email, p.cpf
      	| limit 8
	    """.map[Person](toPerson))
  }

  def createPerson(email: String = null, fullName: String = null, cpf: String = null): Person =
    create(Entities.newPerson(fullName = fullName,
      email = email,
      cpf = cpf))

  def createPersonCPF(cpf: String, fullName: String): Person =
    create(Entities.newPerson(fullName = fullName, cpf = cpf))

  def create(person: Person): Person = {
    if (person.getUUID == null)
      person.setUUID(randUUID)
    sql""" 
    	insert into Person(uuid, fullName, email,
    		company, title, sex, birthDate, confirmation, cpf
    	) values (${person.getUUID},
             ${person.getFullName},
             ${person.getEmail},
             ${person.getCompany},
             ${person.getTitle},
             ${person.getSex},
             ${person.getBirthDate},
             ${person.getConfirmation},
             ${person.getCPF})
    """.executeUpdate
    updateCaches(person)
    person
  }

  def updateCaches(p: Person) = {
    val op = Some(p)
    uuidCache.put(p.getUUID, op)
    if (isSome(p.getCPF)) cpfCache.put(p.getCPF, op)
    if (isSome(p.getEmail)) cpfCache.put(p.getEmail, op)
  }

  def isRegistered(personUUID: String, cpf: String): Boolean =
    sql"""
  		select count(*) from Person 
  		where cpf = ${digitsOf(cpf)}
  			and uuid != ${personUUID}
  	""".get[Boolean]

}

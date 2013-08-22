package kornell.server.dev.data

import kornell.server.repository.slick.plain.Persons

class PersonData {
  val dio = Persons.create("Ronnie James Dio")

  val jack = Persons.create("Jack Black")
  val kyle = Persons.create("Kyle Gass")
  
  val gilmour = Persons.create("David Gilmour")
  val kubrik = Persons.create("Stanley Kubrik")
}
package kornell.dev.data

import kornell.repository.jdbc.Institutions

class InstitutionsData(people: PersonData) {
  val schoolOfRock = Institutions.create("School of Rock", """
  | We don't need no education""")

  val hollywood = Institutions.create("Hollywood Academy", """
  | That Oscar folks
  """)
  
  Institutions.register(people.jack, schoolOfRock)
  

}
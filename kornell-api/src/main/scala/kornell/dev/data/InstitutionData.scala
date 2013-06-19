package kornell.dev.data

import kornell.repository.jdbc.Institutions

trait InstitionsData {
  val schoolOfRock = Institutions.create("School of Rock","""
  | We don't need no education
      """)
  
}
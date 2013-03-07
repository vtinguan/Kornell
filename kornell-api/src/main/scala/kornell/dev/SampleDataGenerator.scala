package kornell.dev

import kornell.repository.Beans
import kornell.repository.slick.plain.Courses
import kornell.repository.slick.plain.Persons
import kornell.repository.slick.plain.Auth
import java.util.Date

trait CleanDB extends Toolkit {
  respawnDB
}

trait BasicData {
  val fulano = Persons.create("Fulano de Tal")
  val cbabbage = Persons.create("Charles Babbage")
}

trait AuthData extends BasicData {  
  val principal = Auth.createUser(fulano.getUUID, "fulano", "detal", List("user"))
}

trait CoursesData extends BasicData {
  val cDSP = Courses.create(
    "Digital Signal Processing", "dsp", """
    |Learn the fundamentals of digital signal processing theory and discover the myriad ways DSP makes everyday life more productive and fun.""",
    "samples/coursera/dsp/small-icon.hover.png")

  val cCalc1 = Courses.create(
    "Calculus One", "calc1", """
    |Calculus One is a first introduction to differential and integral calculus, emphasizing engaging examples from everyday life.""",
    "samples/coursera/calcone/calculus1.png")

  val cCompIn = Courses.create(
    "Computational Investing", "compinv","""
    |Find out how modern electronic markets work, why stock prices change in the ways they do, and how computation can help our understanding of them.  Build algorithms and visualizations to inform investing practice..""",
    "samples/coursera/compinv/small-icon.hover.png")

  val cGamet = Courses.create(
    "Game Theory", "gamet","""
    |The course covers the basics: representing games and strategies, the extensive form (which computer scientists call game trees), repeated and stochastic games, coalitional games, and Bayesian games (modeling things like auctions).""",
    "samples/coursera/gamet/small-icon.hover.png")

  val cNLP = Courses.create(
    "Natural Language Processing","nlp", """
    |Have you ever wondered how to build a system that automatically translates between languages? Or a system that can understand natural language instructions from a human? This class will cover the fundamentals of mathematical and computational models of language, and the application of these models to key problems in natural language processing.""",
    "samples/coursera/nlp/thumb.jpg")

  val cNut = Courses.create(
    "Nutrition, Health, and Lifestyle: Issues and Insights","nut", """
    |This seven week course will explore nutrition concepts that take center stage in mainstream media outlets and become conversation topics among consumers interested in food choice as it relates to optimal health and physical performance.""",
    "samples/coursera/nutrition/Food-icon.jpg")

  val cScala = Courses.create(
    "Functional Programming Principles in Scala","funprog", """
    |Learn about functional programming, and how it can be effectively combined with object-oriented programming. Gain practice in writing clean functional code, using the Scala programming language.""",
    "samples/coursera/scala/small-icon.hover.png")
  
    
  val eScala = Courses.createEnrollment(new Date,cScala.getUUID(),fulano.getUUID(),"1.00")
  val eGamet = Courses.createEnrollment(new Date,cGamet.getUUID(),fulano.getUUID(),"0.90")
  val eCompIn = Courses.createEnrollment(new Date,cGamet.getUUID(),cbabbage.getUUID(),"1.00")
    
}

object SampleDataGenerator extends App
  with Beans
  with CleanDB
  with AuthData
  with CoursesData
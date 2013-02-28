package kornell.dev

import javax.ejb.Singleton
import javax.ejb.Startup
import kornell.cfg.CDIResources
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.shared.to.TOFactory
import java.util.UUID
import java.math.BigDecimal
import kornell.core.shared.to.CoursesTO
import scala.collection.JavaConverters._
import kornell.util.DataURI

object Mocks {
  val toFactory = AutoBeanFactorySource.create(classOf[TOFactory])

  val cDSP = toFactory.courseTO.as
  cDSP setCourseUUID UUID.randomUUID.toString
  cDSP setTitle "Digital Signal Processing"
  cDSP setDescription """
    |Learn the fundamentals of digital signal processing theory and discover the myriad ways DSP makes everyday life more productive and fun.
    """.stripMargin
  cDSP setThumbDataURI DataURI.fromResource("samples/coursera/dsp/small-icon.hover.png").get
  cDSP setProgress new BigDecimal("1.0")

  val cCalc1 = toFactory.courseTO.as
  cCalc1 setCourseUUID UUID.randomUUID.toString
  cCalc1 setTitle "Calculus One"
  cCalc1 setDescription """
    |Calculus One is a first introduction to differential and integral calculus, emphasizing engaging examples from everyday life.
    """.stripMargin
  cCalc1 setThumbDataURI DataURI.fromResource("samples/coursera/calcone/calculus1.png").get
  cCalc1 setProgress new BigDecimal("0.58")

  val cCompIn = toFactory.courseTO.as
  cCompIn setCourseUUID UUID.randomUUID.toString
  cCompIn setTitle "Computational Investing"
  cCompIn setDescription """
    |Find out how modern electronic markets work, why stock prices change in the ways they do, and how computation can help our understanding of them.  Build algorithms and visualizations to inform investing practice..
    """.stripMargin
  cCompIn setThumbDataURI DataURI.fromResource("samples/coursera/compinv/small-icon.hover.png").get
  cCompIn setProgress new BigDecimal("0.15")

  val cGamet = toFactory.courseTO.as
  cGamet setCourseUUID UUID.randomUUID.toString
  cGamet setTitle "Game Theory"
  cGamet setDescription """
    |The course covers the basics: representing games and strategies, the extensive form (which computer scientists call game trees), repeated and stochastic games, coalitional games, and Bayesian games (modeling things like auctions).
    """.stripMargin
  cGamet setThumbDataURI DataURI.fromResource("samples/coursera/gamet/small-icon.hover.png").get
  cGamet setProgress null

  val cNLP = toFactory.courseTO.as
  cNLP setCourseUUID UUID.randomUUID.toString
  cNLP setTitle "Natural Language Processing"
  cNLP setDescription """
    |Have you ever wondered how to build a system that automatically translates between languages? Or a system that can understand natural language instructions from a human? This class will cover the fundamentals of mathematical and computational models of language, and the application of these models to key problems in natural language processing.
    """.stripMargin
  cNLP setThumbDataURI DataURI.fromResource("samples/coursera/nlp/thumb.jpg").get
  cNLP setProgress null

  val cNut = toFactory.courseTO.as
  cNut setCourseUUID UUID.randomUUID.toString
  cNut setTitle "Nutrition, Health, and Lifestyle: Issues and Insights"
  cNut setDescription """
    |This seven week course will explore nutrition concepts that take center stage in mainstream media outlets and become conversation topics among consumers interested in food choice as it relates to optimal health and physical performance.
    """.stripMargin
  cNut setThumbDataURI DataURI.fromResource("samples/coursera/nutrition/Food-icon.jpg").get
  cNut setProgress null

  val cScala = toFactory.courseTO.as
  cScala setCourseUUID UUID.randomUUID.toString
  cScala setTitle "Functional Programming Principles in Scala"
  cScala setDescription """
    |Learn about functional programming, and how it can be effectively combined with object-oriented programming. Gain practice in writing clean functional code, using the Scala programming language.
    """.stripMargin
  cScala setThumbDataURI DataURI.fromResource("samples/coursera/scala/small-icon.hover.png").get
  cScala setProgress null

  val courses = toFactory.coursesTO.as
  courses setCourses (List(cDSP, cCalc1, cCompIn, cGamet, cNLP, cNut, cScala) asJava)

}

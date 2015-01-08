package kornell.server.scorm.scorm12.cam

import org.junit.runner.RunWith
import java.lang.Boolean._
import kornell.core.scorm.scorm12.cam.adlcp.TimeLimitAction
import scala.language._
import org.jboss.arquillian.junit.Arquillian
import kornell.server.test.KornellSuite
import org.junit.Test


@RunWith(classOf[Arquillian])
class SingleCourseExSuite {  /* extends KornellSuite {
  val res = "/scorm/scorm12/SingleCourseEx/imsmanifest.xml"
  val in = getClass().getResourceAsStream(res)
  val manifest = CAM12DOMParser.parse(in)
  val organizations = manifest.getOrganizations
  val organization = organizations.getOrganizationList.get(0)
  val items = organization.getItems
  val resourceList = manifest.getResources.getResourceList

  @Test def parseOrganizations=  {
    val defaultOrg = manifest.getOrganizations.getDefaultOrganization
    assert(defaultOrg == "B0")
  }


  it should "parse organization identifier" in {
    organization.getIdentifier should be("B0")
  }

  it should "parse organization title" in {
    organization.getTitle should be("Maritime Navigation")
    val meta = organization.getMetadata
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01.xml")
  }

  it should "parse item 1" in {
    val item1 = items.get(0)
    item1.getIdentifier should be("B100")
    item1.isVisible should be(TRUE)
    item1.getTitle should be("Inland Rules of the Road (HTML Format)")
    val meta = item1.getMetadata
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01.xml")
  }

  it should "parse item 2" in {
    val item2 = items.get(0).getItems.get(0)
    item2.getIdentifier should be("S100001")
    item2.getIdentifierRef should be("R_S100001")
    item2.isVisible should be(TRUE)
    item2.getTitle should be("References and Lesson Objective")
  }

  it should "parse item 3" in {
    val item3 = items.get(0).getItems.get(1)
    item3.getIdentifier should be("B110")
    item3.isVisible should be(TRUE)
    item3.getTitle should be("Steering & Sailing Rules")
  }

  it should "parse item 4" in {
    val item4 = items.get(0).getItems.get(1).getItems().get(0)
    item4.getIdentifier should be("S110001")
    item4.getIdentifierRef should be("R_S110001")
    item4.getTitle should be("Conduct of Vessels in any Condition of Visibility")
    val preReqs = item4.getPreRequisites
    preReqs.getType should be("aicc_script")
    preReqs.getContent should be("S100001")
    item4.getMaxTimeAllowed should be("0000:30:00.00")
  }

  it should "parse item 5" in {
    val item5 = items.get(0).getItems.get(1).getItems().get(1)
    item5.getIdentifier should be("S110002")
    item5.getIdentifierRef should be("R_S110002")
    item5.getTitle should be("Conduct of Vessels in Sight of One Another")
    val preReqs = item5.getPreRequisites
    preReqs.getType should be("aicc_script")
    preReqs.getContent should be("S110001")
  }

  it should "parse item 6" in {
    val item6 = items.get(0).getItems.get(1).getItems().get(2)
    item6.getIdentifier should be("S110003")
    item6.getIdentifierRef should be("R_S110003")
    item6.getTitle should be("Conduct of Vessels in Restricted Visibility")
    val preReqs = item6.getPreRequisites
    preReqs.getType should be("aicc_script")
    preReqs.getContent should be("S110002")
  }

  it should "parse item 7" in {
    val item7 = items.get(0).getItems.get(2)
    item7.getIdentifier should be("S100002")
    item7.getIdentifierRef should be("R_S100002")
    item7.isVisible should be(TRUE)
    item7.getTitle should be("Lights & Shapes")
    val preReqs = item7.getPreRequisites
    preReqs.getType should be("aicc_script")
    preReqs.getContent should be("B110")
  }

  it should "parse item 8" in {
    val item8 = items.get(0).getItems.get(3)
    item8.getIdentifier should be("S100003")
    item8.getIdentifierRef should be("R_S100003")
    item8.isVisible should be(TRUE)
    item8.getTitle should be("Sound & Light Signals")
    val preReqs = item8.getPreRequisites
    preReqs.getType should be("aicc_script")
    preReqs.getContent should be("S100002")
  }

  it should "parse item 9" in {
    val item9 = items.get(0).getItems.get(4)
    item9.getIdentifier should be("S100004")
    item9.getIdentifierRef should be("R_S100004")
    item9.isVisible should be(TRUE)
    item9.getTitle should be("Exam")
    val preReqs = item9.getPreRequisites
    preReqs.getType should be("aicc_script")
    preReqs.getContent should be("S100003")
    item9.getMaxTimeAllowed should be("0001:00:00.00")
    item9.getTimeLimitAction should be(TimeLimitAction.continue_no_message)
    item9.getMasteryScore should be("75")
  }

  it should "parse resource 1" in {
    val res1 = resourceList.get(0)
    res1.getIdentifier should be("R_S100001")
    res1.getType should be("webcontent")
    res1.getScormType should be("sco")
    res1.getHref should be("Course01/Lesson01/sco01.htm")
    val meta = res1.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/sco01.xml")
    val file = res1.getFiles.get(0)
    file.getHref should be("Course01/Lesson01/sco01.htm")
    res1.getDependencies().get(0) should be("R_D1")
  }

  it should "parse resource 2" in {
    val res2 = resourceList.get(1)
    res2.getIdentifier should be("R_S110001")
    res2.getType should be("webcontent")
    res2.getScormType should be("sco")
    res2.getHref should be("Course01/Lesson01/sco02.htm")
    val meta = res2.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/sco02.xml")
    val file = res2.getFiles.get(0)
    file.getHref should be("Course01/Lesson01/sco02.htm")
    res2.getDependencies().get(0) should be("R_D1")
  }

  it should "parse resource 3" in {
    val res3 = resourceList.get(2)
    res3.getIdentifier should be("R_S110002")
    res3.getType should be("webcontent")
    res3.getScormType should be("sco")
    res3.getHref should be("Course01/Lesson01/sco03.htm")
    val meta = res3.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/sco03.xml")
    val file = res3.getFiles.get(0)
    file.getHref should be("Course01/Lesson01/sco03.htm")
    res3.getDependencies().get(0) should be("R_D1")
  }

  it should "parse resource 4" in {
    val res4 = resourceList.get(3)
    res4.getIdentifier should be("R_S110003")
    res4.getType should be("webcontent")
    res4.getScormType should be("sco")
    res4.getHref should be("Course01/Lesson01/sco04.htm")
    val meta = res4.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/sco04.xml")
    val file = res4.getFiles.get(0)
    file.getHref should be("Course01/Lesson01/sco04.htm")
    res4.getDependencies().get(0) should be("R_D1")
  }

  it should "parse resource 5" in {
    val res5 = resourceList.get(4)
    res5.getIdentifier should be("R_S100002")
    res5.getType should be("webcontent")
    res5.getScormType should be("sco")
    res5.getHref should be("Course01/Lesson01/sco05.htm")
    val meta = res5.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/sco05.xml")
    res5.getFiles.get(0).getHref should be("Course01/Lesson01/sco05.htm")
    res5.getDependencies.get(0) should be("R_D1")
    res5.getDependencies.get(1) should be("R_A1")
    res5.getDependencies.get(2) should be("R_A2")
    res5.getDependencies.get(3) should be("R_A3")
    res5.getDependencies.get(4) should be("R_A4")
    res5.getDependencies.get(5) should be("R_A5")
    res5.getDependencies.get(6) should be("R_A6")
    res5.getDependencies.get(7) should be("R_A7")
    res5.getDependencies.get(8) should be("R_A8")
    res5.getDependencies.get(9) should be("R_A9")
    res5.getDependencies.get(10) should be("R_A10")
    res5.getDependencies.get(11) should be("R_A11")
    res5.getDependencies.get(12) should be("R_A12")
    res5.getDependencies.get(13) should be("R_A13")
    res5.getDependencies.get(14) should be("R_A14")
    res5.getDependencies.get(15) should be("R_A15")
    res5.getDependencies.get(16) should be("R_A16")
    res5.getDependencies.get(17) should be("R_A17")
    res5.getDependencies.get(18) should be("R_A18")
    res5.getDependencies.get(19) should be("R_A19")
    res5.getDependencies.get(20) should be("R_A20")
    res5.getDependencies.get(21) should be("R_A21")
    res5.getDependencies.get(22) should be("R_A22")
    res5.getDependencies.get(23) should be("R_A23")
    res5.getDependencies.get(24) should be("R_A24")
    res5.getDependencies.get(25) should be("R_A25")
    res5.getDependencies.get(26) should be("R_A26")
    res5.getDependencies.get(27) should be("R_A27")
    res5.getDependencies.get(28) should be("R_A31")
  }

  it should "parse resource 6" in {
    val res6 = resourceList.get(5)
    res6.getIdentifier should be("R_S100003")
    res6.getType should be("webcontent")
    res6.getScormType should be("sco")
    res6.getHref should be("Course01/Lesson01/sco06.htm")
    val meta = res6.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/sco06.xml")
    val file = res6.getFiles.get(0)
    file.getHref should be("Course01/Lesson01/sco06.htm")
    res6.getDependencies().get(0) should be("R_D1")
    res6.getDependencies().get(1) should be("R_A28")
    res6.getDependencies().get(2) should be("R_A29")
    res6.getDependencies().get(3) should be("R_A30")
  }

  it should "parse resource 7" in {
    val res7 = resourceList.get(6)
    res7.getIdentifier should be("R_S100004")
    res7.getType should be("webcontent")
    res7.getScormType should be("sco")
    res7.getHref should be("Course01/Lesson01/sco07.htm")
    val meta = res7.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/sco07.xml")
    val file = res7.getFiles.get(0)
    file.getHref should be("Course01/Lesson01/sco07.htm")
    res7.getDependencies().get(0) should be("R_D1")
  }

  it should "parse resource 8" in {
    val res8 = resourceList.get(7)
    res8.getIdentifier should be("R_D1")
    res8.getType should be("webcontent")
    res8.getScormType should be("asset")
    res8.getBase should be("Course01")
    res8.getFiles.get(0).getHref should be("SCOFunctions.js")
    res8.getFiles.get(1).getHref should be("APIWrapper.js")
  }

  it should "parse resource 9" in {
    val res9 = resourceList.get(8)
    res9.getIdentifier should be("R_A1")
    res9.getType should be("webcontent")
    res9.getScormType should be("asset")
    res9.getBase should be("Course01/Lesson01/")
    val meta = res9.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/underway_lights_big.xml")
    res9.getFiles.get(0).getHref should be("pics/underway_lights_big.jpg")
  }

  it should "parse resource 10" in {
    val res10 = resourceList.get(9)
    res10.getIdentifier should be("R_A2")
    res10.getType should be("webcontent")
    res10.getScormType should be("asset")
    res10.getBase should be("Course01/Lesson01/")
    val meta = res10.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/underway_lights_small.xml")
    res10.getFiles.get(0).getHref should be("pics/underway_lights_small.jpg")
  }

  it should "parse resource 11" in {
    val res11 = resourceList.get(10)
    res11.getIdentifier should be("R_A3")
    res11.getType should be("webcontent")
    res11.getScormType should be("asset")
    res11.getBase should be("Course01/Lesson01/")
    val meta = res11.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/underway_lights_hover.xml")
    res11.getFiles.get(0).getHref should be("pics/underway_lights_hover.jpg")
  }

  it should "parse resource 12" in {
    val res12 = resourceList.get(11)
    res12.getIdentifier should be("R_A4")
    res12.getType should be("webcontent")
    res12.getScormType should be("asset")
    res12.getBase should be("Course01/Lesson01/")
    val meta = res12.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/underway_lights_boat.xml")
    res12.getFiles.get(0).getHref should be("pics/underway_lights_boat.jpg")
  }

  it should "parse resource 13" in {
    val res13 = resourceList.get(12)
    res13.getIdentifier should be("R_A5")
    res13.getType should be("webcontent")
    res13.getScormType should be("asset")
    res13.getBase should be("Course01/Lesson01/")
    val meta = res13.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/underway_lights_GL.xml")
    res13.getFiles.get(0).getHref should be("pics/underway_lights_GL.jpg")
  }

  it should "parse resource 14" in {
    val res14 = resourceList.get(13)
    res14.getIdentifier should be("R_A6")
    res14.getType should be("webcontent")
    res14.getScormType should be("asset")
    res14.getBase should be("Course01/Lesson01/")
    val meta = res14.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/tow_astern.xml")
    res14.getFiles.get(0).getHref should be("pics/tow_astern.jpg")
  }

  it should "parse resource 15" in {
    val res15 = resourceList.get(14)
    res15.getIdentifier should be("R_A7")
    res15.getType should be("webcontent")
    res15.getScormType should be("asset")
    res15.getBase should be("Course01/Lesson01/")
    val meta = res15.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/pushing_composite.xml")
    res15.getFiles.get(0).getHref should be("pics/pushing_composite.jpg")
  }

  it should "parse resource 16" in {
    val res16 = resourceList.get(15)
    res16.getIdentifier should be("R_A8")
    res16.getType should be("webcontent")
    res16.getScormType should be("asset")
    res16.getBase should be("Course01/Lesson01/")
    val meta = res16.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/tow_alongside.xml")
    res16.getFiles.get(0).getHref should be("pics/tow_alongside.jpg")
  }

  it should "parse resource 17" in {
    val res17 = resourceList.get(16)
    res17.getIdentifier should be("R_A9")
    res17.getType should be("webcontent")
    res17.getScormType should be("asset")
    res17.getBase should be("Course01/Lesson01/")
    val meta = res17.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/tow_astern_small.xml")
    res17.getFiles.get(0).getHref should be("pics/tow_astern_small.jpg")
  }

  it should "parse resource 18" in {
    val res18 = resourceList.get(17)
    res18.getIdentifier should be("R_A10")
    res18.getType should be("webcontent")
    res18.getScormType should be("asset")
    res18.getBase should be("Course01/Lesson01/")
    val meta = res18.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/tow_astern.xml")
    res18.getFiles.get(0).getHref should be("pics/tow_astern.jpg")
  }

  it should "parse resource 19" in {
    val res19 = resourceList.get(18)
    res19.getIdentifier should be("R_A11")
    res19.getType should be("webcontent")
    res19.getScormType should be("asset")
    res19.getBase should be("Course01/Lesson01/")
    val meta = res19.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/being_towed_alongside.xml")
    res19.getFiles.get(0).getHref should be("pics/being_towed_alongside.jpg")
  }

  it should "parse resource 20" in {
    val res19 = resourceList.get(19)
    res19.getIdentifier should be("R_A12")
    res19.getType should be("webcontent")
    res19.getScormType should be("asset")
    res19.getBase should be("Course01/Lesson01/")
    val meta = res19.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/pushing_west.xml")
    res19.getFiles.get(0).getHref should be("pics/pushing_west.jpg")
  }

  it should "parse resource 21" in {
    val res20 = resourceList.get(20)
    res20.getIdentifier should be("R_A13")
    res20.getType should be("webcontent")
    res20.getScormType should be("asset")
    res20.getBase should be("Course01/Lesson01/")
    val meta = res20.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/sailing_underway.xml")
    res20.getFiles.get(0).getHref should be("pics/sailing_underway.jpg")
  }

  it should "parse resource 22" in {
    val res22 = resourceList.get(21)
    res22.getIdentifier should be("R_A14")
    res22.getType should be("webcontent")
    res22.getScormType should be("asset")
    res22.getBase should be("Course01/Lesson01/")
    val meta = res22.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/row_underway.xml")
    res22.getFiles.get(0).getHref should be("pics/row_underway.jpg")
  }

  it should "parse resource 23" in {
    val res23 = resourceList.get(22)
    res23.getIdentifier should be("R_A15")
    res23.getType should be("webcontent")
    res23.getScormType should be("asset")
    res23.getBase should be("Course01/Lesson01/")
    val meta = res23.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/sail_prop.xml")
    res23.getFiles.get(0).getHref should be("pics/sail_prop.jpg")
  }

  it should "parse resource 24" in {
    val res24 = resourceList.get(23)
    res24.getIdentifier should be("R_A16")
    res24.getType should be("webcontent")
    res24.getScormType should be("asset")
    res24.getBase should be("Course01/Lesson01/")
    val meta = res24.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/trawling.xml")
    res24.getFiles.get(0).getHref should be("pics/trawling.jpg")
  }

  it should "parse resource 25" in {
    val res25 = resourceList.get(24)
    res25.getIdentifier should be("R_A17")
    res25.getType should be("webcontent")
    res25.getScormType should be("asset")
    res25.getBase should be("Course01/Lesson01/")
    val meta = res25.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/trawling_underway.xml")
    res25.getFiles.get(0).getHref should be("pics/trawling_underway.jpg")
  }

  it should "parse resource 26" in {
    val res26 = resourceList.get(25)
    res26.getIdentifier should be("R_A18")
    res26.getType should be("webcontent")
    res26.getScormType should be("asset")
    res26.getBase should be("Course01/Lesson01/")
    val meta = res26.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/fishing.xml")
    res26.getFiles.get(0).getHref should be("pics/fishing.jpg")
  }

  it should "parse resource 27" in {
    val res27 = resourceList.get(26)
    res27.getIdentifier should be("R_A19")
    res27.getType should be("webcontent")
    res27.getScormType should be("asset")
    res27.getBase should be("Course01/Lesson01/")
    val meta = res27.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/not_under_command_making_way.xml")
    res27.getFiles.get(0).getHref should be("pics/not_under_command_making_way.jpg")
  }

  it should "parse resource 28" in {
    val res28 = resourceList.get(27)
    res28.getIdentifier should be("R_A20")
    res28.getType should be("webcontent")
    res28.getScormType should be("asset")
    res28.getBase should be("Course01/Lesson01/")
    val meta = res28.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/restricted_maneuvering_small.xml")
    res28.getFiles.get(0).getHref should be("pics/restricted_maneuvering_small.jpg")
  }

  it should "parse resource 29" in {
    val res29 = resourceList.get(28)
    res29.getIdentifier should be("R_A21")
    res29.getType should be("webcontent")
    res29.getScormType should be("asset")
    res29.getBase should be("Course01/Lesson01/")
    val meta = res29.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/restricted_maneuvering_tow.xml")
    res29.getFiles.get(0).getHref should be("pics/restricted_maneuvering_tow.jpg")
  }

  it should "parse resource 30" in {
    val res30 = resourceList.get(29)
    res30.getIdentifier should be("R_A22")
    res30.getType should be("webcontent")
    res30.getScormType should be("asset")
    res30.getBase should be("Course01/Lesson01/")
    val meta = res30.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/dredging.xml")
    res30.getFiles.get(0).getHref should be("pics/dredging.jpg")
  }

  it should "parse resource 31" in {
    val res31 = resourceList.get(30)
    res31.getIdentifier should be("R_A23")
    res31.getType should be("webcontent")
    res31.getScormType should be("asset")
    res31.getBase should be("Course01/Lesson01/")
    val meta = res31.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/diving_small.xml")
    res31.getFiles.get(0).getHref should be("pics/diving_small.jpg")
  }

  it should "parse resource 32" in {
    val res32 = resourceList.get(31)
    res32.getIdentifier should be("R_A24")
    res32.getType should be("webcontent")
    res32.getScormType should be("asset")
    res32.getBase should be("Course01/Lesson01/")
    val meta = res32.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/mine_clearing.xml")
    res32.getFiles.get(0).getHref should be("pics/mine_clearing.jpg")
  }

  it should "parse resource 33" in {
    val res33 = resourceList.get(32)
    res33.getIdentifier should be("R_A25")
    res33.getType should be("webcontent")
    res33.getScormType should be("asset")
    res33.getBase should be("Course01/Lesson01/")
    val meta = res33.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/duty_pilot.xml")
    res33.getFiles.get(0).getHref should be("pics/duty_pilot.jpg")
  }

  it should "parse resource 34" in {
    val res34 = resourceList.get(33)
    res34.getIdentifier should be("R_A26")
    res34.getType should be("webcontent")
    res34.getScormType should be("asset")
    res34.getBase should be("Course01/Lesson01/")
    val meta = res34.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/anchored.xml")
    res34.getFiles.get(0).getHref should be("pics/anchored.jpg")
  }

  it should "parse resource 35" in {
    val res35 = resourceList.get(34)
    res35.getIdentifier should be("R_A27")
    res35.getType should be("webcontent")
    res35.getScormType should be("asset")
    res35.getBase should be("Course01/Lesson01/")
    val meta = res35.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/aground.xml")
    res35.getFiles.get(0).getHref should be("pics/aground.jpg")
  }

  it should "parse resource 36" in {
    val res36 = resourceList.get(35)
    res36.getIdentifier should be("R_A28")
    res36.getType should be("webcontent")
    res36.getScormType should be("asset")
    res36.getBase should be("Course01/Lesson01/")
    val meta = res36.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/distress_sigs.xml")
    res36.getFiles.get(0).getHref should be("pics/distress_sigs.jpg")
  }

  it should "parse resource 37" in {
    val res37 = resourceList.get(36)
    res37.getIdentifier should be("R_A29")
    res37.getType should be("webcontent")
    res37.getScormType should be("asset")
    res37.getBase should be("Course01/Lesson01/")
    val meta = res37.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/distress_sigs_add.xml")
    res37.getFiles.get(0).getHref should be("pics/distress_sigs_add.jpg")
  }
 
  it should "parse resource 38" in {
    val res38 = resourceList.get(37)
    res38.getIdentifier should be("R_A30")
    res38.getType should be("webcontent")
    res38.getScormType should be("asset")
    res38.getBase should be("Course01/Lesson01/")
    val meta = res38.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/nav_aids.xml")
    res38.getFiles.get(0).getHref should be("pics/nav_aids.jpg")
  }
  
  it should "parse resource 39" in {
    val res39 = resourceList.get(38)
    res39.getIdentifier should be("R_A31")
    res39.getType should be("webcontent")
    res39.getScormType should be("asset")
    res39.getBase should be("Course01/Lesson01/")
    val meta = res39.getMetadata()
    meta.getSchema should be("ADL SCORM")
    meta.getSchemaVersion should be("1.2")
    meta.getLocation should be("Course01/Lesson01/pics/being_pushed.xml")
    res39.getFiles.get(0).getHref should be("pics/being_pushed.jpg")
  } 
 */ 
}
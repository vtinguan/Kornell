package kornell.server.util

import org.wildfly.swarm.container.Container
import org.wildfly.swarm.jaxrs.JAXRSArchive
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter

object KornellSwarm extends App{
    val container = new Container();
    val deployment = ShrinkWrap.create(classOf[MavenImporter])
                        .loadPomFromFile("pom.xml")
                        .importBuildOutput()
                        .as(classOf[WebArchive]);
    
    container.start();
    container.deploy(deployment);
}
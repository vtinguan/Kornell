package kornell.server.test;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;

@ArquillianSuiteDeployment
public class KornellDeployment {
	
	static final PomEquippedResolveStage resolver = Maven.resolver()
			.loadPomFromFile("pom.xml");
	static final MavenFormatStage deps = resolver.importRuntimeDependencies()
			.resolve().withTransitivity();

	@Deployment
	@OverProtocol("Servlet 3.0")
	static WebArchive depl() {
		
		return ShrinkWrap.create(MavenImporter.class)
				.loadPomFromFile("pom.xml").importBuildOutput()
				.as(WebArchive.class);
	}
}

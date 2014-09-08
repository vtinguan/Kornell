package kornell.core.scorm.scorm12.cam;

public interface Manifest {
	Organizations getOrganizations();
	void setOrganizations(Organizations organizations);
	
	Resources getResources();
	void setResources(Resources resources);
	
	Metadata getMetadata();
	void setMetadata(Metadata metadata);
}

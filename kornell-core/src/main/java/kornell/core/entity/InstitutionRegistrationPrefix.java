package kornell.core.entity;



public interface InstitutionRegistrationPrefix extends Named {
	public static String TYPE = EntityFactory.PREFIX + "institutionRegistrationPrefix+json";

	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);

	boolean isShowEmailOnProfile();
	void setShowEmail(boolean showEmailOnProfile);

	boolean isShowCPFOnProfile();
	void setShowCPF(boolean showCPFOnProfile);

	boolean isShowContactInformationOnProfile();
	void setShowContactInformation(boolean showContactInformationOnProfile);
}

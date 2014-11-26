package kornell.api.client;


public class PersonClient extends RESTClient{

	private String personUUID;

	public PersonClient(String personUUID) {
		this.personUUID = personUUID;
	}


	public void isCPFRegistered(String cpf, Callback<Boolean> cb) {
		GET("/people/"
				+ personUUID 
				+"/isRegistered?cpf=" + cpf)
				.sendRequest("", cb);
	}

	public void isEmailRegistered(String email, Callback<Boolean> cb) {
		GET("/people/"
				+ personUUID 
				+"/isRegistered?email=" + email)
				.sendRequest("", cb);
	}


}

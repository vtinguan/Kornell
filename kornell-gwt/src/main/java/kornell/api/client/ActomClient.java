package kornell.api.client;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kornell.core.entity.ActomEntries;
import kornell.core.entity.EntityFactory;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.URL;
import com.google.web.bindery.autobean.shared.AutoBean;

public class ActomClient extends RESTClient {
	//TODO: Consider not hurting DI
	private static final EntityFactory entityFactory = GWT.create(EntityFactory.class);
	
	private String actomKey;
	private String enrollmentUUID;
	

	public ActomClient(EnrollmentClient enrollmentClient,String actomKey) {
		this.enrollmentUUID = enrollmentClient.getEnrollmentUUID();
		this.actomKey = actomKey;
	}

	public void sync(Map<String, String> entries) {
		ActomEntries actomEntries = entityFactory.newActomEntries().as();
		String encodedActomKey = URL.encodePathSegment(actomKey);
		actomEntries.setActomKey(encodedActomKey);
		actomEntries.setEnrollmentUUID(enrollmentUUID);
		actomEntries.setEntries(entries);
		PUT("enrollments",enrollmentUUID,"actoms",encodedActomKey,"entries")
			.withContentType(ActomEntries.TYPE)
			.withEntityBody(actomEntries)
			.go();
	}

	

}

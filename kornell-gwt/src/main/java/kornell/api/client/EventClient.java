package kornell.api.client;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import kornell.core.event.ActomEntered;
import kornell.core.to.UserInfoTO;

public class EventClient extends RESTClient {

	private ActomEntered event;

	public EventClient(ActomEntered event) {
		this.event = event;
	}

	public void fire() {
		getCurrentUser(new Callback<UserInfoTO>() {
			@Override
			protected void ok(UserInfoTO to) {
				event.setFromPersonUUID(to.getPerson().getUUID());
				ExceptionalRequestBuilder req = PUT("/events/actomEntered");
				req.setHeader("Content-Type", ActomEntered.TYPE);
				AutoBean<ActomEntered> autobean = AutoBeanUtils.getAutoBean(event);				
				String reqData = AutoBeanCodex.encode(autobean).getPayload();
				req.setRequestData(reqData);
				req.go();
			}
		});

	}
}

package kornell.gui.client.mvp;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.admin.home.AdminHomeActivity;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.course.ClassroomActivity;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.course.ClassroomPresenter;
import kornell.gui.client.presentation.home.HomeActivity;
import kornell.gui.client.presentation.home.HomePlace;
import kornell.gui.client.presentation.message.MessageActivity;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.profile.ProfileActivity;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.sandbox.SandboxActivity;
import kornell.gui.client.presentation.sandbox.SandboxPlace;
import kornell.gui.client.presentation.sandbox.SandboxPresenter;
import kornell.gui.client.presentation.terms.TermsActivity;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrineActivity;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomeActivity;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;



/**
 * A mapping of places to activities used by this application.
 */
public class GlobalActivityMapper implements AsyncActivityMapper {
	private ClientFactory clientFactory;

	public GlobalActivityMapper(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	/** TODO: This may suck fast */
	public void getActivity(final Place place,
			final ActivityCallbackHandler activityCallbackHandler) {
		GWT.log("GlobalActivityMapper " + place.toString());
		
		if(!clientFactory.getKornellSession().isAuthenticated() || place instanceof VitrinePlace){
			activityCallbackHandler.onReceiveActivity(new VitrineActivity((VitrinePlace) place, clientFactory));
		} else if (place instanceof HomePlace) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable err) {
					Window.alert("Failed to load activity");
				}
				public void onSuccess() {
					activityCallbackHandler.onReceiveActivity(new HomeActivity((HomePlace) place, clientFactory));
				}
			});
		} else if (place instanceof TermsPlace) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable err) {
					Window.alert("Failed to load activity");
				}
				public void onSuccess() {
					activityCallbackHandler.onReceiveActivity(new TermsActivity(clientFactory));
				}
			});
		} else if (place instanceof WelcomePlace) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable err) {
					Window.alert("Failed to load activity");
				}
				public void onSuccess() {
					activityCallbackHandler.onReceiveActivity(new WelcomeActivity(clientFactory));
				}
			});
		} else if (place instanceof ProfilePlace) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable err) {
					Window.alert("Failed to load activity");
				}
				public void onSuccess() {
					activityCallbackHandler.onReceiveActivity(new ProfileActivity(clientFactory));
				}
			});
		} else if (place instanceof MessagePlace) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable err) {
					Window.alert("Failed to load activity");
				}
				public void onSuccess() {
					activityCallbackHandler.onReceiveActivity(new MessageActivity(clientFactory));
				}
			});
		} else if (place instanceof ClassroomPlace) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable err) {
					Window.alert("Failed to load activity");
				}
				public void onSuccess() {
					ClassroomPresenter coursePresenter = clientFactory.getViewFactory().getClassroomPresenter();
					coursePresenter.setPlace((ClassroomPlace) place);
					activityCallbackHandler.onReceiveActivity(new ClassroomActivity(coursePresenter));
				}
			});
		} else if (place instanceof SandboxPlace) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable err) {
					Window.alert("Failed to load activity");
				}
				public void onSuccess() {
					SandboxPresenter sandboxPresenter = clientFactory.getViewFactory().getSandboxPresenter();
					sandboxPresenter.setPlace((SandboxPlace) place);
					activityCallbackHandler.onReceiveActivity(new SandboxActivity(sandboxPresenter));
				}
			});
		} else if (place instanceof AdminHomePlace) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable err) {
					Window.alert("Failed to load activity");
				}
				public void onSuccess() {
					activityCallbackHandler.onReceiveActivity(new AdminHomeActivity(clientFactory));
				}
			});
		} else {
			activityCallbackHandler.onReceiveActivity(null);
		}
	}

}

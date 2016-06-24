package kornell.gui.client.presentation.admin.institution;

import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Institution;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.KornellConstantsHelper;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class AdminInstitutionPresenter implements AdminInstitutionView.Presenter {
	Logger logger = Logger.getLogger(AdminInstitutionPresenter.class.getName());
	private AdminInstitutionView view;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;

	public AdminInstitutionPresenter(KornellSession session,
			PlaceController placeController, Place defaultPlace,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.defaultPlace = defaultPlace;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		formHelper = new FormHelper();
		
		init();
	}

	private void init() {
		if (session.isInstitutionAdmin()) {
			view = getView();
			view.setPresenter(this);      
		} else {
			logger.warning("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminInstitutionView getView() {
		return viewFactory.getAdminInstitutionView();
	}

	@Override
  public void updateInstitution(Institution institution) {
			session.institution(institution.getUUID()).update(institution, new Callback<Institution>() {
				@Override
				public void ok(Institution institution) {
						LoadingPopup.hide();
						KornellNotification.show("Alterações salvas com sucesso!");
						Window.Location.reload();
				}		
				
				@Override
				public void conflict(KornellErrorTO kornellErrorTO){
					LoadingPopup.hide();
					KornellNotification.show(KornellConstantsHelper.getErrorMessage(kornellErrorTO), AlertType.ERROR, 2500);
				}
			});
  }
}
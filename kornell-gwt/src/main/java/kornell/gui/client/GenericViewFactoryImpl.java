package kornell.gui.client;

import static kornell.core.util.StringUtils.composeURL;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.courseClasses.AdminCourseClassesView;
import kornell.gui.client.presentation.admin.courseClasses.generic.GenericAdminCourseClassesView;
import kornell.gui.client.presentation.admin.home.AdminHomePresenter;
import kornell.gui.client.presentation.admin.home.AdminHomeView;
import kornell.gui.client.presentation.admin.home.generic.GenericAdminHomeView;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionView;
import kornell.gui.client.presentation.admin.institution.generic.GenericAdminInstitutionView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.bar.generic.GenericMenuBarView;
//github.com/Craftware/Kornell.git
import kornell.gui.client.presentation.bar.generic.GenericSouthBarView;
import kornell.gui.client.presentation.course.ClassroomPresenter;
import kornell.gui.client.presentation.course.ClassroomView;
import kornell.gui.client.presentation.course.generic.GenericClassroomView;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.home.generic.GenericHomeView;
import kornell.gui.client.presentation.message.MessagePresenter;
import kornell.gui.client.presentation.message.MessageView;
import kornell.gui.client.presentation.message.compose.GenericMessageComposeView;
import kornell.gui.client.presentation.message.compose.MessageComposeView;
import kornell.gui.client.presentation.message.generic.GenericMessageView;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.profile.generic.GenericProfileView;
import kornell.gui.client.presentation.sandbox.SandboxPresenter;
import kornell.gui.client.presentation.sandbox.SandboxView;
import kornell.gui.client.presentation.sandbox.generic.GenericSandboxView;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.terms.generic.GenericTermsView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.vitrine.generic.GenericVitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;
import kornell.gui.client.presentation.welcome.generic.GenericWelcomeView;
import kornell.gui.client.sequence.SequencerFactory;
import kornell.gui.client.sequence.SequencerFactoryImpl;

import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class GenericViewFactoryImpl implements ViewFactory {

	private ClientFactory clientFactory;

	/* Views */
	private ScrollPanel scrollPanel;
	private GenericMenuBarView menuBarView;
	private SouthBarView southBarView;
	private GenericHomeView genericHomeView;
	private GenericAdminHomeView genericAdminHomeView;
	private AdminHomePresenter genericAdminHomePresenter;
	private GenericAdminCourseClassesView genericAdminCourseClassesView;
	private GenericAdminInstitutionView genericAdminInstitutionView;
	private ClassroomPresenter coursePresenter;
	private SandboxPresenter sandboxPresenter;
	private MessagePresenter messagePresenter, messagePresenterCourseClass;
	private boolean isMantleShown = false;

	SimplePanel shell = new SimplePanel();



	public GenericViewFactoryImpl(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void initGUI() {

		shell.addStyleName("wrapper");
		scrollPanel = new ScrollPanel();
		scrollPanel.add(shell);
		scrollPanel.addStyleName("vScrollBar");
		
		final RootPanel rootPanel = RootPanel.get();
		rootPanel.add(scrollPanel);

		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						setPlaceNameAsBodyStyle(event);
						setBackgroundImage(event.getNewPlace() instanceof VitrinePlace);
						checkMenuBars(!(event.getNewPlace() instanceof VitrinePlace));
					}

					private void checkMenuBars(boolean addPanels) {
	          if(addPanels && rootPanel.getWidgetCount() < 3){
	        		rootPanel.add(getMenuBarView());	
	        		rootPanel.add(getSouthBarView());
	          }
          }

					private void setPlaceNameAsBodyStyle(PlaceChangeEvent event) {
						String styleName = rootPanel.getStyleName();
						if (!styleName.isEmpty())
							rootPanel.removeStyleName(styleName);
						String[] split = event.getNewPlace().getClass()
								.getName().split("\\.");
						String newStyle = split[split.length - 1];
						rootPanel.addStyleName(newStyle);
					}
				});
	}
	

	private void setBackgroundImage(boolean showMantle) {
		if(showMantle == isMantleShown) return;
		String style = "position: relative; " +
				"zoom: 1; " + 
				"-webkit-background-size: cover; " + 
				"-moz-background-size: cover; " + 
				"-o-background-size: cover; " + 
				"background-size: cover;";
		if(showMantle)
			style = "background: url('"+composeURL(Dean.getInstance().getInstitution().getAssetsURL(), "bgVitrine.jpg")+"') no-repeat center center fixed; " + style;
		DOM.setElementAttribute(scrollPanel.getElement(), "style", style);
		isMantleShown = showMantle;
	}

	@Override
	public MenuBarView getMenuBarView() {
		if (menuBarView == null)
			menuBarView = new GenericMenuBarView(clientFactory, scrollPanel);
		return menuBarView;
	}

	@Override
	public SouthBarView getSouthBarView() {
		if (southBarView == null)
			southBarView = new GenericSouthBarView(clientFactory, scrollPanel);
		return southBarView;
	}

	@Override
	public HomeView getHomeView() {
		if (genericHomeView == null) {
			genericHomeView = new GenericHomeView(clientFactory,
					clientFactory.getEventBus(),
					clientFactory.getKornellSession());
		}
		return genericHomeView;
	}

	@Override
	public VitrineView getVitrineView() {
		return new GenericVitrineView();
	}

	@Override
	public WelcomeView getWelcomeView() {
		return new GenericWelcomeView(clientFactory.getEventBus(),
				clientFactory.getKornellSession(),
				clientFactory.getPlaceController(),
				clientFactory.getTOFactory());
	}

	@Override
	public ProfileView getProfileView() {
		return new GenericProfileView(clientFactory);
	}

	@Override
	public MessageView getMessageView() {
		return new GenericMessageView(clientFactory.getEventBus());
	}

	@Override
  public MessageComposeView getMessageComposeView() {
		return new GenericMessageComposeView();
  }

	@Override
	public ClassroomView getClassroomView() {
		return new GenericClassroomView(clientFactory.getPlaceController(),
				clientFactory.getKornellSession(), clientFactory.getEventBus());
	}

	@Override
	public TermsView getTermsView() {
		return new GenericTermsView(clientFactory);
	}

	@Override
	public ClassroomPresenter getClassroomPresenter() {
		if (coursePresenter == null) {
			SequencerFactory rendererFactory = new SequencerFactoryImpl(
					clientFactory.getEventBus(),
					clientFactory.getPlaceController(),
					clientFactory.getKornellSession());
			ClassroomView activityView = getClassroomView();
			coursePresenter = new ClassroomPresenter(activityView,
					clientFactory.getPlaceController(), rendererFactory,
					clientFactory.getKornellSession());
		}
		return coursePresenter;
	}

	@Override
	public SandboxView getSandboxView() {
		return new GenericSandboxView(clientFactory.getKornellSession());
	}

	@Override
	public SandboxPresenter getSandboxPresenter() {
		if (sandboxPresenter == null) {
			SandboxView sandboxView = getSandboxView();
			sandboxPresenter = new SandboxPresenter(sandboxView);
		}
		return sandboxPresenter;
	}

	@Override
	public AdminHomeView getAdminHomeView() {
		if(genericAdminHomeView == null)
			genericAdminHomeView = new GenericAdminHomeView(clientFactory.getKornellSession(), clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
		return genericAdminHomeView;
	}
	
	@Override
	public MessagePresenter getMessagePresenterCourseClass() {
		if(messagePresenterCourseClass == null)
			messagePresenterCourseClass = new MessagePresenter(clientFactory.getKornellSession(), clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory(), true);
		return messagePresenterCourseClass;
	}

	@Override
	public SimplePanel getShell() {
		return shell;
	}

	@Override
	public ScrollPanel getScrollPanel() {
		return scrollPanel;
	}

	@Override
  public MessagePresenter getMessagePresenter() {
		if(messagePresenter == null)
			messagePresenter = new MessagePresenter(clientFactory.getKornellSession(), clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
	  return messagePresenter;
  }

	@Override
  public AdminCourseClassesView getAdminCourseClassesView() {
		if(genericAdminCourseClassesView == null)
			genericAdminCourseClassesView = new GenericAdminCourseClassesView(clientFactory.getKornellSession(), clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
		return genericAdminCourseClassesView;
  }

	@Override
  public AdminInstitutionView getAdminInstitutionView() {
		if(genericAdminInstitutionView == null)
			genericAdminInstitutionView = new GenericAdminInstitutionView(clientFactory.getKornellSession(), clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
		return genericAdminInstitutionView;
  }

	@Override
  public AdminHomePresenter getAdminHomePresenter() {
		if(genericAdminHomePresenter == null)
			genericAdminHomePresenter = new AdminHomePresenter(clientFactory.getKornellSession(),clientFactory.getEventBus(),clientFactory.getPlaceController(),clientFactory.getDefaultPlace(),clientFactory.getTOFactory(),this);
		return genericAdminHomePresenter;
  }
}

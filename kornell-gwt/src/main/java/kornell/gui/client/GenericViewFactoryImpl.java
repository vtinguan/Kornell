package kornell.gui.client;

import kornell.gui.client.presentation.admin.home.AdminHomeView;
import kornell.gui.client.presentation.admin.home.generic.GenericAdminHomeView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.bar.generic.GenericMenuBarView;
//github.com/Craftware/Kornell.git
import kornell.gui.client.presentation.bar.generic.GenericSouthBarView;
import kornell.gui.client.presentation.course.ClassroomPresenter;
import kornell.gui.client.presentation.course.ClassroomView;
import kornell.gui.client.presentation.course.generic.GenericClassroomView;
import kornell.gui.client.presentation.course.library.CourseLibraryPresenter;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.home.generic.GenericHomeView;
import kornell.gui.client.presentation.message.MessageView;
import kornell.gui.client.presentation.message.generic.GenericMessageView;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.profile.generic.GenericProfileView;
import kornell.gui.client.presentation.sandbox.SandboxPresenter;
import kornell.gui.client.presentation.sandbox.SandboxView;
import kornell.gui.client.presentation.sandbox.generic.GenericSandboxView;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.terms.generic.GenericTermsView;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.vitrine.generic.GenericVitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;
import kornell.gui.client.presentation.welcome.generic.GenericWelcomeView;
import kornell.gui.client.sequence.SequencerFactory;
import kornell.gui.client.sequence.SequencerFactoryImpl;
import kornell.gui.client.util.orientation.IpadIos7HeightFix;
import kornell.gui.client.util.orientation.OrientationChangeEvent;
import kornell.gui.client.util.orientation.OrientationResizeHandler;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

//TODO: Organize this big, messy class and interface
public class GenericViewFactoryImpl implements ViewFactory {

	private ClientFactory clientFactory;

	/* Views */
	final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
	private GenericMenuBarView menuBarView;
	private SouthBarView southBarView;
	private GenericHomeView genericHomeView;
	private ClassroomPresenter coursePresenter;
	private CourseLibraryPresenter courseLibraryPresenter;
	private SandboxPresenter sandboxPresenter;

	SimplePanel shell = new SimplePanel();

	public GenericViewFactoryImpl(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void initGUI() {
		final RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
		dockLayoutPanel.addNorth(getMenuBarView(), 45);
		dockLayoutPanel.addSouth(getSouthBarView(), 35);

		ScrollPanel sp = new ScrollPanel();
		sp.add(shell);
		dockLayoutPanel.add(sp);
		sp.addStyleName("vScrollBar");
		dockLayoutPanel.addStyleName("wrapper");
		rootLayoutPanel.add(dockLayoutPanel);
		
		final String userAgent = Navigator.getUserAgent();
		if (userAgent.contains("iPad") && userAgent.contains("OS 7")) {
			IpadIos7HeightFix.fixHeight();
			clientFactory.getEventBus().addHandler(OrientationChangeEvent.TYPE, new IpadIos7HeightFix());
		}
		Window.addResizeHandler(new OrientationResizeHandler(clientFactory.getEventBus()));

		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						setPlaceNameAsBodyStyle(event);
						dockLayoutPanel.setWidgetHidden((Widget) getSouthBarView(), !getSouthBarView().isVisible());
						dockLayoutPanel.setWidgetHidden((Widget) getMenuBarView(), !getMenuBarView().isVisible());
					}

					private void setPlaceNameAsBodyStyle(PlaceChangeEvent event) {
						String styleName = rootLayoutPanel.getStyleName();
						if (!styleName.isEmpty())
							rootLayoutPanel.removeStyleName(styleName);
						String[] split = event.getNewPlace().getClass().getName().split("\\.");
						String newStyle = split[split.length - 1];
						rootLayoutPanel.addStyleName(newStyle);
					}
				});
	}

	@Override
	public DockLayoutPanel getDockLayoutPanel() {
		return dockLayoutPanel;
	}

	@Override
	public MenuBarView getMenuBarView() {
		if (menuBarView == null)
			menuBarView = new GenericMenuBarView(clientFactory);
		return menuBarView;
	}

	@Override
	public SouthBarView getSouthBarView() {
		if (southBarView == null)
			southBarView = new GenericSouthBarView(clientFactory);
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
		return new GenericMessageView(clientFactory);
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
		SequencerFactory rendererFactory = new SequencerFactoryImpl(
				clientFactory.getEventBus(),
				clientFactory.getPlaceController(),
				clientFactory.getKornellSession());
		if (coursePresenter == null) {
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
		return new GenericAdminHomeView(clientFactory.getKornellSession(), clientFactory.getEventBus());
	}

	@Override
	public SimplePanel getShell() {
		return shell;
	}
}

package kornell.gui.client.presentation.terms.generic;

import java.math.BigDecimal;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.data.Person;
import kornell.core.shared.data.CourseTO;
import kornell.core.shared.data.CoursesTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.generic.GenericActivityBarView;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;
import kornell.gui.client.presentation.welcome.generic.GenericCourseSummaryView;
import kornell.gui.client.presentation.welcome.generic.GenericMenuLeftView;

import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

//TODO - Courses will overflow the screen

public class GenericTermsView extends Composite implements TermsView {
	interface MyUiBinder extends UiBinder<Widget, GenericTermsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


	@UiField
	Paragraph titleUser;
	@UiField
	Button btnAgree;
	@UiField
	Button btnDontAgree;
	

	private static String COURSES_ALL = "all";
	private static String COURSES_IN_PROGRESS = "inProgress";
	private static String COURSES_TO_START = "toStart";
	private static String COURSES_TO_ACQUIRE = "toAcquire";
	private static String COURSES_FINISHED = "finished";
	
	private KornellClient client;

	private PlaceController placeCtrl;
	
	private String displayCourses;

	private final EventBus eventBus = new SimpleEventBus();

	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	private GenericMenuLeftView menuLeftView;
	
	
	public GenericTermsView(KornellClient client, PlaceController placeCtrl) {
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}
	
	private void initData() {
		client.getCurrentUser(new Callback<Person>() {
			@Override
			protected void ok(Person person) {
				display(person);
			}
		});
	}


	private void display(Person person) {
		titleUser.setText(person.getFullName());
		//TODO i18n
		btnAgree.setText("Concordo".toUpperCase());
		btnDontAgree.setText("Não Concordo".toUpperCase());
		
		
		
		
		
		
		
		/*String str = "Você está na Universidade Virtual Midway!";
		
		str+= "Ao acessar este sistema, você declara que irá respeitar todos os direitos de propriedade intelectual e industrial.";
		
		str+= "Você assume toda e qualquer responsabilidade, de caráter cívil e/ou criminal, pela utilização indevida das informações, textos, gráficos, marcas, obras, enfim, de todo e qualquer direito de propriedade intelectual ou industrial contido neste sistema.";
		
		str+= "Você concorda que é responsável por sua própria conduta e por qualquer Conteúdo que criar, transmitir ou apresentar ao utilizar os serviços da ";
		str+= "" + "Universidade Virtual Midway";
		str+= " e por todas as consequências relacionadas. Você concorda em usar os serviços da ";
		str+= "" + "Universidade Virtual Midway";
		str+= " apenas para finalidades legais, adequadas e condizentes com os Termos e com quaisquer políticas ou diretrizes aplicáveis. Você concorda em não se engajar em qualquer atividade que interfira ou interrompa os serviços da ";
		str+= "" + "Universidade Virtual Midway";
		str+= ", ou os servidores e redes relacionados aos serviços da ";
		str+= "" + "Universidade Virtual Midway";
		str+= ".";
		
		str+= "Ao usar os serviços da ";
		str+= "" + "Universidade Virtual Midway";
		str+= ", você concorda e está ciente de que a ";
		str+= "" + "Universidade Virtual Midway";
		str+= " pode acessar, preservar e divulgar as informações da sua conta e qualquer conteúdo a ela associado, caso assim seja exigido por lei ou quando acreditarmos, de boa-fé, que tal preservação ou divulgação de acesso é necessária para: (a) cumprir qualquer lei, regulamentação, processo legal ou solicitação governamental obrigatória; (b) fazer cumprir os Termos, incluindo a investigação de possíveis violações; (c) detectar, impedir ou tratar de questões de fraude, segurança ou técnicas (inclusive, sem limitações, a filtragem de spam); (d) proteger, mediante perigo iminente, os direitos, a propriedade ou a segurança da ";
		str+= "" + "Universidade Virtual Midway";
		str+= ", seus usuários ou o público, de acordo com o exigido ou permitido por lei.";*/
		
		
	}

	@UiHandler("btnAgree")
	void handleClickAll(ClickEvent e) {
		placeCtrl.goTo(new WelcomePlace());
	}

	@UiHandler("btnDontAgree")
	void handleClickInProgress(ClickEvent e) {
		placeCtrl.goTo(new VitrinePlace());
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}
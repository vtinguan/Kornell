package kornell.gui.client.presentation.course.library.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kornell.api.client.KornellClient;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.course.library.CourseLibraryView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;


public class GenericCourseLibraryView extends Composite implements CourseLibraryView {

	interface MyUiBinder extends UiBinder<Widget, GenericCourseLibraryView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private KornellClient client;
	private PlaceController placeCtrl;
	private EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private String IMAGES_PATH = "skins/first/icons/courseLibrary/";

	@UiField
	FlowPanel libraryPanel;
	@UiField
	FlowPanel titlePanel;
	@UiField
	FlowPanel buttonsPanel; 
	@UiField
	FlowPanel contentPanel; 

	@UiField
	com.github.gwtbootstrap.client.ui.Button btnAllFiles;
	@UiField
	com.github.gwtbootstrap.client.ui.Button btnMostViewed;
	@UiField
	com.github.gwtbootstrap.client.ui.Button btnTags;
	@UiField
	com.github.gwtbootstrap.client.ui.Button btnMyFiles;
	@UiField
	com.github.gwtbootstrap.client.ui.Button btnIncludeFile;
	com.github.gwtbootstrap.client.ui.Button btnCurrent;
	

	com.github.gwtbootstrap.client.ui.Button btnFile;

	FlowPanel filesPanel; 
	FlowPanel filesHeader;
	FlowPanel filesWrapper; 
	
	FilesTO filesTO;
	Button btnIcon;
	Button btnName; 
	Button btnSize;
	Button btnClassification;
	Button btnPublishingDate;
	Button btnLastClicked;
	private static Integer ORDER_ASCENDING = 0;
	private static Integer ORDER_DESCENDING = 1;
	private Integer order = ORDER_ASCENDING;	
	Map<Integer, FlowPanel> fileWidgetMap;
	
	public GenericCourseLibraryView(EventBus eventBus, KornellClient client, PlaceController placeCtrl) {
		this.bus = eventBus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}
	
	private void initData() {
		/*client.getCourses(new Callback<CoursesTO>() {
			@Override
			protected void ok(CoursesTO to) {
				display();
			}
		});*/	
		display();
	}

	private void display() {
		btnCurrent = btnAllFiles;
		//TODO i18n
		displayTitle();
		displayButtons();
		displayContent(btnCurrent, btnLastClicked);
	}

	private void displayContent(com.github.gwtbootstrap.client.ui.Button btnCurrent, Button btnLastClicked) {
		// TODO get info
		contentPanel.clear();
		if(btnCurrent.equals(btnAllFiles)){
			fileWidgetMap = new HashMap<Integer, FlowPanel>();
			filesTO = getAllFilesTO();
			contentPanel.add(getFilesTable(btnLastClicked));
		} else if(btnCurrent.equals(btnMostViewed)){
			filesTO = getMostViewedFilesTO();
			fileWidgetMap = new HashMap<Integer, FlowPanel>();
			contentPanel.add(getFilesTable(btnLastClicked));
		} else if(btnCurrent.equals(btnTags)) {
			contentPanel.add(getFilesTable(btnLastClicked));
		} else if(btnCurrent.equals(btnMyFiles)) {
			filesTO = getMyFilesTO();
			fileWidgetMap = new HashMap<Integer, FlowPanel>();
			contentPanel.add(getFilesTable(btnLastClicked));
		} else if(btnCurrent.equals(btnIncludeFile)) {
			contentPanel.add(new GenericIncludeFileView(bus, client, placeCtrl));
		}
	}

	private void displayTitle() {		
		Image titleImage = new Image(IMAGES_PATH + "library.png");
		titleImage.addStyleName("titleImage");
		titlePanel.add(titleImage);
		
		Label titleLabel = new Label("Biblioteca do curso: ");
		titleLabel.addStyleName("titleLabel");
		titlePanel.add(titleLabel);
		
		//TODO getcoursename
		Label courseNameLabel = new Label("Suplementação Alimentar");
		courseNameLabel.addStyleName("courseNameLabel");
		titlePanel.add(courseNameLabel);
	}
	
	private FlowPanel getFilesTable(Button btn) {
		FlowPanel filesPanel = new FlowPanel();
		filesPanel.addStyleName("filesPanel");
		filesPanel.add(getHeader());
		filesPanel.add(getFiles(btn));
		return filesPanel;
	}

	private FlowPanel getFiles(Button btn) {
		ArrayList<FileTO> list = (ArrayList<FileTO>) filesTO.getFiles();

		if (list.size() > 0) {
			if(btnIcon.equals(btn)){
			    Collections.sort(list, new FileTypeComparator());
		    	if(btnIcon.equals(btnLastClicked) && order == ORDER_ASCENDING){
		    		Collections.reverse(list);
		    		order = ORDER_DESCENDING;
		    	} else {
		    		order = ORDER_ASCENDING;
		    	}
			} else if(btnSize.equals(btn)) {
			    Collections.sort(list, new FileSizeComparator());
		    	if(btnSize.equals(btnLastClicked) && order == ORDER_DESCENDING){
		    		Collections.reverse(list);
		    		order = ORDER_ASCENDING;
		    	} else {
		    		order = ORDER_DESCENDING;
		    	}
			} else if(btnClassification.equals(btn)) {
			    Collections.sort(list, new FileClassificationComparator());
		    	if(btnClassification.equals(btnLastClicked) && order == ORDER_DESCENDING){
		    		Collections.reverse(list);
		    		order = ORDER_ASCENDING;
		    	} else {
		    		order = ORDER_DESCENDING;
		    	}
			} else if(btnPublishingDate.equals(btn)) {
			    Collections.sort(list, new FilePublishingDateComparator());
		    	if(btnPublishingDate.equals(btnLastClicked) && order == ORDER_ASCENDING){
		    		Collections.reverse(list);
		    		order = ORDER_DESCENDING;
		    	} else {
		    		order = ORDER_ASCENDING;
		    	}
			} else {
			    Collections.sort(list, new FileNameComparator());
		    	if(btnName.equals(btnLastClicked) && order == ORDER_ASCENDING){
		    		Collections.reverse(list);
		    		order = ORDER_DESCENDING;
		    	} else {
		    		order = ORDER_ASCENDING;
		    	}
			}
			btnLastClicked = btn != null ? btn : btnLastClicked;
		}
		
		if(fileWidgetMap.size() <= 0)
			for (FileTO fileTO : list)
				fileWidgetMap.put(fileTO.getId(), getFilePanel(fileTO));


		filesWrapper = new FlowPanel();
		filesWrapper.addStyleName("filesWrapper");
		for(FileTO fileTO : list){
			filesWrapper.add(fileWidgetMap.get(fileTO.getId()));
		}
		return filesWrapper;
	}

	private FlowPanel getHeader() {
		if(filesHeader != null)
			return filesHeader;
		
		filesHeader = new FlowPanel();
		filesHeader.addStyleName("filesHeader");
		
		btnIcon = btnIcon != null ? btnIcon : new Button("Tipo");
		displayHeaderButton(btnIcon, "btnIcon", false);
		filesHeader.add(btnIcon);

		btnName = btnName != null ? btnName : new Button("Nome do Arquivo");
		displayHeaderButton(btnName, "btnName", true);
		filesHeader.add(btnName);

		btnSize = btnSize != null ? btnSize : new Button("Tamanho");
		displayHeaderButton(btnSize, "btnSize", false);
		filesHeader.add(btnSize);

		btnClassification = btnClassification != null ? btnClassification : new Button("Classificação");
		displayHeaderButton(btnClassification, "btnClassification", false);
		filesHeader.add(btnClassification);

		btnPublishingDate = btnPublishingDate != null ? btnPublishingDate : new Button("Data da publicação");
		displayHeaderButton(btnPublishingDate, "btnPublishingDate", false);
		filesHeader.add(btnPublishingDate);
		
		btnLastClicked = btnLastClicked != null ? btnLastClicked : btnName;
		
		return filesHeader;
	}

	private void displayHeaderButton(Button btn, String styleName, boolean selected) {
		btn.removeStyleName("btn");
		btn.addStyleName("btnLibraryHeader"); 
		btn.addStyleName(styleName);
		btn.addStyleName(selected ? "btnSelected" : "btnNotSelected");
		btn.addClickHandler(new LibraryHeaderClickHandler());
	}

	private void handleEvent(Button btn) {		
		if(btnLastClicked != null){
			btnLastClicked.removeStyleName("btnSelected");
			btnLastClicked.addStyleName("btnNotSelected");
		}
		contentPanel.clear();
		contentPanel.add(getFilesTable(btn));
		btn.addStyleName("btnSelected");
		btn.removeStyleName("btnNotSelected");
		btnLastClicked = btn;
	}

	private FlowPanel getFilePanel(FileTO fileTO) {
		FlowPanel fileWrapper = new FlowPanel();
		fileWrapper.addStyleName("fileWrapper");
		
		Image fileIcon = new Image(getIconImageByFileType(fileTO.getFileType()));
		fileIcon.addStyleName("fileIcon");
		fileWrapper.add(fileIcon);
		
		FlowPanel pnlFileName = new FlowPanel();
		pnlFileName.addStyleName("pnlFileName");
		
		Label fileName = new Label(fileTO.getFileName());
		fileName.addStyleName("fileName");
		pnlFileName.add(fileName);

		Label fileDescription = new Label(fileTO.getFileDescription());
		fileDescription.addStyleName("fileDescription");
		pnlFileName.add(fileDescription);
		
		fileWrapper.add(pnlFileName);

		Label fileSize = new Label(fileTO.getFileSize());
		fileSize.addStyleName("fileSize");
		fileWrapper.add(fileSize);
		
		FlowPanel starsPanel = new FlowPanel();
		starsPanel.addStyleName("starsPanel");
		for(int i = 1; i <= 5; i++){
			Image star = new Image(IMAGES_PATH + "star" + (i <= fileTO.getClassification() ? "On" : "Off") + ".png");
			starsPanel.add(star);
		}
		fileWrapper.add(starsPanel);

		Label publishingDate = new Label("01/10/2013 15:30"/*fileTO.getPublishingDate().toString()*/);
		publishingDate.addStyleName("publishingDate");
		fileWrapper.add(publishingDate);
		
		return fileWrapper;
	}

	private String getIconImageByFileType(String fileType) {
		if("pdf".equals(fileType) || "xls".equals(fileType) || "doc".equals(fileType)){
			return IMAGES_PATH + fileType + ".png";
		} else {
			return IMAGES_PATH + "unknownFileType.png";
		}
	}

	private void displayButtons() {
		//TODO i18n
		displayButton(btnAllFiles, "Todos os arquivos", true);
		displayButton(btnMostViewed, "Mais acessados", false);
		displayButton(btnTags, "Categoria", false);
		btnTags.setVisible(false);
		displayButton(btnMyFiles, "Meus arquivos", false);
		displayButton(btnIncludeFile, "Incluir arquivo", false);
	}

	private void displayButton(com.github.gwtbootstrap.client.ui.Button btn, String title, boolean selected) {
		btn.removeStyleName("btn");
		
		Label btnTitle = new Label(title);
		btnTitle.addStyleName("btnTitle");
		btn.add(btnTitle);

		btn.addClickHandler(new LibraryButtonClickHandler());
	}

	private final class LibraryHeaderClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			handleEvent((Button) event.getSource());
		}
	}

	private final class FileNameComparator implements Comparator<FileTO> {
		@Override
		public int compare(final FileTO object1, final FileTO object2) {
			if(object1.getFileName().compareTo(object2.getFileName()) == 0)
			    return object1.getFileDescription().compareTo(object2.getFileDescription());
			return object1.getFileName().compareTo(object2.getFileName());
		}
	}

	private final class FilePublishingDateComparator implements Comparator<FileTO> {
		@Override
		public int compare(final FileTO object1, final FileTO object2) {
			if(object1.getPublishingDate().compareTo(object2.getPublishingDate()) == 0)
				return object1.getFileName().compareTo(object2.getFileName());
		    return object1.getPublishingDate().compareTo(object2.getPublishingDate());
		}
	}

	private final class FileClassificationComparator implements Comparator<FileTO> {
		@Override
		public int compare(final FileTO object1, final FileTO object2) {
			if(object1.getClassification().compareTo(object2.getClassification()) == 0)
				return object1.getFileName().compareTo(object2.getFileName());
		    return object2.getClassification().compareTo(object1.getClassification());
		}
	}

	private final class FileSizeComparator implements Comparator<FileTO> {
		@Override
		public int compare(final FileTO object1, final FileTO object2) {
			try {
				int ret = 0;
				String[] parts1 = object1.getFileSize().split(" ");
				String[] parts2 = object2.getFileSize().split(" ");
				Integer value1 = Integer.parseInt(parts1[0]);
				String unit1 = parts1[1];
				Integer value2 = Integer.parseInt(parts2[0]);
				String unit2 = parts2[1];
				if(unit1.equals(unit2))
					ret = value2.compareTo(value1);
				ret = unit2.compareTo(unit1);
				if(ret == 0)
					return object1.getFileName().compareTo(object2.getFileName());
				return ret;
			} catch (Exception e) {
		        return object2.getFileSize().compareTo(object1.getFileSize());
			}
		}
	}

	private final class FileTypeComparator implements Comparator<FileTO> {
		@Override
		public int compare(final FileTO object1, final FileTO object2) {
			if(object1.getFileType().compareTo(object2.getFileType()) == 0)
				return object1.getFileName().compareTo(object2.getFileName());
		    return object1.getFileType().compareTo(object2.getFileType());
		}
	}

	private void handleEvent(com.github.gwtbootstrap.client.ui.Button btn) {		
		btnCurrent.removeStyleName("btnSelected");
		btnCurrent.addStyleName("btnNotSelected");
		btn.addStyleName("btnSelected");
		btn.removeStyleName("btnNotSelected");

		order = order == ORDER_ASCENDING ? ORDER_DESCENDING : ORDER_ASCENDING;
		displayContent(btn, btnLastClicked);
		btnCurrent = btn;
	}

	private final class LibraryButtonClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			handleEvent((com.github.gwtbootstrap.client.ui.Button) event.getSource());
		}
	}

	private FilesTO getAllFilesTO() {
		List<FileTO> files = new ArrayList<FileTO>();
		files.add(new FileTO(1,"pdf", "a Planos de marketing", "l Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "200 Mb", 1, new Date()));
		files.add(new FileTO(2,"xls", "b Calculadora de bonificações", "k A calculadora que permite você fazer simulações dos ganhos com base no seu número de vendas", "50 Mb", 2, new Date()));
		files.add(new FileTO(3,"doc", "c Passos para a elaboração de uma proposta", "j Utilize este documento para a elaboração de propostas comerciais", "2 Mb", 3, new Date()));
		files.add(new FileTO(4,"doc", "d Planos de marketing", "i Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "1 Mb", 3, new Date()));
		files.add(new FileTO(5,"pdf", "e Planos de marketing", "h Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "500 Kb", 4, new Date()));
		files.add(new FileTO(6,"pdf", "f Planos de marketing", "g Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "200 Kb", 5, new Date()));
		files.add(new FileTO(7,"pdf", "g Planos de marketing", "f Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "200 Mb", 1, new Date()));
		files.add(new FileTO(8,"xls", "h Calculadora de bonificações", "e A calculadora que permite você fazer simulações dos ganhos com base no seu número de vendas", "50 Mb", 2, new Date()));
		files.add(new FileTO(9,"doc", "i Passos para a elaboração de uma proposta", "d Utilize este documento para a elaboração de propostas comerciais", "2 Mb", 3, new Date()));
		files.add(new FileTO(10,"doc", "j Planos de marketing", "c Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "1 Mb", 4, new Date()));
		files.add(new FileTO(11,"pdf", "k Planos de marketing", "b Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "500 Kb", 5, new Date()));
		files.add(new FileTO(12,"pdf", "l Planos de marketing", "a Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "200 Kb", 1, new Date()));
		
		return new FilesTO(files);
	}

	private FilesTO getMyFilesTO() {
		List<FileTO> files = new ArrayList<FileTO>();
		files.add(new FileTO(2,"xls", "b Calculadora de bonificações", "k A calculadora que permite você fazer simulações dos ganhos com base no seu número de vendas", "50 Mb", 2, new Date()));
		files.add(new FileTO(3,"doc", "c Passos para a elaboração de uma proposta", "j Utilize este documento para a elaboração de propostas comerciais", "2 Mb", 3, new Date()));
		files.add(new FileTO(5,"pdf", "e Planos de marketing", "h Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "500 Kb", 4, new Date()));
		
		return new FilesTO(files);
	}

	private FilesTO getMostViewedFilesTO() {
		List<FileTO> files = new ArrayList<FileTO>();
		files.add(new FileTO(2,"xls", "b Calculadora de bonificações", "k A calculadora que permite você fazer simulações dos ganhos com base no seu número de vendas", "50 Mb", 2, new Date()));
		files.add(new FileTO(3,"doc", "c Passos para a elaboração de uma proposta", "j Utilize este documento para a elaboração de propostas comerciais", "2 Mb", 3, new Date()));
		files.add(new FileTO(4,"doc", "d Planos de marketing", "i Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "1 Mb", 3, new Date()));
		files.add(new FileTO(5,"pdf", "e Planos de marketing", "h Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "500 Kb", 4, new Date()));
		
		return new FilesTO(files);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated method stub
		
	}
}

class FilesTO{
	List<FileTO> files;
	public FilesTO(List<FileTO> files) {
		super();
		this.files = files;
	}
	public List<FileTO> getFiles() {
		return files;
	}
	public void setFiles(List<FileTO> files) {
		this.files = files;
	}
}
class FileTO {
	Integer id;
	String fileType;
	String fileName;
	String fileDescription;
	//Integer fileSize; //in bytes
	String fileSize;
	Integer classification;//1 - 5
	Date publishingDate;
	public FileTO(Integer id, String fileType, String fileName, String fileDescription, String fileSize, Integer classification, Date publishingDate) {
		super();
		this.id = id;
		this.fileType = fileType;
		this.fileName = fileName;
		this.fileDescription = fileDescription;
		this.fileSize = fileSize;
		this.classification = classification;
		this.publishingDate = publishingDate;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileDescription() {
		return fileDescription;
	}
	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public Integer getClassification() {
		return classification;
	}
	public void setClassification(Integer classification) {
		this.classification = classification;
	}
	public Date getPublishingDate() {
		return publishingDate;
	}
	public void setPublishingDate(Date publishingDate) {
		this.publishingDate = publishingDate;
	}
}
package kornell.gui.client.presentation.course.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import kornell.api.client.KornellClient;
import kornell.gui.client.KornellConstants;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class GenericCourseLibraryView extends Composite {

	interface MyUiBinder extends UiBinder<Widget, GenericCourseLibraryView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	
	private KornellClient client;
	private PlaceController placeCtrl;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private String IMAGES_PATH = "skins/first/icons/courseLibrary/";

	@UiField
	FlowPanel libraryPanel;
	@UiField
	FlowPanel titlePanel;
	@UiField
	FlowPanel buttonsPanel; 
	@UiField
	FlowPanel filesPanel; 
	@UiField
	FlowPanel filesHeader;
	@UiField
	FlowPanel filesWrapper; 
	
	FilesTO filesTO;
	com.google.gwt.user.client.ui.Button btnIcon;
	com.google.gwt.user.client.ui.Button btnName; 
	com.google.gwt.user.client.ui.Button btnSize;
	com.google.gwt.user.client.ui.Button btnClassification;
	com.google.gwt.user.client.ui.Button btnPublishingDate;
	
	Map<Integer, Widget> fileWidgetMap = new HashMap<Integer, Widget>();
	
	public GenericCourseLibraryView(KornellClient client, PlaceController placeCtrl) {
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
		// TODO get info	
		filesTO = getFilesTO();
		display();
	}

	private void display() {
		//TODO i18n
		displayTitle();
		displayButtons();
		displayFilesTable();
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

	private void displayFilesTable() {
		displayFilesTable(null);
	}
	private void displayFilesTable(Object source) {
		displayHeader();
		displayFiles(source);
	}

	private void displayFiles(Object source) {
		ArrayList<FileTO> list = (ArrayList<FileTO>) filesTO.getFiles();
		
		if (list.size() > 0) {
			if(btnIcon.equals(source)){
			    Collections.sort(list, new Comparator<FileTO>() {
			        @Override
			        public int compare(final FileTO object1, final FileTO object2) {
			            return object1.getFileType().compareTo(object2.getFileType());
			        }
			     });
			} else if(btnSize.equals(source)){
			    Collections.sort(list, new Comparator<FileTO>() {
			        @Override
			        public int compare(final FileTO object1, final FileTO object2) {
			        	try {
							String[] parts1 = object1.getFileSize().split(" ");
							String[] parts2 = object2.getFileSize().split(" ");
							Integer value1 = Integer.parseInt(parts1[0]);
							String unit1 = parts1[1];
							Integer value2 = Integer.parseInt(parts2[0]);
							String unit2 = parts2[1];
							if(unit1.equals(unit2))
							    return value2.compareTo(value1);
							return unit2.compareTo(unit1);
						} catch (Exception e) {
				            return object2.getFileSize().compareTo(object1.getFileSize());
						}
			        	
			        }
			     });
			} else if(btnClassification.equals(source)){
			    Collections.sort(list, new Comparator<FileTO>() {
			        @Override
			        public int compare(final FileTO object1, final FileTO object2) {
			            return object2.getClassification().compareTo(object1.getClassification());
			        }
			     });
			} else if(btnPublishingDate.equals(source)){
			    Collections.sort(list, new Comparator<FileTO>() {
			        @Override
			        public int compare(final FileTO object1, final FileTO object2) {
			            return object1.getPublishingDate().compareTo(object2.getPublishingDate());
			        }
			     });
			} else {
			    Collections.sort(list, new Comparator<FileTO>() {
			        @Override
			        public int compare(final FileTO object1, final FileTO object2) {
			            return object1.getFileName().compareTo(object2.getFileName());
			        }
			     });
			}
		}
		
		if(fileWidgetMap.size() <= 0){
			for (FileTO fileTO : list){
				fileWidgetMap.put(fileTO.getId(), getFilePanel(fileTO));
			}
		}

		filesWrapper.clear();
		for(FileTO fileTO : list){
			filesWrapper.add(fileWidgetMap.get(fileTO.getId()));
		}
	}

	private void displayHeader() {
		btnIcon = new com.google.gwt.user.client.ui.Button("Tipo");
		btnIcon.removeStyleName("btn");
		btnIcon.addStyleName("btnLibraryHeader");
		btnIcon.addStyleName("btnIcon");
		btnIcon.addStyleName("btnNotSelected");
		filesHeader.add(btnIcon);
		btnIcon.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				displayFiles(btnIcon);
				clearButtonSelection();
				btnIcon.addStyleName("btnSelected");
			}
		});

		btnName = new com.google.gwt.user.client.ui.Button("Nome do Arquivo");
		btnName.removeStyleName("btn");
		btnName.addStyleName("btnLibraryHeader");
		btnName.addStyleName("btnName");
		btnName.addStyleName("btnSelected");
		filesHeader.add(btnName);
		btnName.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				displayFiles(btnName);
				clearButtonSelection();
				btnName.addStyleName("btnSelected");
			}
		});

		btnSize = new com.google.gwt.user.client.ui.Button("Tamanho");
		btnSize.removeStyleName("btn");
		btnSize.addStyleName("btnLibraryHeader");
		btnSize.addStyleName("btnSize");
		btnSize.addStyleName("btnNotSelected");
		filesHeader.add(btnSize);
		btnSize.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				displayFiles(btnSize);
				clearButtonSelection();
				btnSize.addStyleName("btnSelected");
			}
		});

		btnClassification = new com.google.gwt.user.client.ui.Button("Classificação");
		btnClassification.removeStyleName("btn");
		btnClassification.addStyleName("btnLibraryHeader");
		btnClassification.addStyleName("btnClassification");
		btnClassification.addStyleName("btnNotSelected");
		filesHeader.add(btnClassification);
		btnClassification.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				displayFiles(btnClassification);
				clearButtonSelection();
				btnClassification.addStyleName("btnSelected");
			}
		});

		btnPublishingDate = new com.google.gwt.user.client.ui.Button("Data da publicação");
		btnPublishingDate.removeStyleName("btn");
		btnPublishingDate.addStyleName("btnLibraryHeader");
		btnPublishingDate.addStyleName("btnPublishingDate");
		btnPublishingDate.addStyleName("btnNotSelected");
		filesHeader.add(btnPublishingDate);
		btnPublishingDate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				displayFiles(btnPublishingDate);
				clearButtonSelection();
				btnPublishingDate.addStyleName("btnSelected");
			}
		});
	}

	private Widget getFilePanel(FileTO fileTO) {
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
		buttonsPanel.add(displayButton("Todos os arquivos", true));
		buttonsPanel.add(displayButton("Mais acessados"));
		buttonsPanel.add(displayButton("Categoria"));
		buttonsPanel.add(displayButton("Meus arquivos"));
		buttonsPanel.add(displayButton("Incluir arquivo"));
	}

	private Button displayButton(String title) {
		return displayButton(title, false);
	}

	private Button displayButton(String title, boolean selected) {
		Button btn = new Button();
		btn.addStyleName("btnLibrary");
		btn.addStyleName(selected ? "btnSelected" : "btnNotSelected");
		btn.removeStyleName("btn");
		
		Label btnTitle = new Label(title);
		btnTitle.addStyleName("btnTitle");
		btn.add(btnTitle);
		
		return btn;
	}

	private void clearButtonSelection() {
		btnIcon.removeStyleName("btnSelected");
		btnName.removeStyleName("btnSelected"); 
		btnSize.removeStyleName("btnSelected");
		btnClassification.removeStyleName("btnSelected");
		btnPublishingDate.removeStyleName("btnSelected");
		btnIcon.addStyleName("btnNotSelected");
		btnName.addStyleName("btnNotSelected"); 
		btnSize.addStyleName("btnNotSelected");
		btnClassification.addStyleName("btnNotSelected");
		btnPublishingDate.addStyleName("btnNotSelected");
	}


	private FilesTO getFilesTO() {
		List<FileTO> files = new ArrayList<FileTO>();
		files.add(new FileTO(1,"pdf", "Planos de marketing", "Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "200 Mb", 1, new Date()));
		files.add(new FileTO(2,"xls", "Calculadora de bonificações", "A calculadora que permite você fazer simulações dos ganhos com base no seu número de vendas", "50 Mb", 2, new Date()));
		files.add(new FileTO(3,"doc", "Passos para a elaboração de uma proposta", "Utilize este documento para a elaboração de propostas comerciais", "2 Mb", 3, new Date()));
		files.add(new FileTO(4,"doc", "Planos de marketing", "Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "1 Mb", 3, new Date()));
		files.add(new FileTO(5,"pdf", "Planos de marketing", "Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "500 Kb", 4, new Date()));
		files.add(new FileTO(6,"pdf", "Planos de marketing", "Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "200 Kb", 5, new Date()));
		files.add(new FileTO(7,"pdf", "Planos de marketing", "Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "200 Mb", 1, new Date()));
		files.add(new FileTO(8,"xls", "Calculadora de bonificações", "A calculadora que permite você fazer simulações dos ganhos com base no seu número de vendas", "50 Mb", 2, new Date()));
		files.add(new FileTO(9,"doc", "Passos para a elaboração de uma proposta", "Utilize este documento para a elaboração de propostas comerciais", "2 Mb", 3, new Date()));
		files.add(new FileTO(10,"doc", "Planos de marketing", "Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "1 Mb", 4, new Date()));
		files.add(new FileTO(11,"pdf", "Planos de marketing", "Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "500 Kb", 5, new Date()));
		files.add(new FileTO(12,"pdf", "Planos de marketing", "Utilize este documento para a elaboração de propostas comerciais. Conheça várias abordagens.", "200 Kb", 1, new Date()));
		
		return new FilesTO(files);
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
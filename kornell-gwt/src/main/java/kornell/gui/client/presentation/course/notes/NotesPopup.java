package kornell.gui.client.presentation.course.notes;

import kornell.api.client.KornellClient;
import kornell.core.shared.to.CourseTO;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;

public class NotesPopup {
	PopupPanel popup;
	PopupPanel glass;
	TextArea richTextArea;
	KornellClient client;
	CourseTO course;

	// TODO: fetch these dynamically
	int NORTH_BAR = 45;
	int SOUTH_BAR = 35;
	int BAR_WIDTH = 962;
	int NOTES_MIN_HEIGHT = 300;
	

	public NotesPopup(final KornellClient client, final CourseTO course) {
		this.course = course;
		this.client = client;

		glass = new PopupPanel();
		glass.setStyleName("notesGlass");

		popup = new PopupPanel(true);
		popup.setStyleName("notesPopup");

		richTextArea = new TextArea();
		richTextArea.setText(course.getEnrollment().getNotes());
		richTextArea.setStyleName("notesTextArea");

		popup.add(richTextArea);
		
		addHandlers();
	}
	
	private void updateNotes(){
		client.notesUpdated(course.getCourse().getUUID(), richTextArea.getText());
	}
	
	private void placePopup() {
		int left = (Window.getClientWidth() - BAR_WIDTH) / 2;
		left = (Window.getClientWidth() % 2 == 0) ? left : left + 1;
		int top = Window.getClientHeight() - SOUTH_BAR - NOTES_MIN_HEIGHT;
		
		popup.setPopupPosition(Math.max(left, 0), Math.max(top, 0));
		popup.setWidth(BAR_WIDTH + "px");
		popup.getElement().getStyle().setPropertyPx("size", NOTES_MIN_HEIGHT);
		popup.getElement().getStyle().setPropertyPx("bottom", SOUTH_BAR);
		popup.setVisible(true);
	}

	public void show() {
		placePopup();
		glass.show();
		popup.show();

		richTextArea.setFocus(true);
		/* PUTS THE CURSOR ATE THE END
		String tmp = richTextArea.getText();
		richTextArea.setText("");
		richTextArea.setText(tmp);*/
	}

	private void addHandlers() {
		richTextArea.addKeyUpHandler(new KeyUpHandler() {
			Timer updateTimer = new Timer() {
				@Override
				public void run() {
					updateNotes();
				}
			};
			@Override
			public void onKeyUp(KeyUpEvent event) {
				updateTimer.cancel();
				updateTimer.schedule(1000);
			}
		});

		Window.addResizeHandler(new ResizeHandler() {
			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					placePopup();
				}
			};
			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.cancel();
				resizeTimer.schedule(250);
			}
		});

		popup.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				updateNotes();
				glass.hide();
			}
		});
	}
}
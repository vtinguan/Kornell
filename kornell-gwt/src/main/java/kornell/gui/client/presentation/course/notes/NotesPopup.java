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

	int wrapperWidth;

	public NotesPopup(Integer wrapperWidth, final KornellClient client, final CourseTO course) {
		this.wrapperWidth = wrapperWidth;
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
	
	private void updateNotes(){
		client.notesUpdated(course.getCourse().getUUID(), richTextArea.getText());
	}
	
	private void placePopup() {
		int left = (Window.getClientWidth() - wrapperWidth) / 2;
		left = (Window.getClientWidth() % 2 == 0) ? left : left + 1;
		int top = 0;
		if(Window.getClientHeight() > 500){
			top = (Window.getClientHeight() - popup.getOffsetHeight()) / 2;
		}
		
		popup.setPopupPosition(Math.max(left, 0), Math.max(top, 0));
		popup.setWidth(wrapperWidth + "px");
		popup.getElement().getStyle().setPropertyPx("size", 300);
		popup.getElement().getStyle().setPropertyPx("bottom", 35);
	}

	public void show() {
		placePopup();
		glass.show();
		popup.setVisible(true);
		popup.show();
		richTextArea.setFocus(true);
	}
}
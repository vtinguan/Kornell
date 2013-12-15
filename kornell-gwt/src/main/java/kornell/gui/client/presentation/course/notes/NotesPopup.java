package kornell.gui.client.presentation.course.notes;

import kornell.api.client.KornellClient;
import kornell.gui.client.util.Positioning;

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
	String courseClassUUID;
	String notes;

	

	public NotesPopup(final KornellClient client, final String courseUUID, final String notes) {
		this.courseClassUUID = courseUUID;
		this.notes = notes;
		this.client = client;

		glass = new PopupPanel();
		glass.addStyleName("gwt-PopupPanelGlass");

		popup = new PopupPanel(true);
		popup.addStyleName("notesPopup");

		richTextArea = new TextArea();
		richTextArea.setText(notes);
		richTextArea.addStyleName("notesTextArea");

		popup.add(richTextArea);
		
		addHandlers();
	}
	
	private void updateNotes(){
		client.notesUpdated(courseClassUUID, richTextArea.getText());
	}
	
	private void placePopup() {
		int left = (Window.getClientWidth() - Positioning.BAR_WIDTH) / 2;
		left = (Window.getClientWidth() % 2 == 0) ? left : left + 1;
		int top = Window.getClientHeight() - Positioning.SOUTH_BAR - Positioning.NOTES_MIN_HEIGHT;
		
		popup.setPopupPosition(Math.max(left, 0), Math.max(top, 0));
		popup.setWidth(Positioning.BAR_WIDTH + "px");
		popup.getElement().getStyle().setPropertyPx("size", Positioning.NOTES_MIN_HEIGHT);
		popup.getElement().getStyle().setPropertyPx("bottom", Positioning.SOUTH_BAR);
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
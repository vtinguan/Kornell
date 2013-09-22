package kornell.gui.client.event;

import kornell.core.shared.data.Institution;

import com.google.gwt.event.shared.GwtEvent;

public class InstitutionEvent extends GwtEvent<InstitutionEventHandler> {
	public static final Type<InstitutionEventHandler> TYPE = new Type<InstitutionEventHandler>();

	private Institution institution;

	@Override
	public Type<InstitutionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(InstitutionEventHandler handler) {
		handler.onEnter(this);
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

}

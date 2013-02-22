package kornell.gui.client.presentation.atividade;


import com.google.gwt.user.client.ui.IsWidget;

public interface AtividadeView  extends IsWidget{
	public interface Presenter extends IsWidget {
		void goContinue();
		void goPrevious();
	}

	void setPresenter(Presenter presenter);

}

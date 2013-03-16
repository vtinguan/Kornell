package kornell.gui.client.content;

import kornell.api.client.KornellClient;


public class RendererFactoryImpl implements RendererFactory {

	private KornellClient client;
	
	
	public RendererFactoryImpl(KornellClient client) {
		this.client = client;
	}

	@Override
	public Renderer source(String uuid, Integer position) {
		return new SurrogateRenderer(client, uuid,position);
	}

}

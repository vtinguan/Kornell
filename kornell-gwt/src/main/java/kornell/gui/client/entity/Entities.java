package kornell.gui.client.entity;

import com.google.gwt.core.shared.GWT;

import kornell.core.entity.EntityFactory;


public class Entities extends kornell.core.entity.Entities{
	private static final Entities instance = new Entities();
	private static final EntityFactory entityFactory = GWT.create(EntityFactory.class);
	
	@Override
	protected EntityFactory getEntityFactory() {		
		return entityFactory;
	}

	public static Entities get() {
		return instance;
	}

}

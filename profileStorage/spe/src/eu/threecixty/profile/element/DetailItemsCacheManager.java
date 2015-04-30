package eu.threecixty.profile.element;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.threecixty.profile.elements.ElementDetails;



public class DetailItemsCacheManager {
	
	private static final DetailItemsCacheManager INSTANCE = new DetailItemsCacheManager();
	
	private Map <String, ElementDetails> caches;
	
	public static DetailItemsCacheManager getInstance() {
		return INSTANCE;
	}
	
	public void put(ElementDetails element) {
		if (element == null) return;
		caches.put(element.getId(), element);
	}
	
	public void put(Collection <ElementDetails> elements) {
		for (ElementDetails element: elements) {
			caches.put(element.getId(), element);
		}
	}
	
	public ElementDetails get(String id) {
		if (id == null) return null;
		return caches.get(id);
	}
	
	private DetailItemsCacheManager() {
		caches = new HashMap<String, ElementDetails>();
	}
}

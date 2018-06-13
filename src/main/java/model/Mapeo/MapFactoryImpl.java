package model.Mapeo;

import java.util.HashMap;
import java.util.Map;

public class MapFactoryImpl<K,V> implements MapFactory<K, V> {
	
	public MapFactoryImpl(){}

	@Override
	public Map<K, V> createMap() {
		return new HashMap<K,V>();
	}

}

package model.Mapeo;

import java.util.Map;

public interface MapFactory<K,V> {
	public Map<K,V> createMap();
}

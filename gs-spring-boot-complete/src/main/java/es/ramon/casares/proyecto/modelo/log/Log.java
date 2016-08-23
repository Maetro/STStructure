package es.ramon.casares.proyecto.modelo.log;

import java.util.HashMap;
import java.util.Map;

public class Log {

	private Map<Integer, MovimientoComprimido> objetoMovimientoMap = new HashMap<Integer, MovimientoComprimido>();

	public Log(Map<Integer, MovimientoComprimido> objetoMovimientoMap) {
		super();
		this.objetoMovimientoMap = objetoMovimientoMap;
	}

	public Map<Integer, MovimientoComprimido> getObjetoMovimientoMap() {
		return objetoMovimientoMap;
	}

	public void setObjetoMovimientoMap(Map<Integer, MovimientoComprimido> objetoMovimientoMap) {
		this.objetoMovimientoMap = objetoMovimientoMap;
	}
	
	
}

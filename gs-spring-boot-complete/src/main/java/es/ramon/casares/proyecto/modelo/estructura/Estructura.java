package es.ramon.casares.proyecto.modelo.estructura;

import java.util.HashMap;
import java.util.Map;

import es.ramon.casares.proyecto.modelo.log.Log;
import es.ramon.casares.proyecto.modelo.snapshot.Snapshot;

public class Estructura {

	private Map<Integer, Snapshot> snapshots = new HashMap<Integer,Snapshot>();
	
	private Map<Integer, Log> logs = new HashMap<Integer,Log>();
	
	public Estructura(Map<Integer, Snapshot> snapshots, Map<Integer, Log> logs) {
		super();
		this.snapshots = snapshots;
		this.logs = logs;
	}

	public Map<Integer, Snapshot> getSnapshots() {
		return snapshots;
	}

	public void setSnapshots(Map<Integer, Snapshot> snapshots) {
		this.snapshots = snapshots;
	}

	public Map<Integer, Log> getLogs() {
		return logs;
	}

	public void setLogs(Map<Integer, Log> logs) {
		this.logs = logs;
	}
	
}

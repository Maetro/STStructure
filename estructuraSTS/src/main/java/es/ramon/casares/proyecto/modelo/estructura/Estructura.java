/**
 * Estructura.java 27-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.modelo.estructura;

import java.util.HashMap;
import java.util.Map;

import es.ramon.casares.proyecto.modelo.log.Log;
import es.ramon.casares.proyecto.modelo.snapshot.Snapshot;

/**
 * The Class Estructura.
 */
public class Estructura {

    /** The snapshots. */
    private Map<Integer, Snapshot> snapshots = new HashMap<Integer, Snapshot>();

    /** The logs. */
    private Map<Integer, Log> logs = new HashMap<Integer, Log>();

    /**
     * Instantiates a new estructura.
     *
     * @param snapshotsP
     *            the snapshots
     * @param logsP
     *            the logs
     */
    public Estructura(final Map<Integer, Snapshot> snapshotsP, final Map<Integer, Log> logsP) {
        super();
        this.snapshots = snapshotsP;
        this.logs = logsP;
    }

    /**
     * Gets the snapshots.
     *
     * @return the snapshots
     */
    public final Map<Integer, Snapshot> getSnapshots() {
        return this.snapshots;
    }

    /**
     * Establece snapshots.
     *
     * @param snapshotsP
     *            the snapshots
     */
    public final void setSnapshots(final Map<Integer, Snapshot> snapshotsP) {
        this.snapshots = snapshotsP;
    }

    /**
     * Gets the logs.
     *
     * @return the logs
     */
    public final Map<Integer, Log> getLogs() {
        return this.logs;
    }

    /**
     * Establece logs.
     *
     * @param logsP
     *            the logs
     */
    public final void setLogs(final Map<Integer, Log> logsP) {
        this.logs = logsP;
    }

}

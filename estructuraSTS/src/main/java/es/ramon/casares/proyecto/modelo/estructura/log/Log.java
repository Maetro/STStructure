/**
 * Log.java 27-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.modelo.estructura.log;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class Log.
 */
public class Log {

    /** The objeto movimiento map. */
    private Map<Integer, MovimientoComprimido> objetoMovimientoMap = new HashMap<Integer, MovimientoComprimido>();

    /**
     * Instantiates a new log.
     *
     * @param objetoMovimientoMapP
     *            the objeto movimiento map
     */
    public Log(final Map<Integer, MovimientoComprimido> objetoMovimientoMapP) {
        super();
        this.objetoMovimientoMap = objetoMovimientoMapP;
    }

    /**
     * Gets the objeto movimiento map.
     *
     * @return the objeto movimiento map
     */
    public final Map<Integer, MovimientoComprimido> getObjetoMovimientoMap() {
        return this.objetoMovimientoMap;
    }

    /**
     * Establece objeto movimiento map.
     *
     * @param objetoMovimientoMap
     *            the objeto movimiento map
     */
    public final void setObjetoMovimientoMap(final Map<Integer, MovimientoComprimido> objetoMovimientoMapP) {
        this.objetoMovimientoMap = objetoMovimientoMapP;
    }

}

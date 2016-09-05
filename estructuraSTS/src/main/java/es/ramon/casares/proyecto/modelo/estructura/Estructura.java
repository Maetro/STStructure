/**
 * Estructura.java 27-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.modelo.estructura;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ramon.casares.proyecto.modelo.estructura.log.Log;
import es.ramon.casares.proyecto.modelo.estructura.snapshot.Snapshot;
import es.ramon.casares.proyecto.parametros.ComprimirEstructuraParametersBean;
import es.ramon.casares.proyecto.parametros.ParametrosCompresionLogsBean;

/**
 * The Class Estructura.
 */
public class Estructura {

    /** The snapshots. */
    private Map<Integer, Snapshot> snapshots = new HashMap<Integer, Snapshot>();

    /** The logs. */
    private Map<Integer, Log> logs = new HashMap<Integer, Log>();

    /** The numero cuadrados. */
    private final Integer numeroCuadrados;

    private ComprimirEstructuraParametersBean cabecera;

    /** The movimientos por frecuencia. */
    private List<Integer> movimientosPorFrecuencia;

    /** The parametros. */
    private ParametrosCompresionLogsBean parametros;

    /**
     * Instantiates a new estructura.
     *
     * @param snapshotsP
     *            the snapshots
     * @param logsP
     *            the logs
     * @param numeroCuadradosP
     *            the numero cuadrados p
     * @param cabeceraP
     *            the cabecera p
     * @param movimientosPorFrecuenciaP
     *            the movimientos por frecuencia p
     */
    public Estructura(final Map<Integer, Snapshot> snapshotsP, final Map<Integer, Log> logsP,
            final Integer numeroCuadradosP, final ComprimirEstructuraParametersBean cabeceraP,
            final List<Integer> movimientosPorFrecuenciaP, final ParametrosCompresionLogsBean parametrosP) {
        super();
        this.snapshots = snapshotsP;
        this.logs = logsP;
        this.numeroCuadrados = numeroCuadradosP;
        this.cabecera = cabeceraP;
        this.movimientosPorFrecuencia = movimientosPorFrecuenciaP;
        this.parametros = parametrosP;

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

    /**
     * Obtiene numero cuadrados.
     *
     * @return numero cuadrados
     */
    public Integer getNumeroCuadrados() {
        return this.numeroCuadrados;
    }

    /**
     * Gets the cabecera.
     *
     * @return the cabecera
     */
    public ComprimirEstructuraParametersBean getCabecera() {
        return this.cabecera;
    }

    /**
     * Sets the cabecera.
     *
     * @param cabecera
     *            the new cabecera
     */
    public void setCabecera(final ComprimirEstructuraParametersBean cabecera) {
        this.cabecera = cabecera;
    }

    /**
     * Gets the movimientos por frecuencia.
     *
     * @return the movimientos por frecuencia
     */
    public List<Integer> getMovimientosPorFrecuencia() {
        return this.movimientosPorFrecuencia;
    }

    /**
     * Sets the movimientos por frecuencia.
     *
     * @param movimientosPorFrecuencia
     *            the new movimientos por frecuencia
     */
    public void setMovimientosPorFrecuencia(final List<Integer> movimientosPorFrecuencia) {
        this.movimientosPorFrecuencia = movimientosPorFrecuencia;
    }

    /**
     * Gets the parametros.
     *
     * @return the parametros
     */
    public ParametrosCompresionLogsBean getParametros() {
        return this.parametros;
    }

    /**
     * Sets the parametros.
     *
     * @param parametros
     *            the new parametros
     */
    public void setParametros(final ParametrosCompresionLogsBean parametros) {
        this.parametros = parametros;
    }

}

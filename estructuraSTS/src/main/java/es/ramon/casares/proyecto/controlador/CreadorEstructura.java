/**
 * CreadorEstructura.java 27-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */

package es.ramon.casares.proyecto.controlador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ramon.casares.proyecto.controlador.helpers.CompresorEstructuraHelper;
import es.ramon.casares.proyecto.controlador.helpers.ConfiguracionHelper;
import es.ramon.casares.proyecto.controlador.limpieza.SolucionadorColisiones.ImpossibleToSolveColisionException;
import es.ramon.casares.proyecto.encoder.SCDenseCoder;
import es.ramon.casares.proyecto.modelo.estructura.log.Log;
import es.ramon.casares.proyecto.modelo.estructura.log.Movimiento;
import es.ramon.casares.proyecto.modelo.estructura.log.MovimientoComprimido;
import es.ramon.casares.proyecto.modelo.estructura.snapshot.Snapshot;
import es.ramon.casares.proyecto.modelo.estructura.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.util.K2TreeHelper;
import es.ramon.casares.proyecto.parametros.ParametrosCompresionLogsBean;
import es.ramon.casares.proyecto.util.ByteFileUtil;
import es.ramon.casares.proyecto.util.FunctionUtils;
import es.ramon.casares.proyecto.util.objetos.ObjetoMovil;
import es.ramon.casares.proyecto.util.objetos.Posicion;

/**
 * The Class CreadorEstructura.
 */
public class CreadorEstructura {

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(CreadorEstructura.class);

    /** Las posiciones ocupadas en el momento actual del fichero. */
    private final HashMap<Posicion, ObjetoMovil> posicionIds = new HashMap<Posicion, ObjetoMovil>();

    /** The desaparecido relativo. */
    private final Set<Integer> desaparecidoRelativo = new HashSet<Integer>();

    /** mapa que guarda el estado del objeto en el instante de la desaparicion. */
    private final Map<Integer, ObjetoMovil> ultimaPosicionDesaparecidos = new HashMap<Integer, ObjetoMovil>();

    /** The mapa de log. */
    private final Map<Integer, List<Integer>> mapaDeLog = new HashMap<Integer, List<Integer>>();

    /** The limite log. */
    private Integer limiteLog;

    /** The limite snapshot. */
    private Integer limiteSnapshot;

    /** The numero objetos. */
    private Integer numeroObjetos;

    /** The datareader. */
    private RandomAccessFile datareader; // es

    /** The desaparicion. */
    private Movimiento desaparicion;

    /** The reaparicion. */
    private Movimiento reaparicionRelativa;

    /** The reaparicion relativa fuera limites. */
    private Movimiento reaparicionFueraLimites;

    /** The reaparicion absoluta. */
    private Movimiento reaparicionAbsoluta;

    /** The movimientos por frecuencia. */
    private final List<Integer> movimientosPorFrecuencia = new ArrayList<Integer>();

    /** The mapa ids. */
    private final HashMap<Integer, ObjetoMovil> mapaIds = new HashMap<Integer, ObjetoMovil>();

    /** The snapshots. */
    private Snapshot snapshot;

    /** The logs. */
    private final List<Log> logs = new ArrayList<Log>();

    /** The encoder. */
    private SCDenseCoder encoder;

    /** The numero bloques. */
    private int numeroBloques = 0;

    /** The punteros. */
    private final List<Integer> punteros = new ArrayList<Integer>();

    /** The parametros. */
    private ParametrosCompresionLogsBean parametros;

    /** The puntero. */
    private int puntero;

    /** The instantes hasta desparicion. */
    private int instantesHastaDesparicion;

    /**
     * Instantiates a new creador estructura.
     */
    public CreadorEstructura() {
    }

    /**
     * Instancia un nuevo creador fichero frecuencias.
     * 
     * @param limiteLogP
     *            limite
     * @param limiteSnapshotP
     *            the limite snapshot
     * @param numeroObjetosP
     *            the numero objetos
     */
    public CreadorEstructura(final Integer limiteLogP, final Integer limiteSnapshotP, final Integer numeroObjetosP) {
        this.limiteLog = limiteLogP;
        this.limiteSnapshot = limiteSnapshotP;
        this.numeroObjetos = numeroObjetosP;
    }

    /**
     * Inicializar.
     * 
     * @param ficheroFrecuencias
     *            the fichero frecuencias
     * @param configuracion
     *            the configuracion
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public final void inicializar(final File ficheroFrecuencias, final ConfiguracionHelper configuracion)
            throws FileNotFoundException, IOException {

        this.desaparicion = new Movimiento(this.limiteLog, this.limiteLog);
        this.reaparicionRelativa = new Movimiento(-this.limiteLog, -this.limiteLog);
        this.reaparicionAbsoluta = new Movimiento(this.limiteLog, -this.limiteLog);
        this.reaparicionFueraLimites = new Movimiento(-this.limiteLog, this.limiteLog);

        this.datareader = new RandomAccessFile(ficheroFrecuencias, "r");
        String currentLine;
        while ((currentLine = this.datareader.readLine()) != null) {
            this.movimientosPorFrecuencia.add(Integer.valueOf(currentLine));
        }

        this.instantesHastaDesparicion = configuracion.getInstantesHastaDesaparicion();

        this.encoder = new SCDenseCoder(configuracion.getS(), configuracion.getC());

        this.parametros = new ParametrosCompresionLogsBean();
        this.parametros.setNumeroBloques(this.numeroBloques);
        this.parametros.setParametroC(configuracion.getC());
        this.parametros.setParametroS(configuracion.getS());

        this.parametros.setPosicionReaparicionAbsoluta(this.movimientosPorFrecuencia.indexOf(
                FunctionUtils.unidimensionar(this.reaparicionAbsoluta.getX(), this.reaparicionAbsoluta.getY())));
        this.parametros.setPosicionReaparicionRelativa(this.movimientosPorFrecuencia.indexOf(
                FunctionUtils.unidimensionar(this.reaparicionRelativa.getX(), this.reaparicionRelativa.getY())));
        this.parametros.setPosicionReaparicionFueraLimites(this.movimientosPorFrecuencia.indexOf(FunctionUtils
                .unidimensionar(this.reaparicionFueraLimites.getX(), this.reaparicionFueraLimites.getY())));

    }

    /**
     * Crear estructura.
     * 
     * @param ficheroDataSet
     *            the fichero
     * @param configuracion
     *            the configuracion
     * @return the estructura
     * @throws NumberFormatException
     *             the number format exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ImpossibleToSolveColisionException
     *             the impossible to solve colision exception
     */
    public final List<Integer> crearEstructura(final File ficheroDataSet, final ConfiguracionHelper configuracion)
            throws NumberFormatException, IOException, ImpossibleToSolveColisionException {
        String currentLine;

        logger.info("Generando estructura");
        final FileOutputStream tempFile = ByteFileUtil
                .crearFicheroEscrituraSiNoExiste("src/main/resources/EstructuraTemporal");
        int lastInstant = 0;
        this.datareader = new RandomAccessFile(ficheroDataSet, "r");

        while ((currentLine = this.datareader.readLine()) != null) {
            final String[] result = currentLine.trim().split("\\s");
            if (result.length >= 3) {
                final int instant = Integer.valueOf(result[0]); // En segundos
                final int id = Integer.valueOf(result[1]);
                final int x = Integer.valueOf(result[2]); // Longitud
                final int y = Integer.valueOf(result[3]); // Latitud

                lastInstant = analizarCambioInstante(configuracion, lastInstant, instant, tempFile);

                final Posicion claveNum = new Posicion(x, y);
                final ObjetoMovil nuevaPos = new ObjetoMovil(id, instant, x, y);

                if (this.mapaIds.containsKey(id)) {
                    // Si esta en el mapa hay que cambiar
                    // la posicion anotada
                    final ObjetoMovil viejaPos = this.mapaIds.get(id);
                    final Posicion viejaClaveNum = new Posicion(viejaPos.getPosicionX(), viejaPos.getPosicionY());
                    if (this.posicionIds.get(viejaClaveNum).getObjetoId() == id) {
                        this.posicionIds.remove(viejaClaveNum);
                        this.mapaIds.remove(viejaClaveNum);
                    }

                    if (!this.posicionIds.containsKey(claveNum)) { // no hay
                                                                   // colision
                        anotarMovimiento(id, claveNum, nuevaPos, viejaPos);
                    } else { // Hay colision

                        throw new InternalError("COLISION");
                    }
                } else {
                    // Es la primera vez que se anota este objeto o estaba
                    // desaparecido
                    // Miramos si su posicion esta ocupada
                    if (this.desaparecidoRelativo.contains(id)) {
                        if (!this.posicionIds.containsKey(claveNum)) { // no hay
                                                                       // colision
                            anotarReaparicionRelativa(id, x, y, nuevaPos);
                        } else { // Hay colision
                            throw new InternalError("COLISION");
                        }

                    } else if (instant == 0) {
                        // Posicion libre
                        anotarMovimiento(id, claveNum, nuevaPos, null);
                    } else {
                        anotarReaparicionAbsoluta(id, x, y, nuevaPos);
                    }
                }

            }
        }

        if (!this.mapaDeLog.isEmpty()) {
            procesarCambioInstante(configuracion, lastInstant, tempFile);
        }
        this.punteros.add(this.puntero);
        this.puntero = CompresorEstructuraHelper.comprimirBloqueSnapshotLog(this.snapshot, this.logs, tempFile,
                this.parametros, this.puntero);
        logger.info("Snapshot: " + this.numeroBloques);
        this.numeroBloques++;

        this.datareader.close();
        tempFile.close();
        return this.punteros;

    }

    /**
     * Analizar cambio instante.
     * 
     * @param configuracion
     *            the configuracion
     * @param lastInstant
     *            the last instant
     * @param instant
     *            the instant
     * @param tempFile
     * @return the int
     * @throws IOException
     */
    private int analizarCambioInstante(final ConfiguracionHelper configuracion, final int lastInstant,
            final int instant, final FileOutputStream tempFile) throws IOException {

        int ultimoInstante = lastInstant;
        if (instant != ultimoInstante) {
            // Cambio de instante. Comprobamos si algun objeto ha
            // desaparecido
            if ((instant - ultimoInstante) > 1) {
                // Hay instantes que no tienen ningun movimiento registrado
                while (ultimoInstante < (instant - 1)) {
                    ultimoInstante++;
                    ultimoInstante = procesarCambioInstante(configuracion, ultimoInstante, tempFile);

                }

            }

            ultimoInstante = procesarCambioInstante(configuracion, instant, tempFile);
        }
        return ultimoInstante;
    }

    /**
     * Procesar cambio instante.
     * 
     * @param configuracion
     *            the configuracion
     * @param instant
     *            the instant
     * @param tempFile
     * @return the int
     * @throws IOException
     */
    private int procesarCambioInstante(final ConfiguracionHelper configuracion, final int instant,
            final FileOutputStream tempFile) throws IOException {
        int lastInstant;
        lastInstant = instant;

        final Set<Posicion> posiciones = new HashSet<Posicion>(this.posicionIds.keySet());
        for (final Posicion posicion : posiciones) {
            final ObjetoMovil objeto = this.posicionIds.get(posicion);
            if ((objeto.getInstante() + configuracion.getInstantesHastaDesaparicion()) <= instant) {
                // El objeto ha desaparecido

                this.ultimaPosicionDesaparecidos.put(objeto.getObjetoId(), objeto);
                this.posicionIds.remove(posicion);
                this.mapaIds.remove(objeto.getObjetoId());
                this.desaparecidoRelativo.add(objeto.getObjetoId());

                final int numeroEspiral = FunctionUtils.unidimensionar(this.desaparicion.getX(),
                        this.desaparicion.getY());
                final int posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);
                final List<Integer> word = this.encoder.encode(posicionNumero);
                System.out.println("D (" + this.desaparicion.getX() + ","
                        + this.desaparicion.getY() + ") ->" + numeroEspiral + " -> "
                        + posicionNumero + " -> " + word);
                this.mapaDeLog.put(objeto.getObjetoId(), word);
            }
        }

        creacionLogEnCambioInstante(configuracion, instant);

        creacionSnapshotEnCambioInstante(configuracion, instant, tempFile);
        return lastInstant;
    }

    /**
     * Creacion log en cambio instante.
     * 
     * @param configuracion
     *            the configuracion
     * @param instant
     *            the instant
     */
    private void creacionLogEnCambioInstante(final ConfiguracionHelper configuracion, final int instant) {
        if ((instant - 1) > 0) {
            final Map<Integer, MovimientoComprimido> objetoMovimientoMap = new HashMap<Integer, MovimientoComprimido>();
            for (int i = 1; i <= this.numeroObjetos; i++) {
                MovimientoComprimido movimientoComprimido = null;
                if (this.mapaDeLog.containsKey(i)) {
                    final List<Integer> mov = this.mapaDeLog.get(i);
                    movimientoComprimido = new MovimientoComprimido(mov);
                }
                objetoMovimientoMap.put(i, movimientoComprimido);
            }
            final Log log = new Log(objetoMovimientoMap);
            this.logs.add(log);
            this.mapaDeLog.clear();
            logger.debug("LOG: " + (instant - 1));
        }

    }

    /**
     * Creacion snapshot en cambio instante.
     * 
     * @param configuracion
     *            the configuracion
     * @param instant
     *            the instant
     * @param tempFile
     * @throws IOException
     */
    private void creacionSnapshotEnCambioInstante(final ConfiguracionHelper configuracion, final int instant,
            final FileOutputStream tempFile) throws IOException {
        if (((instant - 1) % configuracion.getDistanciaEntreSnapshots()) == 0) {
            // Primero almacenamos el snapshot y los logs anteriores en el fichero comprimido
            if ((instant - 1) != 0) {
                this.punteros.add(this.puntero);
                this.puntero = CompresorEstructuraHelper.comprimirBloqueSnapshotLog(this.snapshot, this.logs, tempFile,
                        this.parametros, this.puntero);
                logger.info("Snapshot: " + this.numeroBloques);
                this.numeroBloques++;
            }
            this.logs.clear();

            // Generamos Snapshot
            this.ultimaPosicionDesaparecidos.clear();
            this.desaparecidoRelativo.clear();
            // Punto de generacion de Snapshot
            final K2Tree k2Tree = K2TreeHelper.generarK2Tree(this.posicionIds,
                    FunctionUtils.numeroCuadradosSegunLimite(this.limiteSnapshot));
            final byte[] bytes = K2TreeHelper.serializarK2Tree(k2Tree);
            final int tamanoBytes = K2TreeHelper.obtenerTamanoK2Tree(k2Tree);
            logger.debug("NumBytes : " + tamanoBytes);
            logger.debug("Real     : " + bytes.length);
            k2Tree.equals(k2Tree);
            this.snapshot = k2Tree;
            logger.info("SNAPSHOT: " + (instant - 1));

        }
    }

    /**
     * Anotar reaparicion absoluta.
     * 
     * @param id
     *            the id
     * @param x
     *            the x
     * @param y
     *            the y
     * @param nuevaPos
     *            the nueva pos
     */
    private void anotarReaparicionAbsoluta(final int id, final int x, final int y, final ObjetoMovil nuevaPos) {
        final Posicion claveNum = new Posicion(x, y);
        this.posicionIds.put(claveNum, nuevaPos);
        this.mapaIds.put(id, nuevaPos);

        final int numeroEspiral = FunctionUtils.unidimensionar(this.reaparicionAbsoluta.getX(),
                this.reaparicionAbsoluta.getY());
        final int posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);
        final List<Integer> word = this.encoder.encode(posicionNumero);
        word.add(x);
        word.add(y);
        System.out.println("RA (" + this.reaparicionAbsoluta.getX() + ","
                + this.reaparicionAbsoluta.getY() + ") ->" + numeroEspiral + " -> "
                + posicionNumero + " -> " + word);
        this.mapaDeLog.put(id, word);

    }

    /**
     * Anotar reaparicion relativa.
     * 
     * @param id
     *            the id
     * @param x
     *            the x
     * @param y
     *            the y
     * @param nuevaPos
     *            the nueva pos
     */
    private void anotarReaparicionRelativa(final int id, final int x, final int y, final ObjetoMovil nuevaPos) {
        final Posicion claveNum = new Posicion(x, y);
        this.posicionIds.put(claveNum, nuevaPos);
        this.mapaIds.put(id, nuevaPos);

        final ObjetoMovil lastPosicion = this.ultimaPosicionDesaparecidos.get(id);
        final int diferenciaX = nuevaPos.getPosicionX() - lastPosicion.getPosicionX();
        final int diferenciaY = nuevaPos.getPosicionY() - lastPosicion.getPosicionY();
        if ((Math.abs(diferenciaX) > this.limiteLog) || (Math.abs(diferenciaY) > this.limiteLog)) {
            // El movimiento relativo no cabe en la espeiral

            final int numeroEspiral = FunctionUtils.unidimensionar(this.reaparicionFueraLimites.getX(),
                    this.reaparicionFueraLimites.getY());
            final int posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);
            final List<Integer> word = this.encoder.encode(posicionNumero);
            word.add(diferenciaX);
            word.add(diferenciaY);
            System.out.println("RF (" + this.reaparicionFueraLimites.getX() + ","
                    + this.reaparicionFueraLimites.getY() + ") ->" + numeroEspiral + " -> "
                    + posicionNumero + " -> " + word);
            this.mapaDeLog.put(id, word);
        } else {

            int numeroEspiral = FunctionUtils.unidimensionar(this.reaparicionRelativa.getX(),
                    this.reaparicionRelativa.getY());
            int posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);
            final List<Integer> word = this.encoder.encode(posicionNumero);

            // El movimiento relativo de la reaparicion cabe en la espiral
            final Movimiento movimiento = new Movimiento(diferenciaX, diferenciaY);
            numeroEspiral = FunctionUtils.unidimensionar(movimiento.getX(), movimiento.getY());
            posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);
            word.addAll(this.encoder.encode(posicionNumero));
            System.out.println("RR (" + this.reaparicionRelativa.getX() + ","
                    + this.reaparicionRelativa.getY() + ") ->" + numeroEspiral + " -> "
                    + posicionNumero + " -> " + word);
            this.mapaDeLog.put(id, word);
        }
        this.ultimaPosicionDesaparecidos.remove(id);
        this.desaparecidoRelativo.remove(id);
    }

    /**
     * Anotar movimiento.
     * 
     * @param id
     *            the id
     * @param claveNum
     *            the clave num
     * @param nuevaPos
     *            the nueva pos
     * @param viejaPos
     *            the vieja pos
     */
    private void anotarMovimiento(final int id, final Posicion claveNum, final ObjetoMovil nuevaPos,
            final ObjetoMovil viejaPos) {

        // Fuera de los limites fisicos admitidos, no se registra el movimiento.
        Movimiento movimiento = null;

        if (viejaPos != null) {
            final int diferenciaX = nuevaPos.getPosicionX() - viejaPos.getPosicionX();
            final int diferenciaY = nuevaPos.getPosicionY() - viejaPos.getPosicionY();
            if (FunctionUtils.sonDiferenciasDentroDeLimites(diferenciaX, diferenciaY, this.limiteLog)) {
                movimiento = new Movimiento(diferenciaX, diferenciaY);

                final int numeroEspiral = FunctionUtils.unidimensionar(movimiento.getX(), movimiento.getY());
                final int posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);

                final List<Integer> word = this.encoder.encode(posicionNumero);
                Collections.reverse(word);
                System.out.println("(" + movimiento.getX() + "," + movimiento.getY() + ") ->" + numeroEspiral + " -> "
                        + posicionNumero + " -> " + word);
                this.mapaDeLog.put(id, word);
            } else if (FunctionUtils.sonDiferenciasDentroDeLimites(diferenciaX, diferenciaY,
                    this.limiteLog * (nuevaPos.getInstante() - viejaPos.getInstante()))) {
                // Movimeinto válido fuera de espiral, lo anotamos como reaparicion fuera de limites para representarlo.

                final int numeroEspiral = FunctionUtils.unidimensionar(this.reaparicionFueraLimites.getX(),
                        this.reaparicionFueraLimites.getY());
                final int posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);
                final List<Integer> word = this.encoder.encode(posicionNumero);
                word.add(diferenciaX);
                word.add(diferenciaY);
                System.out.println("RF (" + this.reaparicionFueraLimites.getX() + ","
                        + this.reaparicionFueraLimites.getY() + ") ->" + numeroEspiral + " -> "
                        + posicionNumero + " -> " + word);
                this.mapaDeLog.put(id, word);
            }

        }
        this.posicionIds.put(claveNum, nuevaPos);
        this.mapaIds.put(id, nuevaPos);

    }

}

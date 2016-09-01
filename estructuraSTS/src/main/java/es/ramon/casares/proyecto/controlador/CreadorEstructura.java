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

import es.ramon.casares.proyecto.encoder.SCDenseCoder;
import es.ramon.casares.proyecto.modelo.log.Log;
import es.ramon.casares.proyecto.modelo.log.Movimiento;
import es.ramon.casares.proyecto.modelo.log.MovimientoComprimido;
import es.ramon.casares.proyecto.modelo.objetos.ObjetoMovil;
import es.ramon.casares.proyecto.modelo.objetos.PosicionKey;
import es.ramon.casares.proyecto.modelo.snapshot.Snapshot;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2TreeHelper;
import es.ramon.casares.proyecto.util.ByteFileHelper;
import es.ramon.casares.proyecto.util.CompresorEstructuraHelper;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;
import es.ramon.casares.proyecto.util.ControladorHelper;
import es.ramon.casares.proyecto.util.SolucionadorColisionesHelper.ImpossibleToSolveColisionException;

/**
 * The Class CreadorEstructura.
 */
public class CreadorEstructura {

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(CreadorEstructura.class);

    /** Las posiciones ocupadas en el momento actual del fichero. */
    private final HashMap<PosicionKey, ObjetoMovil> posicionIds = new HashMap<PosicionKey, ObjetoMovil>();

    /** The movimientos. */
    private final Map<Movimiento, Integer> movimientos = new HashMap<Movimiento, Integer>();

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
    private Movimiento reaparicion;

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

    private int puntero;

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
        this.reaparicion = new Movimiento(-this.limiteLog, -this.limiteLog);
        this.reaparicionAbsoluta = new Movimiento(this.limiteLog, -this.limiteLog);
        for (int i = -this.limiteLog; i <= this.limiteLog; i++) {
            for (int j = -this.limiteLog; j <= this.limiteLog; j++) {
                this.movimientos.put(new Movimiento(i, j), 0);
            }
        }

        this.datareader = new RandomAccessFile(ficheroFrecuencias, "r");
        String currentLine;
        while ((currentLine = this.datareader.readLine()) != null) {
            this.movimientosPorFrecuencia.add(Integer.valueOf(currentLine));
        }

        this.encoder = new SCDenseCoder(configuracion.getS(), configuracion.getC());

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
        final FileOutputStream tempFile = ByteFileHelper
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

                final PosicionKey claveNum = new PosicionKey(x, y);
                final ObjetoMovil nuevaPos = new ObjetoMovil(id, instant, x, y);

                if (this.mapaIds.containsKey(id)) {
                    // Si esta en el mapa hay que cambiar
                    // la posicion anotada
                    final ObjetoMovil viejaPos = this.mapaIds.get(id);
                    final PosicionKey viejaClaveNum = new PosicionKey(viejaPos.getPosicionX(), viejaPos.getPosicionY());
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

                    }

                    if (instant == 0) {
                        // Posicion libre
                        anotarMovimiento(id, claveNum, nuevaPos, null);
                    } else {
                        anotarReaparicionAbsoluta(id, x, y, nuevaPos);
                    }
                }

            }
        }
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

        final Set<PosicionKey> posiciones = new HashSet<PosicionKey>(this.posicionIds.keySet());
        for (final PosicionKey posicion : posiciones) {
            final ObjetoMovil objeto = this.posicionIds.get(posicion);
            if ((objeto.getInstante() + configuracion.getInstantesHastaDesaparicion()) <= instant) {
                // El objeto ha desaparecido

                this.ultimaPosicionDesaparecidos.put(objeto.getObjetoId(), objeto);
                this.posicionIds.remove(posicion);
                this.mapaIds.remove(objeto.getObjetoId());
                this.desaparecidoRelativo.add(objeto.getObjetoId());
                Integer num = this.movimientos.get(this.desaparicion);
                num++;
                this.movimientos.put(this.desaparicion, num);
                final int numeroEspiral = ControladorHelper.unidimensionar(this.desaparicion.getX(),
                        this.desaparicion.getY());
                final int posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);
                final List<Integer> word = this.encoder.encode(posicionNumero);
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
            for (int i = 1; i < this.numeroObjetos; i++) {
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
                        this.numeroBloques, configuracion.getS(), configuracion.getC(), this.puntero);
                logger.info("Snapshot: " + this.numeroBloques);
                this.numeroBloques++;
            }
            this.logs.clear();

            // Generamos Snapshot
            this.ultimaPosicionDesaparecidos.clear();
            this.desaparecidoRelativo.clear();
            // Punto de generacion de Snapshot
            final K2Tree k2Tree = K2TreeHelper.generarK2Tree(this.posicionIds,
                    ControladorHelper.numeroCuadradosSegunLimite(this.limiteSnapshot),
                    configuracion.getMinimumSquare());
            final byte[] bytes = K2TreeHelper.serializarK2Tree(k2Tree);
            final int tamanoBytes = K2TreeHelper.obtenerTamanoK2Tree(k2Tree);
            System.out.println("NumBytes : " + tamanoBytes);
            System.out.println("Real     : " + bytes.length);
            k2Tree.equals(k2Tree);
            this.snapshot = k2Tree;
            System.out.println("SNAPSHOT: " + (instant - 1));

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
        final PosicionKey claveNum = new PosicionKey(x, y);
        this.posicionIds.put(claveNum, nuevaPos);
        this.mapaIds.put(id, nuevaPos);
        Integer num = this.movimientos.get(this.reaparicionAbsoluta);
        num++;
        this.movimientos.put(this.reaparicionAbsoluta, num);
        final int numeroEspiral = ControladorHelper.unidimensionar(this.reaparicionAbsoluta.getX(),
                this.reaparicionAbsoluta.getY());
        final int posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);
        final List<Integer> word = this.encoder.encode(posicionNumero);
        word.add(x);
        word.add(y);
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
        final PosicionKey claveNum = new PosicionKey(x, y);
        this.posicionIds.put(claveNum, nuevaPos);
        this.mapaIds.put(id, nuevaPos);
        Integer num = this.movimientos.get(this.reaparicion);
        num++;
        this.movimientos.put(this.reaparicion, num);
        int numeroEspiral = ControladorHelper.unidimensionar(this.reaparicion.getX(), this.reaparicion.getY());
        int posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);
        final List<Integer> word = this.encoder.encode(posicionNumero);
        final ObjetoMovil lastPosicion = this.ultimaPosicionDesaparecidos.get(id);
        final int diferenciaX = nuevaPos.getPosicionX() - lastPosicion.getPosicionX();
        final int diferenciaY = nuevaPos.getPosicionY() - lastPosicion.getPosicionY();
        final Movimiento movimiento = new Movimiento(diferenciaX, diferenciaY);
        numeroEspiral = ControladorHelper.unidimensionar(movimiento.getX(), movimiento.getY());
        posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);
        word.addAll(this.encoder.encode(posicionNumero));
        this.mapaDeLog.put(id, word);

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
    private void anotarMovimiento(final int id, final PosicionKey claveNum, final ObjetoMovil nuevaPos,
            final ObjetoMovil viejaPos) {

        // Fuera de los limites fisicos admitidos, no se registra el movimiento.
        Movimiento movimiento = null;

        if (viejaPos != null) {
            final int diferenciaX = nuevaPos.getPosicionX() - viejaPos.getPosicionX();
            final int diferenciaY = nuevaPos.getPosicionY() - viejaPos.getPosicionY();
            if (movimientoDentroLimites(diferenciaX, diferenciaY)) {
                movimiento = new Movimiento(diferenciaX, diferenciaY);
                Integer num = this.movimientos.get(movimiento);
                num++;
                this.movimientos.put(movimiento, num);
                final int numeroEspiral = ControladorHelper.unidimensionar(movimiento.getX(), movimiento.getY());
                final int posicionNumero = this.movimientosPorFrecuencia.indexOf(numeroEspiral);

                final List<Integer> word = this.encoder.encode(posicionNumero);
                Collections.reverse(word);

                this.mapaDeLog.put(id, word);
            }

        }
        this.posicionIds.put(claveNum, nuevaPos);
        this.mapaIds.put(id, nuevaPos);

    }

    /**
     * Movimiento dentro limites.
     *
     * @param diferenciaX
     *            diferencia x
     * @param diferenciaY
     *            diferencia y
     * @return true, si termina correctamente
     */
    private boolean movimientoDentroLimites(final int diferenciaX, final int diferenciaY) {
        return (Math.abs(diferenciaX) <= this.limiteLog) && (Math.abs(diferenciaY) <= this.limiteLog)
                && ((Math.abs(diferenciaX) + Math.abs(diferenciaY)) <= ((this.limiteLog * 2) - 1));
    }
}

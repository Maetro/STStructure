/**
 * CreadorEstructura.java 25-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.controlador;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.Resource;

import es.ramon.casares.proyecto.encoder.SCDenseCoder;
import es.ramon.casares.proyecto.modelo.estructura.Estructura;
import es.ramon.casares.proyecto.modelo.log.Log;
import es.ramon.casares.proyecto.modelo.log.Movimiento;
import es.ramon.casares.proyecto.modelo.log.MovimientoComprimido;
import es.ramon.casares.proyecto.modelo.objetos.ObjetoMovil;
import es.ramon.casares.proyecto.modelo.objetos.PosicionKey;
import es.ramon.casares.proyecto.modelo.snapshot.Snapshot;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2TreeHelper;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;
import es.ramon.casares.proyecto.util.ControladorHelper;
import es.ramon.casares.proyecto.util.SolucionadorColisionesHelper.ImpossibleToSolveColisionException;

public class CreadorEstructura {

    // Las posiciones ocupadas en el momento actual del fichero
    private final HashMap<PosicionKey, ObjetoMovil> posicionIds = new HashMap<PosicionKey, ObjetoMovil>();

    private final Map<Movimiento, Integer> movimientos = new HashMap<Movimiento, Integer>();

    private final Set<Integer> desaparecidoRelativo = new HashSet<Integer>();

    // mapa que guarda el estado del objeto en el instante de la desaparicion
    private final Map<Integer, ObjetoMovil> ultimaPosicionDesaparecidos = new HashMap<Integer, ObjetoMovil>();

    private final Map<Integer, List<Integer>> mapaDeLog = new HashMap<Integer, List<Integer>>();

    private Integer limiteLog;
    private Integer limiteSnapshot;
    private Integer numeroObjetos;

    private RandomAccessFile datareader; // es

    private Movimiento desaparicion;
    private Movimiento reaparicion;
    private Movimiento reaparicionAbsoluta;

    private final List<Integer> movimientosPorFrecuencia = new ArrayList<Integer>();

    /** The mapa ids. */
    private final HashMap<Integer, ObjetoMovil> mapaIds = new HashMap<Integer, ObjetoMovil>();

    private final Map<Integer, Snapshot> snapshots = new HashMap<Integer, Snapshot>();

    private final Map<Integer, Log> logs = new HashMap<Integer, Log>();

    private SCDenseCoder encoder;

    public CreadorEstructura() {
    }

    /**
     * Instancia un nuevo creador fichero frecuencias.
     * 
     * @param limiteLog
     *            limite
     */
    public CreadorEstructura(final Integer limiteLog, final Integer limiteSnapshot, final Integer numeroObjetos) {
        this.limiteLog = limiteLog;
        this.limiteSnapshot = limiteSnapshot;
        this.numeroObjetos = numeroObjetos;
    }

    /**
     * Inicializar.
     * 
     * @param configuracion2
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void inicializar(final Resource ficheroFrecuencias, final ConfiguracionHelper configuracion)
            throws FileNotFoundException, IOException {

        this.desaparicion = new Movimiento(this.limiteLog, this.limiteLog);
        this.reaparicion = new Movimiento(-this.limiteLog, -this.limiteLog);
        this.reaparicionAbsoluta = new Movimiento(this.limiteLog, -this.limiteLog);
        for (int i = -this.limiteLog; i <= this.limiteLog; i++) {
            for (int j = -this.limiteLog; j <= this.limiteLog; j++) {
                this.movimientos.put(new Movimiento(i, j), 0);
            }
        }

        this.datareader = new RandomAccessFile(ficheroFrecuencias.getFile(), "r");
        String currentLine;
        while ((currentLine = this.datareader.readLine()) != null) {
            this.movimientosPorFrecuencia.add(Integer.valueOf(currentLine));
        }

        this.encoder = new SCDenseCoder(configuracion.getS(), configuracion.getC());

    }

    public Estructura crearEstructura(final Resource fichero, final ConfiguracionHelper configuracion)
            throws NumberFormatException, IOException, ImpossibleToSolveColisionException {
        String currentLine;

        System.out.println("Generando estructura");

        int lastInstant = 0;
        this.datareader = new RandomAccessFile(fichero.getFile(), "r");

        while ((currentLine = this.datareader.readLine()) != null) {
            final String[] result = currentLine.trim().split("\\s");
            final int instant = Integer.valueOf(result[0]); // En segundos
            final int id = Integer.valueOf(result[1]);
            final int x = Integer.valueOf(result[2]); // Longitud
            final int y = Integer.valueOf(result[3]); // Latitud

            // if (id == 4222) System.out.println(instant + " " + id + " " + x + " " + y);
            if (instant != lastInstant) {
                // Cambio de instante. Comprobamos si algun objeto ha
                // desaparecido
                if ((instant - lastInstant) > 1) {
                    // Hay instantes que no tienen ningun movimiento registrado
                    while (lastInstant < (instant - 1)) {
                        lastInstant++;
                        lastInstant = procesarCambioInstante(configuracion, lastInstant);

                    }

                }

                lastInstant = procesarCambioInstante(configuracion, instant);
            }

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

        final Estructura estructura = new Estructura(this.snapshots, this.logs);
        return estructura;
    }

    private int procesarCambioInstante(final ConfiguracionHelper configuracion, final int instant) {
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

        creacionSnapshotEnCambioInstante(configuracion, instant);
        return lastInstant;
    }

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
            this.logs.put(instant - 1, log);
            this.mapaDeLog.clear();
            System.out.println("LOG: " + (instant - 1));
        }

    }

    private void creacionSnapshotEnCambioInstante(final ConfiguracionHelper configuracion, final int instant) {
        if (((instant - 1) % configuracion.getDistanciaEntreSnapshots()) == 0) {
            // Generamos Snapshot

            this.ultimaPosicionDesaparecidos.clear();
            this.desaparecidoRelativo.clear();
            // Punto de generacion de Snapshot
            final K2Tree k2Tree = K2TreeHelper.generarK2Tree(this.posicionIds, this.limiteSnapshot,
                    configuracion.getMinimumSquare());
            final byte[] bytes = K2TreeHelper.serializarK2Tree(k2Tree);
            final int tamanoBytes = K2TreeHelper.obtenerTamanoK2Tree(k2Tree);
            System.out.println("NumBytes : " + tamanoBytes);
            System.out.println("Real     : " + bytes.length);
            k2Tree.equals(k2Tree);
            this.snapshots.put(instant - 1, k2Tree);
            System.out.println("SNAPSHOT: " + (instant - 1));
        }
    }

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
        if (nuevaPos.getInstante() == 1739) {
            System.out.println("A id: " + id + " word: " + word);
        }
        this.mapaDeLog.put(id, word);

    }

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
        if (nuevaPos.getInstante() == 1739) {
            System.out.println("R id: " + id + " word: " + word);
        }
        this.mapaDeLog.put(id, word);

    }

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
                if (this.encoder.decode(word) != posicionNumero) {
                    System.out.println("Problema " + posicionNumero);
                    this.encoder.encode(35);

                    this.encoder.decode(Arrays.asList(11, 0));
                }

                if (nuevaPos.getInstante() == 1739) {
                    System.out.println("id: " + id + " word: " + word);
                }
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

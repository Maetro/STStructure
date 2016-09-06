/**
 * MainController.java 26-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.controlador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.Test;
import es.ramon.casares.proyecto.controlador.helpers.CompresorEstructuraHelper;
import es.ramon.casares.proyecto.controlador.helpers.ConfiguracionHelper;
import es.ramon.casares.proyecto.controlador.limpieza.SolucionadorColisiones.ImpossibleToSolveColisionException;
import es.ramon.casares.proyecto.encoder.SCDenseCoder;
import es.ramon.casares.proyecto.modelo.estructura.Estructura;
import es.ramon.casares.proyecto.modelo.estructura.log.Log;
import es.ramon.casares.proyecto.modelo.estructura.log.Movimiento;
import es.ramon.casares.proyecto.modelo.estructura.log.MovimientoComprimido;
import es.ramon.casares.proyecto.modelo.estructura.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.util.K2TreeHelper;
import es.ramon.casares.proyecto.parametros.ComprimirEstructuraParametersBean;
import es.ramon.casares.proyecto.parametros.LimitesBean;
import es.ramon.casares.proyecto.util.FunctionUtils;
import es.ramon.casares.proyecto.util.objetos.ObjetoMovil;
import es.ramon.casares.proyecto.util.objetos.Posicion;
import es.ramon.casares.proyecto.util.objetos.Rectangulo;

/**
 * The Class MainController.
 */
@RestController
public class MainController { // NO_UCD (test only)

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(MainController.class);

    /** The Constant CICLOS_TEST. */
    private static final int CICLOS_TEST = 20000;

    /** The configuracion. */
    @Autowired
    private ConfiguracionHelper configuracion;

    @Autowired
    private PreparadorDatos preparadorDatos;

    /**
     * Crear estructura.
     * 
     * @return the string
     * @throws ClassNotFoundException
     *             the class not found exception
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws NumberFormatException
     *             the number format exception
     * @throws ImpossibleToSolveColisionException
     *             the impossible to solve colision exception
     */
    @RequestMapping("/")
    public final String crearEstructura() throws ClassNotFoundException, FileNotFoundException, // NO_UCD (unused code)
            IOException, NumberFormatException, ImpossibleToSolveColisionException {

        System.out.println(4 + "-" +
                this.configuracion.getDistanciaEntreSnapshots() + "-" +
                this.configuracion.getSegundosEntreInstantes() + "-" +
                this.configuracion.getMetrosPorCelda() + "-" +
                this.configuracion.getS() + "-" +
                this.configuracion.getC());

        // this.preparadorDatos.prepararDatosFichero();

        this.logger.info("Creando estructura");

        final int limiteSuperior = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
                * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));
        final File ficheroDataSet = new File("src/main/resources/testReap.txt");

        this.logger.info("Analizando límites");
        final LimitesBean limites = FunctionUtils.analizadorDeLimites(ficheroDataSet);

        this.logger.info("Creando Modelo estructura");
        final CreadorEstructura creador = new CreadorEstructura(limiteSuperior, limites.getLimiteCuadrado(),
                limites.getNumeroObjetos());

        final File ficheroFrecuencias = new File("src/main/resources/frecuencias");

        creador.inicializar(ficheroFrecuencias, this.configuracion);
        final List<Integer> punteros = creador.crearEstructura(ficheroDataSet, this.configuracion);

        final File ficheroCuerpo = new File("src/main/resources/EstructuraTemporal");

        final ComprimirEstructuraParametersBean parametros = new ComprimirEstructuraParametersBean();

        crearParametrosParaComprimir(limites, parametros);

        this.logger.info("Escribiendo estructura comprimida en fichero");

        CompresorEstructuraHelper.comprimirEstructura(ficheroFrecuencias, ficheroCuerpo, punteros,
                parametros);

        this.logger.info("Todo correcto, función terminada: S: " + this.configuracion.getS() + ", C: " +
                this.configuracion.getC());

        final File estructura = new File("src/main/resources/estructuracomprimida");

        System.out.println(4 + "-" +
                this.configuracion.getDistanciaEntreSnapshots() + "-" +
                this.configuracion.getSegundosEntreInstantes() + "-" +
                this.configuracion.getMetrosPorCelda() + "-" +
                this.configuracion.getS() + "-" +
                this.configuracion.getC() + " = " + estructura.length());

        System.out.println("megabytes : " + ((estructura.length() / 1024) / 1024));

        return "DONE";
    }

    /**
     * Crear parametros para comprimir.
     * 
     * @param limites
     *            the limites
     * @param parametros
     *            the parametros
     */
    private void crearParametrosParaComprimir(final LimitesBean limites,
            final ComprimirEstructuraParametersBean parametros) {

        final int limite = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
                * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));

        final int numeroEspiral = FunctionUtils.unidimensionar(limite,
                -limite);

        parametros.setSeparacionSnapshots(this.configuracion.getDistanciaEntreSnapshots());
        parametros.setNumeroObjetos(limites.getNumeroObjetos());
        parametros.setLimiteCuadrados(limites.getLimiteCuadrado());
        parametros.setParametroC(this.configuracion.getC());
        parametros.setParametroS(this.configuracion.getS());
        parametros.setSegundosPorInstante(this.configuracion.getSegundosEntreInstantes());
        parametros.setLimiteMovimiento(limite);
    }

    /**
     * Descomprimir estructura.
     * 
     * @return the string
     * @throws ClassNotFoundException
     *             the class not found exception
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws NumberFormatException
     *             the number format exception
     * @throws ImpossibleToSolveColisionException
     *             the impossible to solve colision exception
     */
    @RequestMapping("/descomprimir")
    public final String descomprimirEstructura() throws ClassNotFoundException, FileNotFoundException, // NO_UCD (unused
                                                                                                       // // Code) //
                                                                                                       // code)
            IOException, NumberFormatException, ImpossibleToSolveColisionException {

        final File ficheroEstructura = new File("src/main/resources/estructuracomprimida");

        final Estructura estructuraUtil = CompresorEstructuraHelper.descomprimirEstructura(ficheroEstructura, 30);

        K2TreeHelper.obtenerPosicionEnSnapshot((K2Tree) estructuraUtil.getSnapshots().get(0), 133,
                FunctionUtils.numeroCuadradosSegunLimite(estructuraUtil.getNumeroCuadrados()));

        return "DONE";
    }

    @RequestMapping("/resolver")
    public final String resolverConsultaObjetoInstante(@RequestParam(value = "instant") final int instant,
            @RequestParam(value = "idObjeto") final int idObjeto) throws IOException {

        final StopWatch clock = new StopWatch();
        clock.start();
        final File ficheroEstructura = new File("src/main/resources/estructuracomprimida");

        final Estructura estructuraUtil = CompresorEstructuraHelper.descomprimirEstructura(ficheroEstructura, instant);

        final SCDenseCoder encoder = new SCDenseCoder(estructuraUtil.getCabecera().getParametroS(),
                estructuraUtil.getCabecera().getParametroC());

        final Posicion pos = K2TreeHelper.obtenerPosicionEnSnapshot((K2Tree) estructuraUtil.getSnapshots().get(0),
                idObjeto, FunctionUtils.numeroCuadradosSegunLimite(estructuraUtil.getNumeroCuadrados()));

        for (final Log log : estructuraUtil.getLogs().values()) {

            final MovimientoComprimido movimientoComprimido = log.getObjetoMovimientoMap().get(idObjeto);
            if (movimientoComprimido != null) {
                final int posicion = encoder.decode(movimientoComprimido.getMovimiento());
                if (movimientoComprimido != null) {
                    final Integer posicionNumero = posicion;
                    if (posicionNumero.equals(estructuraUtil.getParametros().getPosicionReaparicionAbsoluta())) {
                        pos.setX(pos.getPosicionX() + movimientoComprimido.getMovimiento().get(
                                movimientoComprimido.getMovimiento().size() - 2));
                        pos.setY(pos.getPosicionY() + movimientoComprimido.getMovimiento().get(
                                movimientoComprimido.getMovimiento().size() - 1));
                    } else if (posicionNumero.equals(estructuraUtil.getParametros().getPosicionReaparicionRelativa())) {

                        final List<Integer> movimientoDoble = movimientoComprimido.getMovimiento();
                        final List<Integer> mov = FunctionUtils.obtenerMovimientoInterno(movimientoDoble,
                                estructuraUtil.getCabecera().getParametroS());
                        final Movimiento movimientoReap = FunctionUtils.obtenerMovimiento(estructuraUtil
                                .getMovimientosPorFrecuencia()
                                .get(encoder.decode(mov)));
                        pos.setX(pos.getPosicionX() + movimientoReap.getX());
                        pos.setY(pos.getPosicionY() + movimientoReap.getY());
                    } else if (posicionNumero
                            .equals(estructuraUtil.getParametros().getPosicionReaparicionFueraLimites())) {
                        pos.setX(pos.getPosicionX() + movimientoComprimido.getMovimiento().get(
                                movimientoComprimido.getMovimiento().size() - 2));
                        pos.setY(pos.getPosicionY() + movimientoComprimido.getMovimiento().get(
                                movimientoComprimido.getMovimiento().size() - 1));
                    } else if (posicionNumero
                            .equals(estructuraUtil.getParametros().getPosicionDesaparicion())) {
                        // Desaparecido
                    } else {

                        final Movimiento mov = FunctionUtils.obtenerMovimiento(estructuraUtil
                                .getMovimientosPorFrecuencia()
                                .get(posicion));
                        pos.setX(pos.getPosicionX() + mov.getX());
                        pos.setY(pos.getPosicionY() + mov.getY());

                    }
                }
            }
        }

        clock.stop();
        System.out.println(clock.toString());
        return "DONE";

    }

    @RequestMapping("/timeSlice")
    public final List<ObjetoMovil> resolverConsultatimeSlice(@RequestParam(value = "instant") final int instant,
            @RequestParam(value = "x1") final int x1, @RequestParam(value = "x2") final int x2,
            @RequestParam(value = "y1") final int y1, @RequestParam(value = "y2") final int y2) throws IOException {

        final StopWatch clock = new StopWatch();
        clock.start();
        final File ficheroEstructura = new File("src/main/resources/estructuracomprimida");

        final Rectangulo rectangulo = new Rectangulo(x1, y1, x2, y2);

        final Estructura estructuraUtil = CompresorEstructuraHelper.descomprimirEstructura(ficheroEstructura, instant);

        final SCDenseCoder encoder = new SCDenseCoder(estructuraUtil.getCabecera().getParametroS(),
                estructuraUtil.getCabecera().getParametroC());

        int rango = instant % estructuraUtil.getCabecera().getSeparacionSnapshots();

        // Devuelve los objetos candidatos a cumplir con la query
        List<ObjetoMovil> objetosCandidatos = K2TreeHelper.obtenerPosicionesEnRangoSnapshot(
                (K2Tree) estructuraUtil.getSnapshots().get(0), rango, rectangulo,
                FunctionUtils.numeroCuadradosSegunLimite(estructuraUtil.getNumeroCuadrados()));
        // Aplicamos los cambios de cada log y comprobamos si siguen siendo candidatos.
        final List<ObjetoMovil> nuevaListaCandidatos = new ArrayList<ObjetoMovil>();
        for (final Log log : estructuraUtil.getLogs().values()) {
            for (ObjetoMovil objetoMovil : objetosCandidatos) {
                final MovimientoComprimido movimientoComprimido = log.getObjetoMovimientoMap().get(
                        objetoMovil.getObjetoId());
                if (movimientoComprimido != null) {
                    final int posicion = encoder.decode(movimientoComprimido.getMovimiento());
                    if (movimientoComprimido != null) {
                        objetoMovil = modificarPosicionObjeto(estructuraUtil, encoder, objetoMovil,
                                movimientoComprimido,
                                posicion);
                    }
                }
                if (entraDentroDeRango(rango, rectangulo, objetoMovil)) {
                    nuevaListaCandidatos.add(objetoMovil);
                }
            }
            objetosCandidatos = new ArrayList(nuevaListaCandidatos);
            nuevaListaCandidatos.clear();
            rango--;
        }

        return objetosCandidatos;

    }

    @RequestMapping("/timeInterval")
    public final List<Integer> resolverConsultaTimeInterval(
            @RequestParam(value = "instantInicial") final int instantInicial,
            @RequestParam(value = "instantFinal") final int instantFinal, @RequestParam(value = "x1") final int x1,
            @RequestParam(value = "x2") final int x2, @RequestParam(value = "y1") final int y1,
            @RequestParam(value = "y2") final int y2) throws IOException {

        final HashSet<Integer> idsObjetosRespuesta = new HashSet<Integer>();
        for (int i = instantInicial; i <= instantFinal; i++) {
            final List<ObjetoMovil> objetosInstante = resolverConsultatimeSliceConExcluidos(i, x1, x2, y1, y2,
                    idsObjetosRespuesta);
            for (final ObjetoMovil objetoRespuesta : objetosInstante) {
                idsObjetosRespuesta.add(objetoRespuesta.getObjetoId());
            }
        }
        final ArrayList<Integer> respuesta = new ArrayList<Integer>();
        respuesta.addAll(idsObjetosRespuesta);
        return respuesta;
    }

    @RequestMapping("/trayectoria")
    public final List<Movimiento> resolverTrayectoria(
            @RequestParam(value = "instantInicial") final int instantInicial,
            @RequestParam(value = "instantFinal") final int instantFinal,
            @RequestParam(value = "idObjeto") final int idObjeto) throws IOException {
        final StopWatch clock = new StopWatch();
        clock.start();
        final File ficheroEstructura = new File("src/main/resources/estructuracomprimida");
        final List<Movimiento> movimientos = new ArrayList<Movimiento>();

        final Estructura estructuraUtil = CompresorEstructuraHelper.descomprimirEstructura(ficheroEstructura,
                instantInicial, instantFinal);

        final SCDenseCoder encoder = new SCDenseCoder(estructuraUtil.getCabecera().getParametroS(),
                estructuraUtil.getCabecera().getParametroC());

        for (final Log log : estructuraUtil.getLogs().values()) {
            Movimiento mov = null;
            final MovimientoComprimido movimientoComprimido = log.getObjetoMovimientoMap().get(idObjeto);
            if (movimientoComprimido != null) {
                final int posicion = encoder.decode(movimientoComprimido.getMovimiento());
                if (movimientoComprimido != null) {
                    final Integer posicionNumero = posicion;
                    if (posicionNumero.equals(estructuraUtil.getParametros().getPosicionReaparicionAbsoluta())) {

                        final int x = movimientoComprimido.getMovimiento().get(
                                movimientoComprimido.getMovimiento().size() - 2);
                        final int y = movimientoComprimido.getMovimiento().get(
                                movimientoComprimido.getMovimiento().size() - 1);
                        mov = new Movimiento(x, y);
                        movimientos.add(mov);

                    } else if (posicionNumero.equals(estructuraUtil.getParametros().getPosicionReaparicionRelativa())) {

                        final List<Integer> movimientoDoble = movimientoComprimido.getMovimiento();
                        final List<Integer> movCompr = FunctionUtils.obtenerMovimientoInterno(movimientoDoble,
                                estructuraUtil.getCabecera().getParametroS());
                        final Movimiento movimientoReap = FunctionUtils.obtenerMovimiento(estructuraUtil
                                .getMovimientosPorFrecuencia()
                                .get(encoder.decode(movCompr)));

                        movimientos.add(movimientoReap);
                    } else if (posicionNumero
                            .equals(estructuraUtil.getParametros().getPosicionReaparicionFueraLimites())) {
                        final int x = movimientoComprimido.getMovimiento().get(
                                movimientoComprimido.getMovimiento().size() - 2);
                        final int y = movimientoComprimido.getMovimiento().get(
                                movimientoComprimido.getMovimiento().size() - 1);
                        mov = new Movimiento(x, y);
                        movimientos.add(mov);
                    } else if (posicionNumero
                            .equals(estructuraUtil.getParametros().getPosicionDesaparicion())) {
                        // Desaparecido
                        movimientos.add(new Movimiento(0, 0));
                    } else {

                        mov = FunctionUtils.obtenerMovimiento(estructuraUtil
                                .getMovimientosPorFrecuencia()
                                .get(posicion));
                        movimientos.add(mov);

                    }
                }
            }
        }

        clock.stop();

        return movimientos;
    }

    private List<ObjetoMovil> resolverConsultatimeSliceConExcluidos(final int i, final int x1, final int x2,
            final int y1, final int y2,
            final HashSet<Integer> idsObjetosRespuesta) {
        // TODO Auto-generated method stub
        return null;
    }

    private boolean entraDentroDeRango(final int rango, final Rectangulo rectangulo, final ObjetoMovil objetoMovil) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Modificar posicion objeto.
     * 
     * @param estructuraUtil
     *            estructura util
     * @param encoder
     *            encoder
     * @param objetoMovil
     *            objeto movil
     * @param movimientoComprimido
     *            movimiento comprimido
     * @param posicion
     *            posicion
     */
    private ObjetoMovil modificarPosicionObjeto(final Estructura estructuraUtil, final SCDenseCoder encoder,
            final ObjetoMovil objetoMovil, final MovimientoComprimido movimientoComprimido, final Integer posicionNumero) {
        if (posicionNumero.equals(estructuraUtil.getParametros().getPosicionReaparicionAbsoluta())) {
            objetoMovil.setX(objetoMovil.getPosicionX() + movimientoComprimido.getMovimiento().get(
                    movimientoComprimido.getMovimiento().size() - 2));
            objetoMovil.setY(objetoMovil.getPosicionY() + movimientoComprimido.getMovimiento().get(
                    movimientoComprimido.getMovimiento().size() - 1));
        } else if (posicionNumero.equals(estructuraUtil.getParametros()
                .getPosicionReaparicionRelativa())) {
            final List<Integer> movimientoDoble = movimientoComprimido.getMovimiento();
            final List<Integer> mov = FunctionUtils.obtenerMovimientoInterno(movimientoDoble,
                    estructuraUtil.getCabecera().getParametroS());
            final Movimiento movimientoReap = FunctionUtils.obtenerMovimiento(estructuraUtil
                    .getMovimientosPorFrecuencia()
                    .get(encoder.decode(mov)));
            objetoMovil.setX(objetoMovil.getPosicionX() + movimientoReap.getX());
            objetoMovil.setY(objetoMovil.getPosicionY() + movimientoReap.getY());
        } else if (posicionNumero
                .equals(estructuraUtil.getParametros().getPosicionReaparicionFueraLimites())) {
            objetoMovil.setX(objetoMovil.getPosicionX() + movimientoComprimido.getMovimiento().get(
                    movimientoComprimido.getMovimiento().size() - 2));
            objetoMovil.setY(objetoMovil.getPosicionY() + movimientoComprimido.getMovimiento().get(
                    movimientoComprimido.getMovimiento().size() - 1));
        } else if (posicionNumero
                .equals(estructuraUtil.getParametros().getPosicionDesaparicion())) {
            // "DESAPARECIDO"
        } else {

            final Movimiento mov = FunctionUtils.obtenerMovimiento(estructuraUtil
                    .getMovimientosPorFrecuencia()
                    .get(posicionNumero));
            objetoMovil.setX(objetoMovil.getPosicionX() + mov.getX());
            objetoMovil.setY(objetoMovil.getPosicionY() + mov.getY());

        }
        return objetoMovil;
    }

    /**
     * Test.
     * 
     * @return the string
     */
    @RequestMapping("/test")
    public final String test() {

        final Test tests = new Test();
        final File frecuencias = new File("src/main/resources/frecuenciasTest");

        // tests.probarGeneracionK2TreeyLogs(this.configuracion, frecuencias);

        tests.probarGeneracionK2TreeyBusqueda(this.configuracion);
        // Test.probarLocalizacionObjetosEnK2Tree();

        return "DONE";
    }
}

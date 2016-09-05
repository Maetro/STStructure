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
import es.ramon.casares.proyecto.util.objetos.Posicion;

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

        this.preparadorDatos.prepararDatosFichero();

        this.logger.info("Creando estructura");

        final int limiteSuperior = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
                * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));
        final File ficheroDataSet = new File("src/main/resources/datafileSinColisiones");

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
    public final String resolverConsultaTimeSlice(@RequestParam(value = "instant") final int instant,
            @RequestParam(value = "idObjeto") final int idObjeto) throws IOException {

        final StopWatch clock = new StopWatch();
        clock.start();
        final File ficheroEstructura = new File("src/main/resources/estructuracomprimida");

        final Estructura estructuraUtil = CompresorEstructuraHelper.descomprimirEstructura(ficheroEstructura, instant);

        final SCDenseCoder encoder = new SCDenseCoder(estructuraUtil.getCabecera().getParametroS(),
                estructuraUtil.getCabecera().getParametroC());

        final Posicion pos = K2TreeHelper.obtenerPosicionEnSnapshot((K2Tree) estructuraUtil.getSnapshots().get(0),
                idObjeto, FunctionUtils.numeroCuadradosSegunLimite(estructuraUtil.getNumeroCuadrados()));

        System.out.println(instant + " " + idObjeto + " " + pos.getPosicionX() + " " + pos.getPosicionY());
        int lognumber = 1;
        for (final Log log : estructuraUtil.getLogs().values()) {

            final MovimientoComprimido movimientoComprimido = log.getObjetoMovimientoMap().get(idObjeto);
            if (movimientoComprimido != null) {
                final int movAntesEspiral = estructuraUtil.getMovimientosPorFrecuencia()
                        .get(encoder.decode(movimientoComprimido.getMovimiento()));
                if (movimientoComprimido != null) {
                    final Integer posicionNumero = movAntesEspiral;
                    if (posicionNumero.equals(estructuraUtil.getParametros().getPosicionReaparicionAbsoluta())) {
                        System.out.println("R A");
                    } else if (posicionNumero.equals(estructuraUtil.getParametros().getPosicionReaparicionRelativa())) {
                        System.out.println("R R");
                    } else if (posicionNumero
                            .equals(estructuraUtil.getParametros().getPosicionReaparicionFueraLimites())) {
                        System.out.println("R F");
                    } else {

                        final Movimiento mov = FunctionUtils.obtenerMovimiento(movAntesEspiral);
                        pos.setX(pos.getPosicionX() + mov.getX());
                        pos.setY(pos.getPosicionY() + mov.getY());

                    }
                }
            }
            System.out.println(instant + lognumber + " " + idObjeto + " " + pos.getPosicionX() + " " + pos.getPosicionY());
            lognumber++;
        }
        System.out.println(pos);

        clock.stop();
        System.out.println(clock.toString());
        return "DONE";

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

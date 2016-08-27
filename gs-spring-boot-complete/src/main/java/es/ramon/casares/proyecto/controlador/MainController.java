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
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.encoder.SCDenseCoder;
import es.ramon.casares.proyecto.modelo.estructura.Estructura;
import es.ramon.casares.proyecto.modelo.parametros.ComprimirEstructuraParametersBean;
import es.ramon.casares.proyecto.modelo.parametros.LimitesBean;
import es.ramon.casares.proyecto.util.CompresorEstructuraHelper;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;
import es.ramon.casares.proyecto.util.ControladorHelper;
import es.ramon.casares.proyecto.util.SolucionadorColisionesHelper.ImpossibleToSolveColisionException;

/**
 * The Class MainController.
 */
@RestController
public class MainController { // NO_UCD (test only)

    /** The Constant CICLOS_TEST. */
    private static final int CICLOS_TEST = 20000;

    /** The configuracion. */
    @Autowired
    private ConfiguracionHelper configuracion;

    /** The resource loader. */
    @Autowired
    private ResourceLoader resourceLoader;

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

        final int limiteSuperior = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
                * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));

        final Resource ficheroDataSet = this.resourceLoader.getResource("classpath:datafileSinColisiones");

        final LimitesBean limites = ControladorHelper.analizadorDeLimites(ficheroDataSet.getFile());

        final CreadorEstructura creador = new CreadorEstructura(limiteSuperior, limites.getLimiteMovimiento(),
                limites.getNumeroObjetos());

        final Resource ficheroFrecuencias = this.resourceLoader.getResource("classpath:frecuencias");

        creador.inicializar(ficheroFrecuencias, this.configuracion);
        final Estructura estructura = creador.crearEstructura(ficheroDataSet, this.configuracion);

        final ComprimirEstructuraParametersBean parametros = new ComprimirEstructuraParametersBean();

        final int numeroEspiral = ControladorHelper.unidimensionar(limites.getLimiteMovimiento(),
                -limites.getLimiteMovimiento());

        parametros.setSeparacionSnapshots(this.configuracion.getDistanciaEntreSnapshots());
        parametros.setNumeroObjetos(limites.getNumeroObjetos());
        parametros.setParametroC(this.configuracion.getC());
        parametros.setParametroS(this.configuracion.getS());
        parametros.setSegundosPorInstante(this.configuracion.getSegundosEntreInstantes());
        parametros.setCodigoReaparicionAbsoluta(numeroEspiral);

        final byte[] estructuraComprimida = CompresorEstructuraHelper.comprimirEstructura(estructura, parametros);

        FileUtils.writeByteArrayToFile(new File("src/main/resources/estructuracomprimida"), estructuraComprimida);

        return "DONE";
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
                                                                                                       // code)
            IOException, NumberFormatException, ImpossibleToSolveColisionException {

        final Resource ficheroEstructura = this.resourceLoader.getResource("classpath:estructuraComprimida");

        final byte[] estructuraComprimida = FileUtils.readFileToByteArray(ficheroEstructura.getFile());

        CompresorEstructuraHelper.descomprimirEstructura(estructuraComprimida, this.configuracion.getS(),
                this.configuracion.getC(), this.configuracion.getChunkSize());

        return "DONE";
    }

    /**
     * Test.
     *
     * @return the string
     */
    @RequestMapping("/test")
    public final String test() {

        final SCDenseCoder encoder = new SCDenseCoder(this.configuracion.getS(), this.configuracion.getC());

        for (int i = 0; i < CICLOS_TEST; i++) {
            final List<Integer> list = encoder.encode(i);
            Collections.reverse(list);
            System.out.println("(" + i + ")" + list + " - " + encoder.decode(list));

            if (i != encoder.decode(list)) {
                System.out.println("FALLO");
            }
        }

        return "DONE";
    }
}

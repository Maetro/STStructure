package es.ramon.casares.proyecto.controlador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.modelo.estructura.Estructura;
import es.ramon.casares.proyecto.modelo.limite.LimitesBean;
import es.ramon.casares.proyecto.util.CompresorEstructuraHelper;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;
import es.ramon.casares.proyecto.util.ControladorHelper;
import es.ramon.casares.proyecto.util.SolucionadorColisionesHelper.ImpossibleToSolveColisionException;

@RestController
public class MainController {

    @Autowired
    private ConfiguracionHelper configuracion;

    @Autowired
    private K2TreeController k2TreeController;

    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping("/")
    public String crearEstructura() throws ClassNotFoundException, FileNotFoundException,
            IOException, NumberFormatException, ImpossibleToSolveColisionException {

        final int limiteSuperior = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
                * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));

        final Resource ficheroDataSet =
                this.resourceLoader.getResource("classpath:datafileSinColisiones");

        final LimitesBean limites = ControladorHelper.analizadorDeLimites(ficheroDataSet.getFile());

        final CreadorEstructura creador = new CreadorEstructura(limiteSuperior, limites.getLimite(),
                limites.getIdObjeto());

        final Resource ficheroFrecuencias =
                this.resourceLoader.getResource("classpath:frecuencias");

        creador.inicializar(ficheroFrecuencias, this.configuracion);
        final Estructura estructura = creador.crearEstructura(ficheroDataSet, this.configuracion);

        final byte[] estructuraComprimida = CompresorEstructuraHelper.comprimirEstructura(estructura,
                this.configuracion.getDistanciaEntreSnapshots(), limites.getIdObjeto());

        FileUtils.writeByteArrayToFile(new File("src/main/resources/estructuracomprimida"), estructuraComprimida);

        return "DONE";
    }

    @RequestMapping("/descomprimir")
    public String descomprimirEstructura() throws ClassNotFoundException, FileNotFoundException,
            IOException, NumberFormatException, ImpossibleToSolveColisionException {

        final Resource ficheroEstructura =
                this.resourceLoader.getResource("classpath:estructuraComprimida");

        final byte[] estructuraComprimida = FileUtils.readFileToByteArray(ficheroEstructura.getFile());

        CompresorEstructuraHelper.descomprimirEstructura(estructuraComprimida);

        return "DONE";
    }

}

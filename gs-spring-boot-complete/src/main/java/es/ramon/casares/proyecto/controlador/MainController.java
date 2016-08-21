package es.ramon.casares.proyecto.controlador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.modelo.limite.LimitesBean;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;
import es.ramon.casares.proyecto.util.ControladorHelper;
import es.ramon.casares.proyecto.util.Normalizador;
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

        int limiteSuperior = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
        * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));
    	
        final Resource ficheroDataSet =
                this.resourceLoader.getResource("classpath:datafileSinColisiones");
        
        LimitesBean limites = analizadorDeLimites(ficheroDataSet.getFile());
        
    	CreadorEstructura creador = new CreadorEstructura(limiteSuperior, limites.getLimite(), limites.getIdObjeto());

        final Resource ficheroFrecuencias =
                this.resourceLoader.getResource("classpath:frecuencias");
        
        creador.inicializar(ficheroFrecuencias,configuracion);
        creador.crearEstructura(ficheroDataSet, configuracion);
        return "DONE";
    }
    
    private LimitesBean analizadorDeLimites(File dataSet) throws IOException {
    	int limite = 0;
    	int idObjeto = 0;
    	RandomAccessFile datareader = new RandomAccessFile(dataSet, "r");
    	String currentLine;
        while ((currentLine = datareader.readLine()) != null) {
        	 final String[] result = currentLine.trim().split("\\s");
        	 final int id = Integer.valueOf(result[1]);
             final int x = Integer.valueOf(result[2]); // Longitud
             final int y = Integer.valueOf(result[3]); // Latitud
             if (id > idObjeto){
            	 idObjeto = id;
             }
             if (x > limite){
            	 limite = x;
             }
             if (y > limite){
            	 limite = y;
             }
        }
        LimitesBean limites = new LimitesBean(limite, idObjeto);
        return limites;

    }

}

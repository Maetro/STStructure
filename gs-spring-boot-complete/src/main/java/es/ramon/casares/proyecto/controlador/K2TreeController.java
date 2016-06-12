package es.ramon.casares.proyecto.controlador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.modelo.k2tree.K2TreeHelper;
import es.ramon.casares.proyecto.modelo.matrix.InformacionInstanteObjeto;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;

@RestController
public class K2TreeController {

	@Autowired
    private ConfiguracionHelper configuracion;
	
    @Autowired
    private ResourceLoader resourceLoader;
	
    @RequestMapping("/crearK2Tree")
    public String index() {
    	Resource resource = resourceLoader.getResource("classpath:datosPlanos.txt");

        // Inicializamos el lector del fichero
    	InputStream is;
		try {
			is = resource.getInputStream();
		
	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        Integer instantesEntreSnapshots = configuracion.getDistanciaEntreSnapshots();
	        Integer limites = configuracion.getLimites();
	        Integer minimumSquare = configuracion.getMinimumSquare();
	        String line;
	        List<InformacionInstanteObjeto> listaInfo = new ArrayList<InformacionInstanteObjeto>();
	        Integer instanteAnterior = 0;
	        while ((line = br.readLine()) != null) {
	           System.out.println(line);
	           String[] elementos = line.split(" ");
	           Integer instanteActual = Integer.valueOf(elementos[0]);
	           if (!instanteActual.equals(instanteAnterior)){
	        	   //Cambio de instante
	        	   if ((instanteAnterior % instantesEntreSnapshots) == 0){
	        		   // Punto de generacion de Snapshot
	        		   K2TreeHelper.generarK2Tree(listaInfo, limites, minimumSquare);
	        	   }
	           }
	           
	           InformacionInstanteObjeto info = crearInfoPosicionDesdeLinea(elementos);
	           listaInfo.add(info);	           
	     	  } 
	        
		} catch (IOException e) {
			throw new InternalError(e);
		}
    	String probando = "probando";
    	String pantalla = "using env:" + configuracion.getEnv() + System.getProperty("line.separator")
    			.concat("name:" + configuracion.getName() + System.getProperty("line.separator"))
    			.concat("servers:" + configuracion.getServers() + System.getProperty("line.separator"))
    			.concat("numbers:" + configuracion.getNumbers() + System.getProperty("line.separator"))
    			.concat("Greetings from Spring Boot!").concat(probando);
        return pantalla;
    }

	/**
	 * Crear info posicion desde linea.
	 *
	 * @param elementos the elementos
	 * @return the informacion instante objeto
	 */
	private InformacionInstanteObjeto crearInfoPosicionDesdeLinea(String[] elementos) {
		InformacionInstanteObjeto info = new InformacionInstanteObjeto();
		   info.setInstante(Integer.valueOf(elementos[0]));
		   info.setObjetoId(Integer.valueOf(elementos[1]));
		   info.setPosicionX(Integer.valueOf(elementos[2]));
		   info.setPosicionY(Integer.valueOf(elementos[3]));
		return info;
	}
}

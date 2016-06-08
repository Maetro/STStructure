package es.ramon.casares.proyecto.controlador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	      	
	        String line;
	        while ((line = br.readLine()) != null) {
	           System.out.println(line);
	           String[] elementos = line.split(" ");
	           elemento
	     	  } 
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
}

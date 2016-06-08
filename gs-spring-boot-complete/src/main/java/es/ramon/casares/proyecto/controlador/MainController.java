package es.ramon.casares.proyecto.controlador;

import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.util.ConfiguracionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class MainController {
    
	@Autowired
    private ConfiguracionHelper configuracion;
	
	@Autowired
    private K2TreeController k2TreeController;
	
    @RequestMapping("/")
    public String index() {
    	
    	k2TreeController.index();
    	String probando = "probando";
    	String pantalla = "using env:" + configuracion.getEnv() + System.getProperty("line.separator")
    			.concat("name:" + configuracion.getName() + System.getProperty("line.separator"))
    			.concat("servers:" + configuracion.getServers() + System.getProperty("line.separator"))
    			.concat("numbers:" + configuracion.getNumbers() + System.getProperty("line.separator"))
    			.concat("Greetings from Spring Boot!").concat(probando);
        return pantalla;
    }
    
}

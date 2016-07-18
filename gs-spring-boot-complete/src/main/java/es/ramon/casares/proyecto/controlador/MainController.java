package es.ramon.casares.proyecto.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.util.ConfiguracionHelper;

@RestController
public class MainController {

    @Autowired
    private ConfiguracionHelper configuracion;

    @Autowired
    private K2TreeController k2TreeController;

    @RequestMapping("/")
    public String index() throws ClassNotFoundException {

        this.k2TreeController.index();
        final String probando = "probando";
        final String pantalla = "using env:" + this.configuracion.getEnv() + System.getProperty("line.separator")
                .concat("name:" + this.configuracion.getName() + System.getProperty("line.separator"))
                .concat("servers:" + this.configuracion.getServers() + System.getProperty("line.separator"))
                .concat("numbers:" + this.configuracion.getNumbers() + System.getProperty("line.separator"))
                .concat("Greetings from Spring Boot!").concat(probando);
        return pantalla;
    }

}

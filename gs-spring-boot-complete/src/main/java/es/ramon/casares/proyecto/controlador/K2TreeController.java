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

import es.ramon.casares.proyecto.modelo.matrix.InformacionInstanteObjeto;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2TreeHelper;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;

@RestController
public class K2TreeController {

    @Autowired
    private ConfiguracionHelper configuracion;

    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping("/crearK2Tree")
    public String index() throws ClassNotFoundException {
        final Resource resource = this.resourceLoader.getResource("classpath:datosPlanos.txt");

        // Inicializamos el lector del fichero
        InputStream is;
        try {
            is = resource.getInputStream();

            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            final Integer instantesEntreSnapshots = this.configuracion.getDistanciaEntreSnapshots();
            final Integer limites = this.configuracion.getLimites();
            final Integer minimumSquare = this.configuracion.getMinimumSquare();
            String line;
            final List<InformacionInstanteObjeto> listaInfo = new ArrayList<InformacionInstanteObjeto>();
            final List<K2Tree> snapshots = new ArrayList<K2Tree>();
            final Integer instanteAnterior = 0;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                final String[] elementos = line.split(" ");
                final Integer instanteActual = Integer.valueOf(elementos[0]);
                if (!instanteActual.equals(instanteAnterior)) {
                    // Cambio de instante
                    if ((instanteAnterior % instantesEntreSnapshots) == 0) {
                        // Punto de generacion de Snapshot
                        final K2Tree k2Tree = K2TreeHelper.generarK2Tree(listaInfo, limites, minimumSquare);
                        final byte[] bytes = K2TreeHelper.serializarK2Tree(k2Tree);
                        final int tamanoBytes = K2TreeHelper.obtenerTamanoK2Tree(k2Tree);
                        System.out.println("NumBytes: " + tamanoBytes);

                        k2Tree.equals(k2Tree);
                    }
                }

                final InformacionInstanteObjeto info = crearInfoPosicionDesdeLinea(elementos);
                listaInfo.add(info);
            }

        } catch (final IOException e) {
            throw new InternalError(e);
        }
        final String probando = "probando";
        final String pantalla = "using env:" + this.configuracion.getEnv() + System.getProperty("line.separator")
                .concat("name:" + this.configuracion.getName() + System.getProperty("line.separator"))
                .concat("servers:" + this.configuracion.getServers() + System.getProperty("line.separator"))
                .concat("numbers:" + this.configuracion.getNumbers() + System.getProperty("line.separator"))
                .concat("Greetings from Spring Boot!").concat(probando);
        return pantalla;
    }

    /**
     * Crear info posicion desde linea.
     * 
     * @param elementos
     *            the elementos
     * @return the informacion instante objeto
     */
    private InformacionInstanteObjeto crearInfoPosicionDesdeLinea(final String[] elementos) {
        final InformacionInstanteObjeto info = new InformacionInstanteObjeto();
        info.setInstante(Integer.valueOf(elementos[0]));
        info.setObjetoId(Integer.valueOf(elementos[1]));
        info.setPosicionX(Integer.valueOf(elementos[2]));
        info.setPosicionY(Integer.valueOf(elementos[3]));
        return info;
    }
}

/**
 * PreparadorDatos.java 01-ago-2016
 *
 */
package es.ramon.casares.proyecto.controlador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.modelo.objetos.ObjectDataConGaps;
import es.ramon.casares.proyecto.modelo.objetos.ObjectInformation;
import es.ramon.casares.proyecto.modelo.objetos.Point;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;
import es.ramon.casares.proyecto.util.ControladorHelper;
import es.ramon.casares.proyecto.util.CreadorFicheroFrecuencias;
import es.ramon.casares.proyecto.util.Normalizador;
import es.ramon.casares.proyecto.util.SolucionadorColisionesHelper;
import es.ramon.casares.proyecto.util.SolucionadorColisionesHelper.ImpossibleToSolveColisionException;
import es.ramon.casares.proyecto.util.SolucionadorRepetidosHelper;
import es.ramon.casares.proyecto.util.parser.LineaEntradaParseada;
import es.ramon.casares.proyecto.util.parser.ParseadorFicherosEntrada;
import es.ramon.casares.proyecto.util.parser.impl.ParseadorFicherosEntradaImis;

@RestController
public class PreparadorDatos {

    @Autowired
    private ConfiguracionHelper configuracion;

    @Autowired
    private ResourceLoader resourceLoader;

    private static final int MAXIMO_DESPLAZAMIENTO = 113;
    private static final String FILE_DATA = "./imis4hours.norm2500m2";
    private static final int TAMANO_CELDAS = 1;
    private int limiteSuperior;
    private int numeroObjetos;
    private static final boolean FILTRADO = true;
    private static final int ID_FILTRADO = 321;

    private static int numObjetosDesconocidos = 0;
    private static int momentos = 0;

    private static RandomAccessFile datareader;
    private static BufferedWriter mainWriter;
    private static BufferedWriter statiticswriter;

    private final HashMap<Integer, ObjectDataConGaps> mapaIds = new HashMap<Integer, ObjectDataConGaps>();
    private final HashMap<Integer, ObjectInformation> mapInformationIds = new HashMap<Integer, ObjectInformation>();
    private final HashMap<Integer, String> mapIdsDesaparecidos = new HashMap<Integer, String>();
    private final HashMap<String, Integer> mapFrecuency = new HashMap<String, Integer>();

    private final HashMap<Integer, Point> lastKnownPosition = new HashMap<Integer, Point>();
    private final HashMap<Integer, Point> puntoDesaparicion = new HashMap<Integer, Point>();

    private final List<Integer> descartados = new ArrayList<Integer>();

    private static int lastwriten;

    private Normalizador normalizador;

    public PreparadorDatos() {

    }

    public void run() {

        try {
            datareader = new RandomAccessFile(FILE_DATA, "r");
            inicializarPosiciones();
            encode(230, 26, 59489);
            unidimensionar("21:-26");
            fillHashMap();
            escribirResultados();
            datareader.close();
            mainWriter.close();
            statiticswriter.close();

        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void encode(final int s, final int c, final int word) {
        System.out.println(word % s);
        int x = (int) Math.floor(word / s);
        while (x > 0) {
            x--;
            System.out.println((x % c) + s);
            x = (int) Math.floor(x / c);
        }

    }

    // Todos los objetos empiezan en posicion desconocida. Cuando desconocemos
    // su posicion ocupa el espacio 0,0 de la
    // matriz
    private void inicializarPosiciones() throws IOException {
        final Point newPoint = new Point(0, -1, -1, this.configuracion.getMetrosPorCelda());
        final ObjectDataConGaps data = new ObjectDataConGaps();
        data.setPoint(newPoint);
        final Resource ficheroEscritura = this.resourceLoader.getResource("classpath:normalizado");
        final Resource ficheroEstadistica = this.resourceLoader.getResource("classpath:estadistica");
        mainWriter = new BufferedWriter(new FileWriter(ficheroEscritura.getFile()));
        statiticswriter = new BufferedWriter(new FileWriter(ficheroEstadistica.getFile()));
        for (int i = 1; i <= this.numeroObjetos; i++) {
            data.setObjectId(i);
            this.mapaIds.put(i, data);
            this.mapInformationIds.put(i, new ObjectInformation(0d, 0d));
            this.mapIdsDesaparecidos.put(i, "-1:-1");
            this.lastKnownPosition.put(i, new Point(0, -1, -1, this.configuracion.getMetrosPorCelda())); // Desconocido
                                                                                                         // es
                                                                                                         // -1:-1
        }

    }

    private void fillHashMap() throws IOException {
        String currentLine;
        int lastInstant = 0;
        int linea = 0;
        boolean firstSnapshot = false;
        final File tempFile = new File("./datafileLimpio");
        final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        while ((currentLine = datareader.readLine()) != null) {
            // Rellenamos el hashMap
            linea++;

            final String[] result = currentLine.trim().split("\\s");
            // System.out.println(Integer.valueOf(result[0]) + "->" +
            // Integer.valueOf(result[0]) / FREQ_MUESTREO);

            final int instant = (int) Math
                    .floor((Integer.valueOf(result[0]) - 1) / this.configuracion.getSegundosEntreInstantes()); // En
                                                                                                               // segundos
            final int id = Integer.valueOf(result[1]);
            if (!this.descartados.contains(id)) {
                if ((FILTRADO && (ID_FILTRADO == id)) || !FILTRADO) {
                    final int y = Integer.valueOf(result[2]); // Latitud
                    final int x = Integer.valueOf(result[3]); // Longitud

                    if (instant != lastInstant) { // Cambiamos de instante
                        for (int i = lastInstant + 1; i < instant; i++) {

                            visitarObjetos(i - 1);
                            escribirPosiciones(i);

                            // Acabamos de registrar todos los objetos de ese
                            // instante
                        }
                        visitarObjetos(instant);
                        escribirPosiciones(instant);
                        lastInstant = instant;

                    }
                    // if (FILTRADO) {
                    // System.out.println("---- "+ instant + " " + id + " " + y
                    // + " " + x);
                    // }
                    final Point newPoint = new Point(instant, y, x, this.configuracion.getMetrosPorCelda());
                    final ObjectDataConGaps data = this.mapaIds.get(id);
                    final Point oldPoint = data.getPoint();
                    if (!firstSnapshot && (Integer.valueOf(result[0]) != 0)) {
                        escribirPosiciones(0);
                        firstSnapshot = true;
                    }

                    final int tramitable = compararPuntos(oldPoint, newPoint, id);
                    if (tramitable == 1) {
                        final ObjectDataConGaps newdata = new ObjectDataConGaps();
                        newdata.setObjectId(id);
                        newdata.setPoint(newPoint);
                        this.mapaIds.put(id, newdata);
                    } else if (tramitable == 2) {
                        final ObjectDataConGaps newdata = new ObjectDataConGaps();
                        newdata.setObjectId(id);
                        newdata.setPoint(oldPoint);
                        this.mapaIds.put(id, newdata);
                    }

                }
            }
        }
        writer.close();
    }

    /* Busca el siguiente punto en las */
    private Point getNextPoint(final int id, final int instant) throws IOException {
        final long pointer = datareader.getFilePointer();
        String currentLine;
        Point nextPoint = null;
        int cont = 0;
        while ((currentLine = datareader.readLine()) != null) {
            cont++;
            final String[] result = currentLine.trim().split("\\s");
            final int ninstant = (int) Math
                    .floor(Integer.valueOf(result[0]) / this.configuracion.getSegundosEntreInstantes()); // En
                                                                                                         // segundos
            final int nid = Integer.valueOf(result[1]);
            if ((nid == id) && (instant != ninstant)) {
                final int x = Integer.valueOf(result[2]); // Latitud
                final int y = Integer.valueOf(result[3]); // Longitud
                nextPoint = new Point(ninstant, y, x, this.configuracion.getMetrosPorCelda());
                datareader.seek(pointer);
                return nextPoint;
            }
            if ((ninstant - instant) > this.limiteSuperior) {
                datareader.seek(pointer);
                return null;
            }
        }
        datareader.seek(pointer);
        return null;

    }

    private void escribirPosiciones(final int lastInstant) throws IOException {
        for (int i = 1; i <= this.numeroObjetos; i++) {
            if ((FILTRADO && (ID_FILTRADO == i)) || !FILTRADO) {
                final Point punto = this.mapaIds.get(i).getPoint();
                final Point ultima = this.lastKnownPosition.get(i);
                if ((punto.getX() != -1) && (punto.getY() != -1)) {
                    // Punto conocido
                    if ((ultima.getX() != -1) && (ultima.getY() != -1)) {
                        // Anterior posicion conocida
                        final int deltaX = punto.getX() - ultima.getX();
                        final int deltaY = punto.getY() - ultima.getY();
                        final String key = (punto.getX() - ultima.getX()) + ":" + (punto.getY() - ultima.getY());
                        if (this.mapFrecuency.containsKey(key)) {
                            this.mapFrecuency.put(key, this.mapFrecuency.get(key) + 1);
                        } else {
                            this.mapFrecuency.put(key, 1);
                        }
                    } else {
                        final String key = "reaparición";
                        if (this.mapFrecuency.containsKey(key)) {
                            this.mapFrecuency.put(key, this.mapFrecuency.get(key) + 1);
                        } else {
                            this.mapFrecuency.put(key, 1);
                        }
                    }
                } else {
                    if ((ultima.getX() != -1) && (ultima.getY() != -1)) {
                        final String key = "desaparición";
                        if (this.mapFrecuency.containsKey(key)) {
                            this.mapFrecuency.put(key, this.mapFrecuency.get(key) + 1);
                        } else {
                            this.mapFrecuency.put(key, 1);
                        }
                    } else {
                        // NADA
                    }
                }

                this.lastKnownPosition.put(i, punto);
                if ((punto.getX() != -1) && (punto.getY() != -1)) {
                    System.out.println(lastInstant + " " + i + " " + punto.getY() + " " + punto.getX());
                    mainWriter.write(lastInstant + " " + i + " " + punto.getY() + " " + punto.getX()
                            + System.getProperty("line.separator"));

                    // writer.write(lastInstant + " " + i + " " + punto.getY() +
                    // " " + punto.getX()
                    // + System.getProperty("line.separator"));
                } else {

                    // System.out.println(lastInstant + " " + i + " " + 0 + " "
                    // + 0 +
                    // System.getProperty("line.separator"));
                    // writer.write(lastInstant + " " + i + " " + 0 + " " + 0 +
                    // System.getProperty("line.separator"));
                    numObjetosDesconocidos++;
                    System.out.println(lastInstant + " " + i + " " + -1 + " " + -1);
                    mainWriter
                            .write(lastInstant + " " + i + " " + -1 + " " + -1 + System.getProperty("line.separator"));
                }
            }
        }
        momentos++;
        lastwriten = lastInstant;
    }

    // Comprueba el HashMap y asocia el nuevo estado de seconocido
    // si el objeto lleva mas de 7 minutos sin notificar su posicion
    // Si la FREQ es = 1 segundo seran 420
    // Si la FREQ es = 60 segundo seran 7
    // Si la FREQ es = 180 seran 3
    private void visitarObjetos(final int instant) {
        final Point newPoint = new Point(instant, -1, -1, this.configuracion.getMetrosPorCelda());
        final ObjectDataConGaps data = new ObjectDataConGaps();
        data.setPoint(newPoint);
        for (int i = 1; i <= this.numeroObjetos; i++) {
            if ((FILTRADO && (ID_FILTRADO == i)) || !FILTRADO) {
                final ObjectDataConGaps objeto = this.mapaIds.get(i);
                if ((objeto.getPoint().getX() != 0) && (objeto.getPoint().getY() != 0)) {
                    if ((instant - objeto.getPoint().getInstant()) > this.limiteSuperior) {
                        // Lleva demasiado tiempo sin notificar.
                        // Anotamos el lugar en donde desaparece
                        if (!this.mapIdsDesaparecidos.get(i).equals("-1:-1")) {
                            // Ya estaba desaparecido
                        } else {
                            this.mapaIds.put(i, data);
                            final String posicion = objeto.getPoint().getX() + ":" + objeto.getPoint().getY();
                            this.mapIdsDesaparecidos.put(i, posicion);
                            // System.out.println("Desaparece " + i + ": " +
                            // instant + " ultima Notificacion "
                            // + objeto.getPoint().getInstant());
                            // String key = "desaparecido";
                            // if (mapFrecuency.containsKey(key)) {
                            // mapFrecuency.put(key, mapFrecuency.get(key) + 1);
                            // } else {
                            // mapFrecuency.put(key, 1);
                            // }
                        }
                    }
                }
            }
        }

    }

    // 463 km/hora es el record de velocidad. Nada debería superarlo
    // 1 celda = 30 m/celda
    // máx 61.73 celdas/min

    private int compararPuntos(final Point oldPoint, final Point newPoint, final int id) throws IOException {
        if ((oldPoint.getX() == newPoint.getX()) && (oldPoint.getY() == newPoint.getY())) {
            // Actualización de posición
            final ObjectDataConGaps objeto = new ObjectDataConGaps();
            objeto.setPoint(newPoint);
            objeto.setObjectId(id);
            this.mapaIds.put(id, objeto);
            return 0;
        }
        if ((oldPoint.getX() == -1) && (oldPoint.getY() == -1)) {
            // Reaparecido
            this.mapInformationIds.put(id, new ObjectInformation(0d, 0d));
            if (!this.mapIdsDesaparecidos.get(id).equals("-1:-1")) {
                final String key = "reaparecido";
                // if (mapFrecuency.containsKey(key)) {
                // mapFrecuency.put(key, mapFrecuency.get(key) + 1);
                // } else {
                // mapFrecuency.put(key, 1);
                // }
                final String posicion = this.mapIdsDesaparecidos.get(id);
                final String[] result = posicion.trim().split(":");
                final int x = Integer.valueOf(result[0]);
                final int y = Integer.valueOf(result[1]);
                this.mapIdsDesaparecidos.put(id, "-1:-1");
                // System.out.println("Reaparece " + id + ": " +
                // newPoint.getInstant());
            }
            return 1;
        } else {
            final int deltaX = Math.abs(newPoint.getX() - oldPoint.getX());
            final int deltaY = Math.abs(newPoint.getY() - oldPoint.getY());
            final int deltaT = newPoint.getInstant() - oldPoint.getInstant();
            final double speed = (Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))) / deltaT;
            // 1.02883 celdas/segundo equivale a 60 nudos
            if ((deltaX < MAXIMO_DESPLAZAMIENTO) && (deltaY < MAXIMO_DESPLAZAMIENTO)) {

                Point nextPoint;
                if (speed > 0) {

                    if ((deltaX > 30) || (deltaY > 30)) {
                        nextPoint = getNextPoint(id, newPoint.getInstant());
                        if (nextPoint != null) {
                            if (distanciaEntre(newPoint, oldPoint) > distanciaEntre(oldPoint, nextPoint)) {
                                return 0;

                            }
                        }
                    }
                }

                // String key = (newPoint.getX() - oldPoint.getX()) + ":" +
                // (newPoint.getY() - oldPoint.getY());
                // if (mapFrecuency.containsKey(key)) {
                // mapFrecuency.put(key, mapFrecuency.get(key) + 1);
                // } else {
                // mapFrecuency.put(key, 1);
                // }

                return 1;

            } else {

                return 0;

            }
        }
    }

    public static void showUsage() {
        System.out.println("Proper Usage is: java DataAnalyser start end");
        System.exit(0);
    }

    public void analizarInstante(final int instante) {

    }

    private void escribirResultados() throws IOException {

        visitarObjetos(80);
        for (int i = lastwriten; i <= 80; i++) {
            escribirPosiciones(i);
        }
        final DecimalFormat df = new DecimalFormat("#.####");

        final ValueComparator bvc = new ValueComparator(this.mapFrecuency);
        final TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        sorted_map.putAll(this.mapFrecuency);

        for (final String key : sorted_map.keySet()) {
            // System.out.println(unidimensionar(key));
            System.out.println(this.mapFrecuency.get(key) + " - " + key);
            statiticswriter.write(unidimensionar(key) + System.getProperty("line.separator"));
        }
        // statiticswriter.write("Datos: " +
        // System.getProperty("line.separator"));
        // statiticswriter.write("Tamaño celda: " + "1,41 m2 * " + TAMANO_CELDAS
        // +
        // System.getProperty("line.separator"));
        // statiticswriter.write("Frecuencia de muestreo: " + FREQ_MUESTREO + "
        // segundos" +
        // System.getProperty("line.separator"));
        // statiticswriter.write("Nº medio de objetos en estado desconocido: " +
        // df.format((float)
        // numObjetosDesconocidos / momentos) +
        // System.getProperty("line.separator"));
        System.out.println("Datos: ");
        System.out.println("Tamaño celda: " + "1,41 m2 * " + TAMANO_CELDAS);
        System.out.println("Frecuencia de muestreo: " + this.configuracion.getSegundosEntreInstantes() + " segundos");
        System.out.println(
                "Nº medio de objetos en estado desconocido: " + df.format((float) numObjetosDesconocidos / momentos));

    }

    private float distanciaEntre(final Point a, final Point b) {
        final float dist = (float) Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
        return dist;
    }

    /* La clave debe ser X:Y */
    private int unidimensionar(final String key) {
        if ((key != "reaparición") && (key != "desaparición")) {
            final String[] result = key.trim().split(":");
            final int x = Integer.valueOf(result[0]);
            final int y = Integer.valueOf(result[1]);
            final int max = Math.max(Math.abs(x), Math.abs(y));
            final int max2 = max * 2;
            // max2 siempre sera par por lo que se tratará de la esquina
            // inferior izquierda de la espiral
            if ((x == -max) || (y == -max)) {
                // Esta en uno de los lados de la esquina inferior izquierda
                final int deltaX = x - (-max);
                final int deltaY = y - (-max);
                if (deltaX == 0) {
                    // Lado izquierdo
                    return (int) Math.pow(max2, 2) + deltaY;
                } else {
                    // Lado abajo
                    return (int) Math.pow(max2, 2) - deltaX;
                }
            } else {
                if ((x == max) && (x != y)) {
                    // Lado derecho
                    final int temp = max2 - 1;
                    return ((int) Math.pow(temp, 2) + ((max - 1) - y));

                } else {
                    // Lado superior
                    final int temp = max2 + 1;
                    return ((int) Math.pow(temp, 2) - ((max + 1) - x));
                }

                // Esta en uno de los lados de la esquina superior derecha

            }
        } else if (key == "reaparición") {
            // Puntos inalcanzables por los barcos pero que si que entran dentro
            // de los puntos posibles
            return unidimensionar(-(MAXIMO_DESPLAZAMIENTO - 1) + ":" + -(MAXIMO_DESPLAZAMIENTO - 1));
        } else {
            return unidimensionar((MAXIMO_DESPLAZAMIENTO - 1) + ":" + (MAXIMO_DESPLAZAMIENTO - 1));
        }

    }

    private String obtenerMovimiento(final int mov) {
        final int temp = (int) Math.round(Math.sqrt(mov));
        final int temp2 = (int) Math.pow(temp, 2);
        if ((temp % 2) == 0) {
            // Par
            final int dividido = temp / 2;

            if (temp2 > mov) {
                // Lado Abajo
                return ((-dividido) + (temp2 - mov)) + ":-" + dividido;
            } else {
                // Lado Izquierdo
                return "-" + dividido + ":" + ((-dividido) + (mov - temp2));
            }
        } else {
            // Impar
            final int dividido = (temp - 1) / 2;
            final int x = dividido + 1;
            final int y = dividido;
            if (temp2 >= mov) {
                // Lado arriba
                return (x - (temp2 - mov)) + ":" + y;
            } else {
                // Lado derecha
                return x + ":" + (y - (mov - temp2));
            }
        }

    }

    public static void main(final String[] args) throws Exception {
        final PreparadorDatos analizador = new PreparadorDatos();
        analizador.run();

    }

    class ValueComparator implements Comparator<String> {

        Map<String, Integer> base;

        public ValueComparator(final Map<String, Integer> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        public int compare(final String a, final String b) {
            if (this.base.get(a) >= this.base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

    /**
     * En el primer analisis se establecen los limites del mapa y el numero de objetos diferentes que existen
     * 
     * @throws ImpossibleToSolveColisionException
     * @throws NumberFormatException
     */
    @RequestMapping("/analizar")
    private void primerAnalisis() throws NumberFormatException, ImpossibleToSolveColisionException {
        final Resource ficheroEntrada = this.resourceLoader.getResource("classpath:imis1day");
        // Inicializamos el lector del fichero
        final InputStream is;
        try {
//             is = ficheroEntrada.getInputStream();
//            
//             final BufferedReader br = new BufferedReader(new InputStreamReader(is));
//             String line;
//             final ParseadorFicherosEntrada parseador = new ParseadorFicherosEntradaImis(
//             this.configuracion.getSegundosEntreInstantes());
//            
//             Double menorLatitud = 50000D;
//             Double mayorLatitud = 0D;
//             Double menorLongitud = 50000D;
//             Double mayorLongitud = 0D;
//             final Set<Integer> idsObjetos = new HashSet<Integer>();
//             long numeroLineas = 0L;
//             int ultimoInstante = 0;
//             line = br.readLine();
//             while ((line = br.readLine()) != null) {
//             final LineaEntradaParseada linea = parseador.parsearLineaEntrada(line);
//            
//             if (linea.getPosX() > mayorLongitud) {
//             mayorLongitud = linea.getPosX();
//             }
//             if (linea.getPosY() > mayorLatitud) {
//             mayorLatitud = linea.getPosY();
//             }
//             if (linea.getPosX() < menorLongitud) {
//             menorLongitud = linea.getPosX();
//             }
//             if (linea.getPosY() < menorLatitud) {
//             menorLatitud = linea.getPosY();
//             }
//             idsObjetos.add(linea.getIdObjeto());
//             ultimoInstante = linea.getInstante();
//             numeroLineas++;
//             }
//             System.out.println("Analizando limites");
//             analizadorDeLimites(menorLatitud, mayorLatitud, menorLongitud, mayorLongitud, idsObjetos, numeroLineas,
//             ultimoInstante);
//             System.out.println("Inicializar posiciones");
//             inicializarPosiciones();
             this.limiteSuperior = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
             * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));
//             System.out.println("Velocidad máxima (cuadrados/instante):       " + this.limiteSuperior);
//             br.close();
//             is.close();
//             System.out.println("Crear fichero normalizado");
//             crearFicheroNormalizado();
//             final Resource ficheroNormalizado = this.resourceLoader.getResource("classpath:imis1dayNormalizado");
//            
//             // Si un objeto produce varias notificaciones entre instantes habra datos de mas
//             final SolucionadorRepetidosHelper solucionadorRepetidos = new SolucionadorRepetidosHelper();
//             System.out.println("Eliminar datos repetidos");
//             solucionadorRepetidos.resolverRepeticiones(ficheroNormalizado);
//            
//             final Resource ficheroSinRepeticiones =
//             this.resourceLoader.getResource("classpath:datafileSinRepetidos");
//            
//             final SolucionadorColisionesHelper solucionadorColisiones = new SolucionadorColisionesHelper();
//             System.out.println("Solucionar colisiones");
//             int numColisiones = solucionadorColisiones.resolverColisiones(ficheroSinRepeticiones);
//            
//             System.out.println("Numero colisiones:  " + numColisiones);
//
            final Resource ficheroSinColisiones =
                    this.resourceLoader.getResource("classpath:datafileSinColisiones");
//
//             final SolucionadorColisionesHelper revisorColisiones = new SolucionadorColisionesHelper();
//             System.out.println("Revisar colisiones");
//             numColisiones = revisorColisiones.detectarColisiones(ficheroSinColisiones);
//            
//             System.out.println("Numero colisiones final :  " + numColisiones);

            final CreadorFicheroFrecuencias frecuenciasCreador = new CreadorFicheroFrecuencias(this.limiteSuperior);
            frecuenciasCreador.inicializar();
            frecuenciasCreador.crearFicheroFrecuencias(this.configuracion, ficheroSinColisiones);
            System.out.println("Fichero frecuencias generado");
//            CreadorEstructura creador = new CreadorEstructura(this.limiteSuperior, null , null);
//            
//            final Resource ficheroFrecuencias =
//                    this.resourceLoader.getResource("classpath:frecuencias");
//            
//            creador.inicializar(ficheroFrecuencias,configuracion);
//            creador.crearEstructura(ficheroSinColisiones, configuracion);
            // fillHashMap();
        } catch (final Exception e) {
            throw new InternalError(e);
        }

    }

    private void copyFile(final Resource ficheroSinColisiones, final File tempFile) {
        InputStream inStream = null;
        OutputStream outStream = null;

        try {

            final File afile = ficheroSinColisiones.getFile();

            inStream = new FileInputStream(afile);
            outStream = new FileOutputStream(tempFile);

            final byte[] buffer = new byte[1024];

            int length;
            // copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0) {

                outStream.write(buffer, 0, length);

            }

            inStream.close();
            outStream.close();

        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    private void crearFicheroNormalizado() {
        final Resource ficheroEntrada = this.resourceLoader.getResource("classpath:imis1day");

        // Inicializamos el lector del fichero
        InputStream is;

        try {
            // create new file

            final File file = new File("src/main/resources/imis1dayNormalizado");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            final FileWriter fw = new FileWriter(file.getAbsoluteFile());
            final BufferedWriter bw = new BufferedWriter(fw);
            // write in file

            // close connection

            is = ficheroEntrada.getInputStream();

            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            final ParseadorFicherosEntrada parseador = new ParseadorFicherosEntradaImis(
                    this.configuracion.getSegundosEntreInstantes());

            line = br.readLine();

            while ((line = br.readLine()) != null) {
                final LineaEntradaParseada datos = parseador.parsearLineaEntrada(line);

                final String lineaActual = datos.getInstante() + " " + datos.getIdObjeto() + " "
                        + this.normalizador.calcularXnormalizado(datos.getPosX()) + " "
                        + this.normalizador.calcularYnormalizado(datos.getPosY()) + "\n";

                bw.write(lineaActual);
            }
            bw.close();
            br.close();
            is.close();
            System.out.println("Fichero normalizado creado");
        } catch (final IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Analizador de limites.
     * 
     * @param menorLatitud
     *            menor latitud
     * @param mayorLatitud
     *            mayor latitud
     * @param menorLongitud
     *            menor longitud
     * @param mayorLongitud
     *            mayor longitud
     * @param idsObjetos
     *            ids objetos
     * @param numeroLineas
     *            numero lineas
     * @param ultimoInstante
     *            ultimo instante
     */
    private void analizadorDeLimites(final Double menorLatitud, final Double mayorLatitud, final Double menorLongitud,
            final Double mayorLongitud, final Set<Integer> idsObjetos, final long numeroLineas,
            final int ultimoInstante) {
        double ladoSuperior = ControladorHelper.haversine_km(mayorLatitud, menorLongitud, mayorLatitud, mayorLongitud);
        double ladoInferior = ControladorHelper.haversine_km(menorLatitud, menorLongitud, menorLatitud, mayorLongitud);
        double menorLongitudCorregida = menorLongitud;
        double mayorLongitudCorregida = mayorLongitud;
        if (ladoSuperior >= ladoInferior) {
            // Hemisferio Sur
            while (ladoSuperior >= ladoInferior) {
                menorLongitudCorregida -= 0.001;
                mayorLongitudCorregida += 0.001;
                ladoInferior = ControladorHelper.haversine_km(menorLatitud, menorLongitudCorregida, menorLatitud,
                        mayorLongitudCorregida);
            }
        } else {
            // Hemisferio Norte
            while (ladoInferior >= ladoSuperior) {
                menorLongitudCorregida -= 0.001;
                mayorLongitudCorregida += 0.001;
                ladoSuperior = ControladorHelper.haversine_km(menorLatitud, menorLongitudCorregida, menorLatitud,
                        mayorLongitudCorregida);
            }
        }

        final double ladoLateral = ControladorHelper.haversine_km(menorLatitud, mayorLongitud, mayorLatitud,
                mayorLongitud);
        final int numeroCeldasLado = (int) Math
                .ceil((Math.max(ladoLateral, ladoSuperior) * 1000) / this.configuracion.getMetrosPorCelda());
        System.out.println("***********************************");
        System.out.println("Número Lineas:              " + numeroLineas);
        System.out.println("Mayor Longitud:             " + mayorLongitud);
        System.out.println("Menor Longitud:             " + menorLongitud);
        System.out.println("Mayor Longitud Corregida:   " + mayorLongitudCorregida);
        System.out.println("Menor Longitud Corregida:   " + menorLongitudCorregida);
        System.out.println("Mayor Latitud:              " + mayorLatitud);
        System.out.println("Menor Latitud:              " + menorLatitud);
        System.out.println("Último instante:            " + ultimoInstante);
        System.out.println("Número objetos:             " + idsObjetos.size());
        System.out.println("Distancias (Lado superior): " + ladoSuperior);
        System.out.println("Distancias (Lado inferior): " + ladoInferior);
        System.out.println("Distancias (Lado Izq):      " + ladoLateral);
        System.out.println("Distancias (Lado Dcho):     " + ladoLateral);
        System.out.println("Distancias (Hipotenusa):    "
                + ControladorHelper.haversine_km(menorLatitud, menorLongitud, mayorLatitud, mayorLongitud));
        System.out.println("Numero cuadrados lado:      " + numeroCeldasLado);
        System.out.println("Numero cuadrados totales:   " + (numeroCeldasLado * numeroCeldasLado));
        System.out.println("***********************************");
        this.numeroObjetos = 0;
        for (final Integer integer : idsObjetos) {
            if (this.numeroObjetos < integer) {
                this.numeroObjetos = integer;
            }
        }

        this.normalizador = new Normalizador(menorLatitud, mayorLatitud, menorLongitudCorregida,
                mayorLongitudCorregida,
                numeroCeldasLado);

    }
}

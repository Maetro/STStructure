package es.ramon.casares.proyecto.modelo.matrix;
 

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The Class MatrixOfPositions.
 */
public class MatrixOfPositions {

	/**
	 * Instantiates a new matrix of positions.
	 */
	public MatrixOfPositions() {
	}
	
	/** The matriz. */
	List<List<Integer>> matriz = new ArrayList<List<Integer>>();

	/**
	 * Gets the matriz.
	 *
	 * @return the matriz
	 */
	public List<List<Integer>> getMatriz() {
		return matriz;
	}

	/**
	 * Sets the matriz.
	 *
	 * @param matriz the new matriz
	 */
	public void setMatriz(List<List<Integer>> matriz) {
		this.matriz = matriz;
	}

	/**
	 * Instantiates a new matrix of positions.
	 *
	 * @param matriz the matriz
	 */
	public MatrixOfPositions(List<List<Integer>> matriz) {
		super();
		this.matriz = matriz;
	}
	
	/**
	 * Inicializar matriz. La matriz debe ser cuadrada, por tanto si el plano 
	 * contiene más ancho o más alto se tomara el maximo entre ellos.
	 * La primera posicion de la matriz corresponde a la 0,0 La matriz corresponde a una lista de filas
	 *
	 * @param limits the limits
	 * @param minimumsquare the minimumsquare
	 * @return the integer
	 */
	public Integer inicializarMatriz(int limits, Integer minimumsquare){
		Integer limite = MatrixOfPositions.round(limits, minimumsquare);
		for (int i = 0; i<limite; i++) {
			ArrayList<Integer> fila = new ArrayList<Integer>();
			for (int j = 0; j<limite; j++){
				fila.add(0);
			}
			matriz.add(fila);
		}
		return limite;
	}
	
	private static int round(double num, int multipleOf) {
		
		    return (int) (Math.ceil(num/multipleOf) * multipleOf);
		
    }
	
	/**
	 * Anadir objeto a posicion.
	 *
	 * @param idObjeto the id objeto
	 * @param x the x
	 * @param y the y
	 */
	public void anadirObjetoAPosicion(final Integer idObjeto, final Integer x, final Integer y){
		List<Integer> columna = matriz.get(y);
		columna.set(x, idObjeto); 
	}

	/**
	 * Anadir objeto a posicion.
	 *
	 * @param informacionInstanteObjeto the informacion instante objeto
	 */
	public void anadirObjetoAPosicion(InformacionInstanteObjeto informacionInstanteObjeto) {
		List<Integer> columna = matriz.get(informacionInstanteObjeto.getPosicionY());
		columna.set(informacionInstanteObjeto.getPosicionX(), informacionInstanteObjeto.getObjetoId());
		
	}
	
}

package es.ramon.casares.proyecto.modelo.k2tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.util.CollectionUtils;

import es.ramon.casares.proyecto.modelo.matrix.InformacionInstanteObjeto;
import es.ramon.casares.proyecto.modelo.matrix.MatrixOfPositions;
import es.ramon.casares.proyecto.modelo.matrix.RegionAnalizarBean;

/**
 * The Class K2TreeHelper.
 */
public class K2TreeHelper {


	/**
	 * Generar k2 tree.
	 *
	 * @param listaInfo the lista info
	 * @return the k2 tree
	 */
	public static K2Tree generarK2Tree(List<InformacionInstanteObjeto> listaInfo,Integer limites, Integer minimumSquare){
		regionesPendientesAnalizar = new LinkedList<RegionAnalizarBean>();
		T = new ArrayList<Integer>();
		L = new ArrayList<Integer>();	
		MatrixOfPositions matrizPosiciones = new MatrixOfPositions();
		Integer numeroCeldas = matrizPosiciones.inicializarMatriz(limites, minimumSquare);
		for (InformacionInstanteObjeto informacionInstanteObjeto : listaInfo) {
			matrizPosiciones.anadirObjetoAPosicion(informacionInstanteObjeto);	
		}
		
		analizarRegion(matrizPosiciones, 0, 0, numeroCeldas - 1 , numeroCeldas - 1, 1);
		
		while (!CollectionUtils.isEmpty(regionesPendientesAnalizar)){
			RegionAnalizarBean siguienteRegion = regionesPendientesAnalizar.poll();
			analizarRegion(matrizPosiciones, siguienteRegion.getPuntoXinferior(), siguienteRegion.getPuntoYinferior(),
					siguienteRegion.getPuntoXsuperior() , siguienteRegion.getPuntoXsuperior(), siguienteRegion.getNivel());
		}
		
		return null;
		
		
		
	}

	private static Queue<RegionAnalizarBean> regionesPendientesAnalizar = new LinkedList<RegionAnalizarBean>();
	private static List<Integer> T = new ArrayList<Integer>();
	private static List<Integer> L = new ArrayList<Integer>();				
					
	private static void analizarRegion(MatrixOfPositions matriz, Integer puntoXinferior,
			Integer puntoYinferior, Integer puntoXsuperior, Integer puntoYsuperior, Integer nivel){
		    
		Integer datos = 0;
		if ((puntoXsuperior - puntoXinferior) <= 1){
			// Último cuadrado
			datos = analisisCuadradoFinal(matriz, puntoXinferior, puntoYinferior, puntoXsuperior, puntoYsuperior, datos);
			L.add(datos);
		} else {
			// Por cada ciclo hay que analizar 4 posibilidades.
			Integer puntoMedioY = (puntoYsuperior - puntoYinferior)/ 2;
			Integer puntoMedioX = (puntoXsuperior - puntoXinferior)/ 2;
			datos = analisisCuadrado(matriz, puntoXinferior, puntoYinferior, puntoXsuperior, puntoYsuperior, datos,
					puntoMedioY, puntoMedioX, nivel+1); 
			T.add(datos);
		}
	}

	private static Integer analisisCuadradoFinal(MatrixOfPositions matriz, Integer puntoXinferior, Integer puntoYinferior,
			Integer puntoXsuperior, Integer puntoYsuperior, Integer datos) {
				// Esquina SuperiorIzquierda
				List<Integer> resultados = new ArrayList<Integer>();
				List<Integer> filaSuperior  = matriz.getMatriz().get(puntoYsuperior);
				List<Integer> filaInferior = matriz.getMatriz().get(puntoYinferior);
				if (filaSuperior.get(puntoXinferior) != 0){
					resultados.add(filaSuperior.get(puntoXinferior));
					datos += 8;
				} 
				// Esquina SuperiorDerecha
				if (filaSuperior.get(puntoXsuperior) != 0){
					resultados.add(filaSuperior.get(puntoXsuperior));
					datos += 4;
				} 
				// Esquina InferiorIzquierda
				if (filaInferior.get(puntoXinferior) != 0){
					resultados.add(filaInferior.get(puntoXinferior));
					datos += 2;
				} 
				// Esquina InferiorDerecha
				if (filaInferior.get(puntoXsuperior) != 0){
					resultados.add(filaInferior.get(puntoXsuperior));
					datos += 1;
				} 
				return datos;
	}

	/**
	 * Analisis cuadrado.
	 *
	 * @param matriz the matriz
	 * @param puntoXinferior the punto xinferior
	 * @param puntoYinferior the punto yinferior
	 * @param puntoXsuperior the punto xsuperior
	 * @param puntoYsuperior the punto ysuperior
	 * @param datos the datos
	 * @param puntoMedioY the punto medio y
	 * @param puntoMedioX the punto medio x
	 * @param nivel 
	 * @return the integer
	 */
	private static Integer analisisCuadrado(MatrixOfPositions matriz, Integer puntoXinferior, Integer puntoYinferior,
			Integer puntoXsuperior, Integer puntoYsuperior, Integer datos, Integer puntoMedioY, Integer puntoMedioX, Integer nivel) {
		// Esquina SuperiorIzquierda
		if (hayValoresEnRegion(matriz,puntoXinferior,puntoMedioY + 1,puntoMedioX,puntoYsuperior)){
			datos += 8;
			regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoXinferior,puntoMedioY + 1,puntoMedioX,puntoYsuperior,nivel));  
		} 
		// Esquina SuperiorDerecha
		if (hayValoresEnRegion(matriz,puntoMedioX + 1,puntoMedioY+1,puntoXsuperior,puntoYsuperior)){
			datos += 4;
			regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoMedioX + 1,puntoMedioY+1,puntoXsuperior,puntoYsuperior,nivel));  
		} 
		// Esquina InferiorIzquierda
		if (hayValoresEnRegion(matriz,puntoXinferior,puntoYinferior,puntoMedioX,puntoMedioY)){
			datos += 2;
			regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoXinferior,puntoYinferior,puntoMedioX,puntoMedioY,nivel));  
		} 
		// Esquina InferiorDerecha
		if (hayValoresEnRegion(matriz,puntoMedioX + 1,puntoYinferior,puntoXsuperior,puntoMedioY)){
			datos += 1;
			regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoMedioX + 1,puntoYinferior,puntoXsuperior,puntoMedioY,nivel));  
		}
		return datos;
	}
	
	
	
	/**
	 * Hay valores en region.
	 *
	 * @param matriz the matriz
	 * @param puntoXinferior the punto xinferior
	 * @param puntoYinferior the punto yinferior
	 * @param puntoXsuperior the punto xsuperior
	 * @param puntoYsuperior the punto ysuperior
	 * @return true, if successful
	 */
	public static boolean hayValoresEnRegion(MatrixOfPositions matriz, Integer puntoXinferior,
			Integer puntoYinferior, Integer puntoXsuperior, Integer puntoYsuperior){
		for (int i = puntoYinferior; i <= puntoYsuperior; i++){
			List<Integer> fila  = matriz.getMatriz().get(i);
			for (int j = puntoXinferior; j <= puntoXsuperior; j++){
				if (fila.get(j) != 0){
					return true;
				}
			}
		}		
		return false;
	}
	
}

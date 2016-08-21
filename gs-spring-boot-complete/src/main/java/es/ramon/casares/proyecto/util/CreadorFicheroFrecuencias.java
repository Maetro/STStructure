/**
 * CreadorFicheroFrecuencias.java 11-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.Resource;

import es.ramon.casares.proyecto.modelo.log.Movimiento;
import es.ramon.casares.proyecto.modelo.objetos.ObjetoMovil;
import es.ramon.casares.proyecto.modelo.objetos.PosicionKey;
import es.ramon.casares.proyecto.util.SolucionadorColisionesHelper.ImpossibleToSolveColisionException;

/**
 * The Class CreadorFicheroFrecuencias.
 * 
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon
 *         Casares</a>
 */
public class CreadorFicheroFrecuencias {

	// Las posiciones ocupadas en el momento actual del fichero
	private final HashMap<PosicionKey, ObjetoMovil> posicionIds = new HashMap<PosicionKey, ObjetoMovil>();

	private Map<Movimiento, Integer> movimientos = new HashMap<Movimiento, Integer>();

	private final Set<Integer> desaparecidoRelativo = new HashSet<Integer>();
	private final Set<Integer> desaparecidoAbsoluto = new HashSet<Integer>();

	private final Integer limite;

	private RandomAccessFile datareader; // es

	private Movimiento desaparicion;
	private Movimiento reaparicion;
	private Movimiento reaparicionAbsoluta;

	/** The mapa ids. */
	private final HashMap<Integer, ObjetoMovil> mapaIds = new HashMap<Integer, ObjetoMovil>();

	/**
	 * Instancia un nuevo creador fichero frecuencias.
	 * 
	 * @param limite
	 *            limite
	 */
	public CreadorFicheroFrecuencias(final Integer limite) {
		this.limite = limite;
	}

	/**
	 * Inicializar.
	 */
	public void inicializar() {

		this.desaparicion = new Movimiento(this.limite, this.limite);
		this.reaparicion = new Movimiento(-this.limite, -this.limite);
		this.reaparicionAbsoluta = new Movimiento(this.limite, -this.limite);
		for (int i = -this.limite; i <= this.limite; i++) {
			for (int j = -this.limite; j <= this.limite; j++) {
				this.movimientos.put(new Movimiento(i, j), 0);
			}
		}
	}

	public void crearFicheroFrecuencias(final ConfiguracionHelper configuracion, final Resource fichero)
			throws NumberFormatException, IOException, ImpossibleToSolveColisionException {
		String currentLine;

		System.out.println("Generando fichero de frecuencias");
		final File tempFile = new File("src/main/resources/frecuencias");

		// if file doesnt exists, then create it
		if (!tempFile.exists()) {
			tempFile.createNewFile();
		}

		final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		int lastInstant = 0;
		this.datareader = new RandomAccessFile(fichero.getFile(), "r");

		while ((currentLine = this.datareader.readLine()) != null) {
			final String[] result = currentLine.trim().split("\\s");
			final int instant = Integer.valueOf(result[0]); // En segundos
			final int id = Integer.valueOf(result[1]);
			final int x = Integer.valueOf(result[2]); // Longitud
			final int y = Integer.valueOf(result[3]); // Latitud
//			if (id == 4222){
//				System.out.println(instant + " " + id + " " + x + " "+ y);
//			}
			if (instant != lastInstant) {
				// Cambio de instante. Comprobamos si algun objeto ha
				// desaparecido
				lastInstant = instant;
				final Set<PosicionKey> posiciones = new HashSet<PosicionKey>(this.posicionIds.keySet());
				for (final PosicionKey posicion : posiciones) {
					final ObjetoMovil objeto = this.posicionIds.get(posicion);
					if ((objeto.getInstante() + configuracion.getInstantesHastaDesaparicion()) <= instant) {
						// El objeto ha desaparecido
						this.posicionIds.remove(posicion);
						this.mapaIds.remove(objeto.getObjetoId());
						this.desaparecidoRelativo.add(objeto.getObjetoId());
						Integer num = this.movimientos.get(this.desaparicion);
						num++;
						this.movimientos.put(this.desaparicion, num);
					}
				}

				if (((instant-1) % configuracion.getDistanciaEntreSnapshots()) == 0) {
					// Cambio de Snapshot, los desaparecidos parciales pasan a
					// completos
//					System.out.println(" Snapshot: " + (instant-1) );
					for (final Integer idObjetoDesaparecido : this.desaparecidoRelativo) {
						this.desaparecidoAbsoluto.add(idObjetoDesaparecido);
					
					}
					this.desaparecidoRelativo.clear();
				}
			}

			final PosicionKey claveNum = new PosicionKey(x, y);
			final ObjetoMovil nuevaPos = new ObjetoMovil(id, instant, x, y);

			if (this.mapaIds.containsKey(id)) {
				// Si esta en el mapa hay que cambiar
				// la posicion anotada
				final ObjetoMovil viejaPos = this.mapaIds.get(id);
				final PosicionKey viejaClaveNum = new PosicionKey(viejaPos.getPosicionX(), viejaPos.getPosicionY());
				if (this.posicionIds.get(viejaClaveNum).getObjetoId() == id) {
					this.posicionIds.remove(viejaClaveNum);
					this.mapaIds.remove(viejaClaveNum);
				}

				if (!this.posicionIds.containsKey(claveNum)) { // no hay
																// colision
					anotarMovimiento(id, claveNum, nuevaPos, viejaPos);
				} else { // Hay colision

					throw new InternalError("COLISION");
				}
			} else {
				// Es la primera vez que se anota este objeto o estaba
				// desaparecido
				// Miramos si su posicion esta ocupada
				if (this.desaparecidoRelativo.contains(id)) {
					if (!this.posicionIds.containsKey(claveNum)) { // no hay colision
						anotarReaparicionRelativa(id, x, y, nuevaPos);
					} else { // Hay colision
						throw new InternalError("COLISION");
					}

				}
				if (this.desaparecidoAbsoluto.contains(id) && (instant != 0)) {
					if (!this.posicionIds.containsKey(claveNum)) { // no hay colision
						anotarReaparicionAbsoluta(id, x, y, nuevaPos);
					} else { // Hay colision
						
						throw new InternalError("COLISION");
					}
				}
				if (instant == 0) {
					// Posicion libre
					anotarMovimiento(id, claveNum, nuevaPos, null);
				} else {
					anotarReaparicionAbsoluta(id, x, y, nuevaPos);
				}
			}

		}

		this.movimientos = ControladorHelper.sortByValue(this.movimientos);
		final Set<Movimiento> movimientosSet = this.movimientos.keySet();
		for (final Movimiento mov : movimientosSet) {
			
			writer.write(ControladorHelper.unidimensionar(mov.getX(), mov.getY())+ "\n");
		}

	}

	private void anotarReaparicionAbsoluta(final int id, final int x, final int y, final ObjetoMovil nuevaPos) {
		final PosicionKey claveNum = new PosicionKey(x, y);
		this.posicionIds.put(claveNum, nuevaPos);
		this.mapaIds.put(id, nuevaPos);
		Integer num = this.movimientos.get(this.reaparicionAbsoluta);
		num++;
		this.movimientos.put(this.reaparicionAbsoluta, num);
		if (desaparecidoAbsoluto.contains(id)){
			this.desaparecidoAbsoluto.remove(id);
		}

	}

	private void anotarReaparicionRelativa(final int id, final int x, final int y, final ObjetoMovil nuevaPos) {
		final PosicionKey claveNum = new PosicionKey(x, y);
		this.posicionIds.put(claveNum, nuevaPos);
		this.mapaIds.put(id, nuevaPos);
		Integer num = this.movimientos.get(this.reaparicion);
		num++;
		this.movimientos.put(this.reaparicion, num);
		this.desaparecidoRelativo.remove(id);

	}

	private void anotarMovimiento(final int id, final PosicionKey claveNum, final ObjetoMovil nuevaPos,
			final ObjetoMovil viejaPos) {

		// Fuera de los limites fisicos admitidos, no se registra el movimiento.
		Movimiento movimiento = null;

		if (viejaPos != null) {
			final int diferenciaX = nuevaPos.getPosicionX() - viejaPos.getPosicionX();
			final int diferenciaY = nuevaPos.getPosicionY() - viejaPos.getPosicionY();
			if ((Math.abs(diferenciaX) <= this.limite) && (Math.abs(diferenciaY) <= this.limite)) {
				movimiento = new Movimiento(diferenciaX, diferenciaY);
				Integer num = this.movimientos.get(movimiento);
				num++;
				this.movimientos.put(movimiento, num);
			}

		}
		this.posicionIds.put(claveNum, nuevaPos);
		this.mapaIds.put(id, nuevaPos);

	}
}

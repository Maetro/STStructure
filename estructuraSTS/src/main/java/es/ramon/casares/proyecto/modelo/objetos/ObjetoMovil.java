package es.ramon.casares.proyecto.modelo.objetos;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class InformacionInstanteObjeto.
 */
public class ObjetoMovil implements Serializable{

	
	private static final long serialVersionUID = 7776211319563188088L; 
	
	/** The objeto id. */
	private Integer objetoId;
	
	/** The instante. */
	private Integer instante;
	
	/** The posicion x. */
	private Integer posicionX;
	
	/** The posicion y. */
	private Integer posicionY;
	
	/**
	 * Instantiates a new informacion instante objeto.
	 */
	public ObjetoMovil() {
	
	}

	
	
	public ObjetoMovil(Integer objetoId, Integer instante, Integer posicionX, Integer posicionY) {
		super();
		this.objetoId = objetoId;
		this.instante = instante;
		this.posicionX = posicionX;
		this.posicionY = posicionY;
	}



	/**
	 * Gets the objeto id.
	 *
	 * @return the objeto id
	 */
	public Integer getObjetoId() {
		return objetoId;
	}

	/**
	 * Sets the objeto id.
	 *
	 * @param objetoId the new objeto id
	 */
	public void setObjetoId(Integer objetoId) {
		this.objetoId = objetoId;
	}

	/**
	 * Gets the instante.
	 *
	 * @return the instante
	 */
	public Integer getInstante() {
		return instante;
	}

	/**
	 * Sets the instante.
	 *
	 * @param instante the new instante
	 */
	public void setInstante(Integer instante) {
		this.instante = instante;
	}

	/**
	 * Gets the posicion x.
	 *
	 * @return the posicion x
	 */
	public Integer getPosicionX() {
		return posicionX;
	}

	/**
	 * Sets the posicion x.
	 *
	 * @param posicionX the new posicion x
	 */
	public void setPosicionX(Integer posicionX) {
		this.posicionX = posicionX;
	}

	/**
	 * Gets the posicion y.
	 *
	 * @return the posicion y
	 */
	public Integer getPosicionY() {
		return posicionY;
	}

	/**
	 * Sets the posicion y.
	 *
	 * @param posicionY the new posicion y
	 */
	public void setPosicionY(Integer posicionY) {
		this.posicionY = posicionY;
	}
	
     	
}

package es.ramon.casares.proyecto.parametros;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class RegionAnalizarBean.
 */
public class RegionAnalizarBean {

	/** The punto xinferior. */
	private Integer puntoXinferior;
	
	/** The punto yinferior. */
	private Integer puntoYinferior;
	
	/** The punto xsuperior. */
	private Integer puntoXsuperior; 
	
	/** The punto ysuperior. */
	private Integer puntoYsuperior;
	
	/** The nivel. */
	private Integer nivel;
	
	/**
	 * Instantiates a new region analizar bean.
	 */
	public RegionAnalizarBean() {
	}

	
	
	/**
	 * Instantiates a new region analizar bean.
	 *
	 * @param puntoXinferior the punto xinferior
	 * @param puntoYinferior the punto yinferior
	 * @param puntoXsuperior the punto xsuperior
	 * @param puntoYsuperior the punto ysuperior
	 */
	public RegionAnalizarBean(Integer puntoXinferior, Integer puntoYinferior, Integer puntoXsuperior,
			Integer puntoYsuperior, Integer nivel) {
		super();
		this.puntoXinferior = puntoXinferior;
		this.puntoYinferior = puntoYinferior;
		this.puntoXsuperior = puntoXsuperior;
		this.puntoYsuperior = puntoYsuperior;
		this.nivel = nivel;
	}



	/**
	 * Gets the punto xinferior.
	 *
	 * @return the punto xinferior
	 */
	public Integer getPuntoXinferior() {
		return puntoXinferior;
	}

	/**
	 * Sets the punto xinferior.
	 *
	 * @param puntoXinferior the new punto xinferior
	 */
	public void setPuntoXinferior(Integer puntoXinferior) {
		this.puntoXinferior = puntoXinferior;
	}

	/**
	 * Gets the punto yinferior.
	 *
	 * @return the punto yinferior
	 */
	public Integer getPuntoYinferior() {
		return puntoYinferior;
	}

	/**
	 * Sets the punto yinferior.
	 *
	 * @param puntoYinferior the new punto yinferior
	 */
	public void setPuntoYinferior(Integer puntoYinferior) {
		this.puntoYinferior = puntoYinferior;
	}

	/**
	 * Gets the punto xsuperior.
	 *
	 * @return the punto xsuperior
	 */
	public Integer getPuntoXsuperior() {
		return puntoXsuperior;
	}

	/**
	 * Sets the punto xsuperior.
	 *
	 * @param puntoXsuperior the new punto xsuperior
	 */
	public void setPuntoXsuperior(Integer puntoXsuperior) {
		this.puntoXsuperior = puntoXsuperior;
	}

	/**
	 * Gets the punto ysuperior.
	 *
	 * @return the punto ysuperior
	 */
	public Integer getPuntoYsuperior() {
		return puntoYsuperior;
	}

	/**
	 * Sets the punto ysuperior.
	 *
	 * @param puntoYsuperior the new punto ysuperior
	 */
	public void setPuntoYsuperior(Integer puntoYsuperior) {
		this.puntoYsuperior = puntoYsuperior;
	}

	/**
	 * Gets the nivel.
	 *
	 * @return the nivel
	 */
	public Integer getNivel() {
		return nivel;
	}
	
	/**
	 * Sets the nivel.
	 *
	 * @param nivel the new nivel
	 */
	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((puntoXinferior == null) ? 0 : puntoXinferior.hashCode());
		result = prime * result + ((puntoXsuperior == null) ? 0 : puntoXsuperior.hashCode());
		result = prime * result + ((puntoYinferior == null) ? 0 : puntoYinferior.hashCode());
		result = prime * result + ((puntoYsuperior == null) ? 0 : puntoYsuperior.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegionAnalizarBean other = (RegionAnalizarBean) obj;
		if (puntoXinferior == null) {
			if (other.puntoXinferior != null)
				return false;
		} else if (!puntoXinferior.equals(other.puntoXinferior))
			return false;
		if (puntoXsuperior == null) {
			if (other.puntoXsuperior != null)
				return false;
		} else if (!puntoXsuperior.equals(other.puntoXsuperior))
			return false;
		if (puntoYinferior == null) {
			if (other.puntoYinferior != null)
				return false;
		} else if (!puntoYinferior.equals(other.puntoYinferior))
			return false;
		if (puntoYsuperior == null) {
			if (other.puntoYsuperior != null)
				return false;
		} else if (!puntoYsuperior.equals(other.puntoYsuperior))
			return false;
		return true;
	}	
	
}

package es.ramon.casares.proyecto.modelo.k2tree;

import java.util.BitSet;

// TODO: Auto-generated Javadoc
/**
 * The Class K2Tree.
 */
public class K2Tree { 
	
	/** The t. */
	private BitSet t;
	
	/** The l. */
	private BitSet l;
	
	/** The permutacion. */
	private Permutation permutacion;
	
	/**
	 * Instantiates a new k2 tree.
	 *
	 * @param k2Tree the k2 tree
	 */
	public K2Tree(K2Tree k2Tree){
		this.t = k2Tree.getT();
		this.l = k2Tree.getL();
		this.permutacion = k2Tree.getPermutacion();
		
	}


	/**
	 * Instantiates a new k2 tree.
	 *
	 * @param t the t
	 * @param l the l
	 * @param permutacion the permutacion
	 */
	public K2Tree(BitSet t, BitSet l, Permutation permutacion) {
		super();
		this.t = t;
		this.l = l;
		this.permutacion = permutacion;
	}


	/**
	 * Gets the t.
	 *
	 * @return the t
	 */
	public BitSet getT() {
		return t;
	}

	/**
	 * Sets the t.
	 *
	 * @param t the new t
	 */
	public void setT(BitSet t) {
		this.t = t;
	}

	/**
	 * Gets the l.
	 *
	 * @return the l
	 */
	public BitSet getL() {
		return l;
	}

	/**
	 * Sets the l.
	 *
	 * @param l the new l
	 */
	public void setL(BitSet l) {
		this.l = l;
	}

	/**
	 * Gets the permutacion.
	 *
	 * @return the permutacion
	 */
	public Permutation getPermutacion() {
		return permutacion;
	}

	/**
	 * Sets the permutacion.
	 *
	 * @param permutacion the new permutacion
	 */
	public void setPermutacion(Permutation permutacion) {
		this.permutacion = permutacion;
	}
	
	
	
	
}

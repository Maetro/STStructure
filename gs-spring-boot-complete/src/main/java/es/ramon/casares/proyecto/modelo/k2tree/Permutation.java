package es.ramon.casares.proyecto.modelo.k2tree;

import java.util.ArrayList;
import java.util.BitSet;

// TODO: Auto-generated Javadoc
/**
 * The Class Permutation.
 */
public class Permutation {

	/** The perm. */
	private ArrayList<Integer> perm = new ArrayList<Integer>();
	
	/** The sampled. */
	private BitSet sampled = new BitSet();
	
	/** The rev_links. */
	private ArrayList<Integer> rev_links = new ArrayList<Integer>();

	/**
	 * Gets the perm.
	 *
	 * @return the perm
	 */
	public ArrayList<Integer> getPerm() {
		return perm;
	}

	/**
	 * Sets the perm.
	 *
	 * @param perm the new perm
	 */
	public void setPerm(ArrayList<Integer> perm) {
		this.perm = perm;
	}

	/**
	 * Gets the sampled.
	 *
	 * @return the sampled
	 */
	public BitSet getSampled() {
		return sampled;
	}

	/**
	 * Sets the sampled.
	 *
	 * @param sampled the new sampled
	 */
	public void setSampled(BitSet sampled) {
		this.sampled = sampled;
	}

	/**
	 * Gets the rev_links.
	 *
	 * @return the rev_links
	 */
	public ArrayList<Integer> getRev_links() {
		return rev_links;
	}

	/**
	 * Sets the rev_links.
	 *
	 * @param rev_links the new rev_links
	 */
	public void setRev_links(ArrayList<Integer> rev_links) {
		this.rev_links = rev_links;
	}
	
	
	
}

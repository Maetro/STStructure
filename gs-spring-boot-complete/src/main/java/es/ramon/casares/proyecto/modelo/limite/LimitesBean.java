package es.ramon.casares.proyecto.modelo.limite;

public class LimitesBean {

	private int limite ;
	private int idObjeto;
	
	public LimitesBean() {
	}
	
	public LimitesBean(int limite, int idObjeto) {
		super();
		this.limite = limite;
		this.idObjeto = idObjeto;
	}
	
	public int getLimite() {
		return limite;
	}
	
	public void setLimite(int limite) {
		this.limite = limite;
	}
	
	public int getIdObjeto() {
		return idObjeto;
	}
	
	public void setIdObjeto(int idObjeto) {
		this.idObjeto = idObjeto;
	}
	
	
	
}

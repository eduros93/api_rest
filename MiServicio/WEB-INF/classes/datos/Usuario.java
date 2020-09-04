package datos;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="usuario")
public class Usuario {
	
	private int id;
	private String nombre;
	private String genero;
	private String estadoCivil;
	private String poblacionDeOrigen;
	
	public Usuario(String nombre, String genero, String estadoSocial, String poblacionDeOrigen) {
		this.nombre = nombre;
		this.genero = genero;
		this.estadoCivil = estadoSocial;
		this.poblacionDeOrigen = poblacionDeOrigen;
		
	}
	public Usuario() {}
	
	public String print() {
		return "Id: "+id+ " | Nombre: "+nombre+ " | Género: "+genero+" | Estado civil: "+
				estadoCivil+ " | Poblacion de origen: "+poblacionDeOrigen;
	}
	
	@XmlAttribute(required=false)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getGenero() {
		return genero;
	}
	public void setGenero(String genero) {
		this.genero = genero;
	}
	public String getEstadoCivil() {
		return estadoCivil;
	}
	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}
	public String getPoblacionDeOrigen() {
		return poblacionDeOrigen;
	}
	public void setPoblacionDeOrigen(String poblacionDeOrigen) {
		this.poblacionDeOrigen = poblacionDeOrigen;
	}
}

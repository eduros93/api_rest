package datos;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "snapshotMovil")
public class SnapshotMovil {

	private Usuario datosBasicos;
	private Mensaje ultimoMensaje;
	private int numAmigos;
	private Mensajes ultimos10MsgsDeAmigos;
	
	public SnapshotMovil(Usuario datosBasicos, Mensaje ultimoMensaje, int numAmigos, Mensajes ultimos10MsgsDeAmigos) {
		this.datosBasicos = datosBasicos;
		this.ultimoMensaje = ultimoMensaje;
		this.numAmigos = numAmigos;
		this.ultimos10MsgsDeAmigos = ultimos10MsgsDeAmigos;
	}
	
	public SnapshotMovil() {}
	
	public String print() {
		return datosBasicos.print() + "\n\nUltimo mensaje:\n" + ultimoMensaje.print() + 
				"\n\nNúmero de amigos: " + numAmigos + "\n\nUltimos 10 mensajes de amigos:\n"+
				ultimos10MsgsDeAmigos.print();
	}
	
	public Usuario getDatosBasicos() {
		return datosBasicos;
	}
	public void setDatosBasicos(Usuario datosBasicos) {
		this.datosBasicos = datosBasicos;
	}
	public Mensaje getUltimoMensaje() {
		return ultimoMensaje;
	}
	public void setUltimoMensaje(Mensaje ultimoMensaje) {
		this.ultimoMensaje = ultimoMensaje;
	}
	public int getNumAmigos() {
		return numAmigos;
	}
	public void setNumAmigos(int numAmigos) {
		this.numAmigos = numAmigos;
	}
	public Mensajes getUltimos10MsgsDeAmigos() {
		return ultimos10MsgsDeAmigos;
	}
	public void setUltimos10MsgsDeAmigos(Mensajes ultimos10MsgsDeAmigos) {
		this.ultimos10MsgsDeAmigos = ultimos10MsgsDeAmigos;
	}
}

package datos;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="amigo")
public class Amigo {
	
	private int id;
	private int idSmall;
	private int idBig;
	
	public Amigo(int idSmall, int idBig) {
		this.idSmall = idSmall;
		this.idBig = idBig;
	}
	
	public Amigo() {}

	@XmlAttribute(required=false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdSmall() {
		return idSmall;
	}

	public void setIdSmall(int idSmall) {
		this.idSmall = idSmall;
	}

	public int getIdBig() {
		return idBig;
	}

	public void setIdBig(int idBig) {
		this.idBig = idBig;
	}

}

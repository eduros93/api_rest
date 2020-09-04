package datos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mensajes")
@XmlAccessorType (XmlAccessType.FIELD)
public class Mensajes {
	
	@XmlElement(name = "mensaje")
	private List<Mensaje> msgs = new ArrayList<Mensaje>();
	
	public Mensajes() {}
	
	public String print() {
		String s = "";
		for(Mensaje msg : msgs) {
			s += msg.print() + "\n";
		}
		return s;
	}

	public List<Mensaje> getMsgs() {
		return msgs;
	}

	public void setMsgs(List<Mensaje> msgs) {
		this.msgs = msgs;
	}
}

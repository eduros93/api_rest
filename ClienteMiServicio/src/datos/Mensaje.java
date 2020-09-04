package datos;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="mensaje")
public class Mensaje {


	private int id;
	private int idAuthor;
	private String msgText;
	private Date msgDate;
	
	public Mensaje(int idAuthor, String msgText, Date msgDate) {
		this.idAuthor = idAuthor;
		this.msgText = msgText;
		this.msgDate = msgDate;
	}
	
	public Mensaje() {}
	
	@SuppressWarnings("deprecation")
	public String print() {
		return "Id: "+id+ " | Autor id: "+idAuthor+ " | Fecha: "
				+msgDate.toLocaleString() + "\nTexto: " + msgText;
	}

	@XmlAttribute(required=false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdAuthor() {
		return idAuthor;
	}

	public void setIdAuthor(int idAuthor) {
		this.idAuthor = idAuthor;
	}

	public String getMsgText() {
		return msgText;
	}

	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}

	public Date getMsgDate() {
		return msgDate;
	}

	public void setMsgDate(Date msgDate) {
		this.msgDate = msgDate;
	}
	
	
	
}

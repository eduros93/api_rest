package datos;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="mensajePrivado")
public class MensajePrivado {

	private int id;
	private int idAuthor;
	private int idReceiver;
	private String msgText;
	private Date msgDate;
	
	public MensajePrivado(int idAuthor, int idReceiver, String msgText, Date msgDate) {
		this.idAuthor = idAuthor;
		this.idReceiver = idReceiver;
		this.msgText = msgText;
		this.msgDate = msgDate;
	}
	
	public MensajePrivado() {}

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

	public int getIdReceiver() {
		return idReceiver;
	}

	public void setIdReceiver(int idReceiver) {
		this.idReceiver = idReceiver;
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

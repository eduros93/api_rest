package datos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "usuarios")
@XmlAccessorType (XmlAccessType.FIELD)
public class Usuarios {
	
	@XmlElement(name = "usuario")
	private List<Usuario> users = new ArrayList<Usuario>();
	
	public Usuarios() {}

	public List<Usuario> getUsers() {
		return users;
	}

	public void setUsers(List<Usuario> users) {
		this.users = users;
	}
}

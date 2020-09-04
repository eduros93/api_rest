package recursos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.naming.NamingContext;

import datos.*;

@Path("/amigos")
public class AmigosRecurso {

	@Context
	private UriInfo uriInfo;	
	
	private DataSource ds;
	private Connection conn;

	public AmigosRecurso() {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			NamingContext envCtx = (NamingContext) ctx.lookup("java:comp/env");

			ds = (DataSource) envCtx.lookup("jdbc/MiServicio");
			conn = ds.getConnection();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response addAmigo(
			Amigo amigo,
			@QueryParam("auth-user-id") int id) {
		try {
			int amigoId ;
			if (id == amigo.getIdSmall()) amigoId = amigo.getIdBig();
			else if (id == amigo.getIdBig()) amigoId = amigo.getIdSmall();
			else{
				return Response.status(Response.Status.FORBIDDEN)
						.entity("No se puede crear una amistad entre dos usuarios "
								+ "ajenos al autor de la petición de amistad")
						.build();
			}
			if (amigoId == id) {
				return Response.status(Response.Status.FORBIDDEN)
						.entity("No puedes entablar amistad contigo mismo").build();
			}
			String sql = "INSERT INTO Amigos(userIdSmall, userIdBig) VALUES (?,?);";
			PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, amigo.getIdSmall());
			ps.setInt(2, amigo.getIdBig());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				amigo.setId(rs.getInt(1));
				String location = uriInfo.getAbsolutePath() + "/" + amigo.getId();
				return Response.status(Response.Status.CREATED)
						.header("Location", location).header("Content-Location", 
								location).build();
			} else {
				return Response.status(Response.Status.CONFLICT)
						.entity("La amistad ya existe").build();
				
			}
		} catch (SQLException e){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error al crear la amistad en la BBDD").build();			
		}
	}
	
	@DELETE
	@Path("{amistad_id}")
	public Response deleteAmigo(
			@PathParam("amistad_id") int amistadId,
			@QueryParam("auth-user-id") int userId) {
		try {	
			String sql = "DELETE FROM Amigos WHERE id = ? AND "
					+ "(userIdSmall = ? OR userIdBig = ?);";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, amistadId);
			ps.setInt(2, userId);
			ps.setInt(3, userId);
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 1) {
				return Response.status(Response.Status.NO_CONTENT).build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Error al eliminar el mensaje de la BBDD").build();
			}
		} catch (SQLException e){
			return Response.status(Response.Status.NOT_FOUND)
					.entity("No existe amistad con tal id que involucre "
							+ "al usuario autor de la petición").build();			
		}
	}	
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getAmigos(
			@QueryParam("auth-user-id") int id,
			@QueryParam("patron") @DefaultValue("") String patron,
			@QueryParam("inicio") @DefaultValue("0") int inicio,
			@QueryParam("maximo") @DefaultValue("20") int maximo) {
		try {					

			String sql = "SELECT * FROM Usuarios" + 
					      " WHERE id in (SELECT userIdSmall FROM Amigos WHERE userIdBig = ?)" + 
					        " OR id in (SELECT userIdBig FROM Amigos WHERE userIdSmall = ?);";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.setInt(2, id);
			ResultSet rs = ps.executeQuery();
			Usuario user;
			List<Usuario> users = new ArrayList<Usuario>();
			int i = 0;
			while(rs.next() && users.size() < maximo) {
				String nombre = rs.getString("nombre");
				if (i++ >= inicio && nombre.contains(patron)) {
					user = new Usuario(
							nombre,
							rs.getString("genero"),
							rs.getString("estadoCivil"),
							rs.getString("poblacionDeOrigen")
					);
					user.setId(rs.getInt("id"));
					users.add(user);
				}
			}
			if (users.size() > 0) {
				Usuarios entidad = new Usuarios();
				entidad.setUsers(users);
				return Response.status(Response.Status.OK).entity(entidad).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("No existen usuarios que "
						+ "cumplan los requisitos especificados en la query").build();
			}
		} catch (SQLException e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error al acceder a la BBDD").build();
		}
	}
	
	@GET
	@Path("ultimos-mensajes")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getUltimosMensajes(
			@QueryParam("auth-user-id") int authUserId,
			@QueryParam("patron") @DefaultValue("") String patron,
			@QueryParam("inicio") @DefaultValue("0") int inicio,
			@QueryParam("maximo") @DefaultValue("10") int maximo) {
		
		try {
			String sql = "SELECT * FROM Mensajes WHERE idAuthor in " + 
					       "(SELECT id FROM Usuarios " + 
					         "WHERE id in (SELECT userIdSmall FROM Amigos WHERE userIdBig = ?) " + 
					         "OR id in (SELECT userIdBig FROM Amigos WHERE userIdSmall = ?)) " + 
					       "ORDER BY msgDate ASC;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, authUserId);
			ps.setInt(2, authUserId);
			ResultSet rs = ps.executeQuery();
			Mensaje msg;
			List<Mensaje> msgs = new ArrayList<Mensaje>();
			int i = 0;
			while(rs.next() && msgs.size() < maximo) {
				String msgText = rs.getString("msgText");
				if (i++ >= inicio && msgText.contains(patron)) {
					msg = new Mensaje(
							rs.getInt("idAuthor"),
							msgText,
							rs.getTimestamp("msgDate")
					);
					msg.setId(rs.getInt("id"));
					msgs.add(msg);
				}				
			}			
			if (msgs.size() > 0) {
				Mensajes entidad = new Mensajes();
				entidad.setMsgs(msgs);
				return Response.status(Response.Status.OK).entity(entidad).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("No existen mensajes que "
						+ "cumplan los requisitos especificados en la query").build();
			}
		} catch (SQLException e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error al acceder a la BBDD").build();
		}
	}
	
}

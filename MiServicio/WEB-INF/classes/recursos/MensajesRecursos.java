package recursos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.naming.NamingContext;

import datos.*;

@Path("mensajes")
public class MensajesRecursos {

	@Context
	private UriInfo uriInfo;	
	
	private DataSource ds;
	private Connection conn;

	public MensajesRecursos() {
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
	public Response publishMsg(
			Mensaje msg,
			@QueryParam("auth-user-id") int id) {
		try {
			if (id != msg.getIdAuthor()) {
				return Response.status(Response.Status.FORBIDDEN)
						.entity("No se puede publicar mensajes en las páginas de otros").build();
			}
			String sql = "INSERT INTO Mensajes(idAuthor, msgText, MsgDate) VALUES (?,?,?);";
			PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, msg.getIdAuthor());
			ps.setString(2, msg.getMsgText());
			ps.setTimestamp(3, new Timestamp(msg.getMsgDate().getTime()));
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				msg.setId(rs.getInt(1));
				String location = uriInfo.getAbsolutePath() + "/" + msg.getId();
				return Response.status(Response.Status.CREATED)
						.header("Location", location).header("Content-Location", location).build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Error al crear el nuevo mensaje en la BBDD").build();
			}
		} catch (SQLException e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error al crear el nuevo mensaje en la BBDD").build();
		}
	}
	

	@DELETE
	@Path("{msg_id}")
	public Response deleteMsg(
			@PathParam("msg_id") int msgId,
			@QueryParam("auth-user-id") int userId) {
		try {	
			String sql = "SELECT idAuthor FROM Mensajes WHERE id = ?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, msgId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt("idAuthor") == userId) {
					sql = "DELETE FROM Mensajes WHERE id = ?;";
					ps = conn.prepareStatement(sql);
					ps.setInt(1, msgId);
					int affectedRows = ps.executeUpdate();
					if (affectedRows == 1) {
						return Response.status(Response.Status.NO_CONTENT).build();
					} else {
						return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
								.entity("Error al eliminar el mensaje de la BBDD").build();
					}
					
				} else {
					return Response.status(Response.Status.FORBIDDEN)
							.entity("No se puede eliminar mensajes de otros usuarios").build();
				}
			} else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("No existe un mensaje con tal id").build();
			}
		} catch (SQLException e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error al eliminar el mensaje de la BBDD").build();
		}
	}
	
	@PUT
	@Path("{msg_id}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response editMsg(
			Mensaje msg,
			@PathParam("msg_id") int msgId,
			@QueryParam("auth-user-id") int userId) {
		try {	
			if (msg.getIdAuthor() != userId) {
				return Response.status(Response.Status.FORBIDDEN)
						.entity("No se puede editar un mensaje propio y cambiar su autor "
								+ "para que aparezca en la página de otro usuario").build();
			}
			String sql = "SELECT idAuthor FROM Mensajes WHERE id = ?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, msgId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt("idAuthor") == userId) {
					sql = "UPDATE Mensajes "
							+ "SET idAuthor = ?, msgText = ?, msgDate = ? "
							+ "WHERE id = ?;";
					ps = conn.prepareStatement(sql);
					ps.setInt(1, msg.getIdAuthor());
					ps.setString(2, msg.getMsgText());
					ps.setTimestamp(3, new Timestamp(msg.getMsgDate().getTime()));
					ps.setInt(4, msgId);
					int affectedRows = ps.executeUpdate();
					if (affectedRows == 1) {
						return Response.status(Response.Status.NO_CONTENT).build();
					} else {
						return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
								.entity("Error al editar el mensaje de la BBDD").build();
					}
					
				} else {
					return Response.status(Response.Status.FORBIDDEN)
							.entity("No se puede editar mensajes de otros usuarios").build();
				}
			} else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("No existe un mensaje con tal id").build();
			}
		} catch (SQLException e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error al editar el mensaje de la BBDD").build();
		}
	}
	
	
	
	
}

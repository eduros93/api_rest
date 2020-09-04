package recursos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

@Path("/usuarios")
public class UsuariosRecurso {
	
	@Context
	private UriInfo uriInfo;	
	
	private DataSource ds;
	private Connection conn;

	public UsuariosRecurso() {
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
	public Response createUser(Usuario user) {
		try {
			String sql = "INSERT INTO Usuarios(nombre,genero,estadoCivil,poblacionDeOrigen)"+
							" VALUES (?,?,?,?);";
			PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, user.getNombre());
			ps.setString(2, user.getGenero());
			ps.setString(3, user.getEstadoCivil());
			ps.setString(4, user.getPoblacionDeOrigen());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				user.setId(rs.getInt(1));
				String location = uriInfo.getAbsolutePath() + "/" + user.getId();
				return Response.status(Response.Status.CREATED)
						.header("Location", location).header("Content-Location", location).build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Error al crear el nuevo usuario en la BBDD").build();
			}
		} catch (SQLException e){
			return Response.status(Response.Status.FORBIDDEN)
					.entity("Ya existe un usuario con ese nombre en la BBDD").build();
			
		}
	}
	
	@GET
	@Path("{user_id}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getUsuario(
			@PathParam("user_id") int id) {
		try {
			String sql = "SELECT * FROM Usuarios where id = ?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			Usuario usuario;
			if (rs.next()) {
				usuario = new Usuario(
						rs.getString("nombre"),
						rs.getString("genero"),
						rs.getString("estadoCivil"),
						rs.getString("poblacionDeOrigen")
					);
				usuario.setId(rs.getInt("id"));
				return Response.status(Response.Status.ACCEPTED).entity(usuario).build();
			}
			else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Usuario no existe en la BBDD").build();
			}
		} catch (SQLException e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error de acceso a la BBDD").build();
		}
	}
	

	@PUT
	@Path("{user_id}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response editUser(
			Usuario user, 
			@PathParam("user_id") int userId,
			@QueryParam("auth-user-id") int authUserId) {
		try {			
			if ( !(userId == authUserId) ) {
				return Response.status(Response.Status.FORBIDDEN).entity("Un usuario no tiene "
						+ "permiso para editar el perfil de otro usuario").build();
			}
			String sql = "UPDATE Usuarios " + 
					"SET genero = ?, estadoCivil = ?, poblacionDeorigen = ? " + 
					"WHERE id = ?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, user.getGenero());
			ps.setString(2, user.getEstadoCivil());
			ps.setString(3, user.getPoblacionDeOrigen());
			ps.setInt(4, user.getId());						
			int affectedRows = ps.executeUpdate();

			if (affectedRows == 1) {
				return Response.status(Response.Status.NO_CONTENT).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Usuario no existe en la BBDD").build();
			}
		} catch (SQLException e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error al actualizar el usuario en la BBDD").build();
		}
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getUsuarios(
			@QueryParam("patron") @DefaultValue("") String patron,
			@QueryParam("inicio") @DefaultValue("0") int inicio,
			@QueryParam("maximo") @DefaultValue("20") int maximo) {
		try {
			String sql = "SELECT * FROM Usuarios ORDER BY id ASC;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();					
			
			Usuario user;
			List<Usuario> users = new ArrayList<Usuario>();
			Usuarios usuarios = new Usuarios();
			int i = 0;
			while (rs.next() && users.size() < maximo) {				
				String name = rs.getString("nombre");
				if (name.contains(patron) && i++ >= inicio){
					user = new Usuario(
						name,
						rs.getString("genero"),
						rs.getString("estadoCivil"),
						rs.getString("poblacionDeOrigen")						
					);
					user.setId(rs.getInt("id"));
					users.add(user);
				}
			}
			usuarios.setUsers(users);
			if (users.size() > 0) {
				return Response.status(Response.Status.OK).entity(usuarios).build();
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
	@Path("{user_id}/mensajes")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getMensajes(
			@PathParam("user_id") int id,
			@QueryParam("auth-user-id") @DefaultValue("-1") int authUserId,
			@QueryParam("desde") @DefaultValue("2000-01-01 00:00:01") String desdeStr,
			@QueryParam("hasta") @DefaultValue("2100-01-01 00:00:01") String hastaStr,
			@QueryParam("inicio") @DefaultValue("0") int inicio,
			@QueryParam("maximo") @DefaultValue("10") int maximo) {
		try {
			
			// Añadir la hora, minuto y segundo si no se ha dado
			if (desdeStr.length() == 10) {
				desdeStr = desdeStr + " 00:00:01";
			}
			if (hastaStr.length() == 10) {
				hastaStr = hastaStr + " 00:00:01";
			}
			if (id != authUserId) {
				// Esta pidiendo mensajes de otro usuario, hay que ver si son amigos
				int idSmall = Math.min(id, authUserId);			
				int idBig = Math.max(id, authUserId);
				String sql = "SELECT * FROM Amigos WHERE userIdSmall = ? AND userIdBig = ?;";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, idSmall);
				ps.setInt(2, idBig);
				ResultSet rs = ps.executeQuery();
				if ( ! rs.next()) {
					// NO son amigos
					return Response.status(Response.Status.FORBIDDEN)
							.entity("No puedes ver mensajes de usuarios no amigos").build();	
				}				
			}
			Timestamp desde = Timestamp.valueOf(desdeStr); 
			Timestamp hasta = Timestamp.valueOf(hastaStr); 
			
			String sql = "SELECT * FROM Mensajes "
					+ "WHERE idAuthor = ? AND msgDate >= ? AND msgDate <= ? "
					+ "ORDER BY msgDate ASC;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.setTimestamp(2, desde);
			ps.setTimestamp(3, hasta); 
			ResultSet rs = ps.executeQuery();			
			
			Mensajes mensajes = new Mensajes();
			Mensaje msg;
			List<Mensaje> msgs = new ArrayList<Mensaje>();
			int i = 0;
			while (rs.next() && msgs.size() < maximo) {				
				if (i++ >= inicio){
					msg = new Mensaje(
							rs.getInt("idAuthor"),
							rs.getString("msgText"),
							rs.getTimestamp("msgDate")						
					);
					msg.setId(rs.getInt("id"));
					msgs.add(msg);
				}
			}
			mensajes.setMsgs(msgs);;
			if (msgs.size() > 0) {
				return Response.status(Response.Status.OK).entity(mensajes).build();
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
	
	@POST
	@Path("{user_id}/mensajes-privados")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response sendPrivMsg(
			MensajePrivado msg,
			@PathParam("user_id") int targetUserId,
			@QueryParam("auth-user-id") int authUserId) {
		
		try {
			int idSmall = Math.min(targetUserId, authUserId);			
			int idBig = Math.max(targetUserId, authUserId);
			String sql = "SELECT * FROM Amigos WHERE userIdSmall = ? AND userIdBig = ?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, idSmall);
			ps.setInt(2, idBig);
			ResultSet rs = ps.executeQuery();
			if ( ! rs.next()) {
				// NO son amigos
				return Response.status(Response.Status.FORBIDDEN)
						.entity("No puedes enviar un mensaje privado si el usuario "
								+ "no existe o no es tu amigo").build();	
			}
			sql = "INSERT INTO MsgsPrivados(idAuthor,idReceiver,msgText,msgDate)"+
							" VALUES (?,?,?,?);";
			ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, msg.getIdAuthor());
			ps.setInt(2, msg.getIdReceiver());
			ps.setString(3, msg.getMsgText());
			ps.setTimestamp(4, new Timestamp(msg.getMsgDate().getTime()));
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				msg.setId(rs.getInt(1));
				String location = uriInfo.getBaseUri() + "mensajes-privados/" + msg.getId();
				return Response.status(Response.Status.CREATED)
						.header("Location", location).header("Content-Location", location).build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Error al crear el mensaje privado en la BBDD").build();
			}
		} catch (SQLException e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error al crear el mensaje privado en la BBDD").build();
		}
	}
	
	@DELETE
	@Path("{user_id}")
	public Response deleteUser(
			@PathParam("user_id") int userId,
			@QueryParam("auth-user-id") int authUserId) {
		try {
			if (userId != authUserId) {
				return Response.status(Response.Status.FORBIDDEN)
						.entity("No puedes eliminar la cuenta de otro usuario").build();	
			}
			String sql = "DELETE FROM Usuarios WHERE id = ?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);			
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 1) {
				return Response.status(Response.Status.NO_CONTENT).build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Error al eliminar el mensaje de la BBDD").build();
			}
		} catch (SQLException e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error al eliminar el mensaje de la BBDD").build();
		}
	}
	
	@GET
	@Path("{user_id}/snapshot-movil")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getMensajes(
			@PathParam("user_id") int userId,
			@QueryParam("auth-user-id") int authUserId) {
		
		try {
			if (userId != authUserId) {
				return Response.status(Response.Status.FORBIDDEN)
						.entity("Operación permitida solo para la propia cuenta del "
								+ "autor de la petición").build();
			}
			SnapshotMovil snapshot = new SnapshotMovil();
			// Datos basicos
			String sql = "SELECT * FROM Usuarios WHERE id = ?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if ( ! rs.next()) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Error al acceder a la BBDD").build();
			} else {
				Usuario user = new Usuario(
						rs.getString("nombre"),
						rs.getString("genero"),
						rs.getString("estadoCivil"),
						rs.getString("poblacionDeOrigen")						
				);
				user.setId(rs.getInt("id"));
				snapshot.setDatosBasicos(user);
			}
			// Ultimo mensaje propio
			sql = "SELECT * FROM Mensajes WHERE idAuthor = ? ORDER BY msgDate DESC LIMIT 1;";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			rs = ps.executeQuery();
			Mensaje msg;
			if (rs.next()) {
				msg = new Mensaje(
						rs.getInt("idAuthor"),
						rs.getString("msgText"),
						rs.getTimestamp("msgDate")				
				);
				msg.setId(rs.getInt("id"));
				snapshot.setUltimoMensaje(msg);
			}
			// Numero de amigos
			sql = "SELECT count(*) FROM Usuarios " + 
					"WHERE id in (SELECT userIdSmall FROM Amigos WHERE userIdBig = ?) " + 
					"OR id in (SELECT userIdBig FROM Amigos WHERE userIdSmall = ?);";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, userId);
			rs = ps.executeQuery();
			rs.next();
			snapshot.setNumAmigos(rs.getInt(1));
			// Ultimos 10 mensajes de amigos
			sql = "SELECT * FROM Mensajes WHERE idAuthor in " + 
				       "(SELECT id FROM Usuarios " + 
				         "WHERE id in (SELECT userIdSmall FROM Amigos WHERE userIdBig = ?) " + 
				         "OR id in (SELECT userIdBig FROM Amigos WHERE userIdSmall = ?)) " + 
				       "ORDER BY msgDate DESC LIMIT 10;";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, authUserId);
			ps.setInt(2, authUserId);
			rs = ps.executeQuery();
			List<Mensaje> msgs = new ArrayList<Mensaje>();
			while(rs.next()) {
				msg = new Mensaje(
						rs.getInt("idAuthor"),
						rs.getString("msgText"),
						rs.getTimestamp("msgDate")
				);
				msg.setId(rs.getInt("id"));
				msgs.add(msg);				
			}
			Mensajes mensajes = new Mensajes();
			mensajes.setMsgs(msgs);
			snapshot.setUltimos10MsgsDeAmigos(mensajes);
			//
			return Response.status(Response.Status.OK).entity(snapshot).build();
		} catch (SQLException e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error al acceder a la BBDD").build();
		}
	}
	
	
}






















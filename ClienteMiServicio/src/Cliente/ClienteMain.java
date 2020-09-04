package Cliente;

import java.util.Date;
import java.util.Scanner;
import java.net.URI;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import datos.*;

public class ClienteMain {	

	public static void main(String[] args) throws Exception{

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target = client.target(getBaseURI());
        
        //////////
        
        Scanner in = new Scanner(System.in);
        int authUserId = logIn(in);
        int nextAction = -1;
        while(nextAction != 0) {
        	nextAction = mainMenu(in);
        	switch(nextAction) {
        		case 0: System.out.println("Hasta la próxima!\nEjecución detenida.");
        				return;
        		case 1: crearUsuario(in, target);
        				break;
        		case 2: verDatosBasicos(in, target);
        				break;
        		case 3: cambiarDatosBasicos(in, target, authUserId);
        				break;
        		case 4: verListaDeUsuarios(in, target);
						break;  
        		case 5: publicarMensaje(in, target, authUserId);
						break; 
        		case 6: borrarMensaje(in, target, authUserId);
						break; 
        		case 7: editarMensaje(in, target, authUserId);
						break; 
        		case 8: verListaMensajes(in, target, authUserId);
						break; 
        		case 9: crearAmistad(in, target, authUserId);
						break; 	
        		case 10: borrarAmigo(in, target, authUserId);
						break; 
        		case 11: verListaAmigos(in, target, authUserId);
						break; 
        		case 12: crearMsgPrivado(in, target, authUserId);
						break; 
        		case 13: boolean exit = borrarPerfil(in, target, authUserId);
        				 if (exit) {
        					 System.out.println("Ejecución detenida.");
             				 return;	
        				 }
        				 break; 
        		case 14: verUltimosMensajesAmigos(in, target, authUserId);
						break;   
        		case 15: verSnapshotMovil(in, target, authUserId);
						break;   						
        		default: System.out.println("Respuesta no válida, por favor "
        				+ "elija otra vez"); 
        		
        	}
        	System.out.println("\ntecla <ENTER> para continuar...");
        	in.nextLine();
        	System.out.println("-----------------------\n");
        }
        System.out.println("Hasta la próxima!\nEjecución detenida.");
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/MiServicio/api/v1").build();
    }
    
    private static int logIn(Scanner in) {
    	System.out.println("Bienvenido, indique su identificador de usuario "
        		+ "(autenticación no implementada):");
        String s = in.nextLine();
        int res = Integer.parseInt(s);
        return res;
    }
	
    private static int mainMenu(Scanner in) {
    	System.out.println("Elija una de las siguientes acciones tecleando "
    			+ "el número asociado a la acción:\n"
    			+ "\t[1]: Añadir un nuevo usuario a la red.\n"
    			+ "\t[2]: Ver los datos básicos de un usuario.\n"
    			+ "\t[3]: Cambiar datos básicos de tu perfil.\n"
    			+ "\t[4]: Obtener una lista de los usuarios existentes.\n"
    			+ "\t[5]: Publicar un nuevo mensaje en tu página personal.\n"
    			+ "\t[6]: Eliminar un mensaje propio.\n"
    			+ "\t[7]: Editar un mensaje propio.\n"
    			+ "\t[8]: Ver todos los mensajes de tu página personal o la de un amigo.\n"
    			+ "\t[9]: Añadir un nuevo amigo.\n"
    			+ "\t[10]: Eliminar un amigo.\n"
    			+ "\t[11]: Obtener una lista de todos tus amigos.\n"
    			+ "\t[12]: Enviar un mensaje personal a otro usuario.\n"
    			+ "\t[13]: Borrar tu perfil de la red social.\n"
    			+ "\t[14]: Obtener una lista con los últimos mensajes de las "
    			+ "páginas de tus amigos ordenados por fecha.\n"
    			+ "\t[15]: Ver una snapshot de tu perfil: Datos básicos, "
    			+ "tu último mensaje, número de amigos y 10 últimos mensajes "
    			+ "de tus amigos.\n"
    			+ "\t[0]: Terminar la sesión.");   
	String s = in.nextLine();
	int res = Integer.parseInt(s);
    return res;
    }
    
    public static void crearUsuario(Scanner in, WebTarget target) {
    	System.out.println("Indique el nombre del usuario: ");
    	String nombre = in.nextLine();
    	System.out.println("Indique el genero del usuario [hombre/mujer]: ");
    	String genero = in.nextLine();
    	System.out.println("Indique el estado civil del usuario: ");
    	String estadoC = in.nextLine();
    	System.out.println("Indique la poblacion de origen del usuario: ");
    	String poblacion = in.nextLine();
    	Usuario user = new Usuario(nombre,genero,estadoC,poblacion);
    	Response response = target.path("usuarios")
    	        .request()
    	        .post(Entity.xml(user));
    	switch (response.getStatusInfo().getStatusCode()) {
	    	case 201: String location = response.getLocation().toString();
	    			  System.out.println("Usuario creado con éxito.\nLocation: "+location);
	    			  break;
	    	case 403: System.out.println("Error: ya existe un usuario con ese nombre.");
	    			  break;
	    	default: System.out.println("No se ha podido servir la petición.");
    	}
    	response.close();
    }
    
    public static void verDatosBasicos(Scanner in, WebTarget target) {
    	System.out.println("Indique el id del usuario: ");
    	String id = in.nextLine();
    	try {
    		Usuario user = target.path("usuarios/"+id)
        	        .request()
        	        .get(Usuario.class);   	
        	System.out.println("\n"+ user.print());
    	} catch (NotFoundException e) {
    		System.out.println("Error: el usuario no existe.");
    	}
    	
    }
    
    public static void cambiarDatosBasicos(Scanner in, WebTarget target,
    		int authUserId) {
    	Usuario user = target.path("usuarios/"+authUserId)
    	        .request()
    	        .get(Usuario.class); 
    	System.out.println("Indique el nuevo genero [hombre/mujer]: ");
    	String genero = in.nextLine();
    	System.out.println("Indique el nuevo estado civil: ");
    	String estadoC = in.nextLine();
    	System.out.println("Indique la nueva poblacion de origen: ");
    	String poblacion = in.nextLine();
    	user.setGenero(genero);
    	user.setEstadoCivil(estadoC);
    	user.setPoblacionDeOrigen(poblacion);
    	Response response = target.path("usuarios/"+authUserId)
    			.queryParam("auth-user-id", authUserId)
    	        .request()
    	        .put(Entity.xml(user));
    	switch (response.getStatusInfo().getStatusCode()) {
    	case 204: System.out.println("Usuario actualizado con éxito");
    			  break;
    	default: System.out.println("No se ha podido servir la petición.");
	}
    	response.close();
    }
    public static void verListaDeUsuarios(Scanner in, WebTarget target) {
    	System.out.println("Escribe un patrón de texto para filtrar los usuarios "
    			+ "por nombre \nO bien pulsa <ENTER> directamente para continuar: ");
    	String patron = in.nextLine();
    	System.out.println("Escribe el número de la lista a partir del cual "
    			+ "mostrar resultados (paginación): ");
    	String inicio = in.nextLine();
    	System.out.println("Escribe el número máximo de resultados a mostrar "
    			+ "(paginación): ");
    	String maximo = in.nextLine();
    	try {
    		Usuarios users = target.path("usuarios")
        	        .queryParam("patron", patron)
        	        .queryParam("inicio", inicio)
        	        .queryParam("maximo", maximo)
    				.request()
        	        .get(Usuarios.class);   
    		System.out.println("\nResultados:");
    		for(Usuario user : users.getUsers()) {
    			System.out.println(user.print());
    		}        	
    	} catch (NotFoundException e) {
    		System.out.println("Ningún usuario satisface los criterios elegidos.");
    	}
    }
    public static void publicarMensaje(Scanner in, WebTarget target,
    		int authUserId) {
    	System.out.println("Escriba una linea de 350 caracteres máximo "
    			+ "para publicar: ");
    	String msgText = in.nextLine();
    	
    	Mensaje msg = new Mensaje(authUserId, msgText, new Date());
    	Response response = target.path("mensajes")
    			.queryParam("auth-user-id", authUserId)
    	        .request()
    	        .post(Entity.xml(msg));
    	switch (response.getStatusInfo().getStatusCode()) {
	    	case 201: String location = response.getLocation().toString();
	    			  System.out.println("Mensaje publicado con éxito.\nLocation: "+location);
	    			  break;
	    	default: System.out.println("No se ha podido servir la petición.");
    	}
    	response.close();
    }
    public static void borrarMensaje(Scanner in, WebTarget target,
    		int authUserId) {
    	System.out.println("Escribe la id del mensaje a borrar: ");
    	String msgId = in.nextLine();

    	Response response = target.path("mensajes/"+msgId)
    			.queryParam("auth-user-id", authUserId)
    	        .request()
    	        .delete();
    	switch (response.getStatusInfo().getStatusCode()) {
	    	case 204: System.out.println("Mensaje eliminado con éxito.");
	    			  break;
	    	case 403: System.out.println("No puedes eliminar mensajes de "
	    			+ "otros usuarios.");
			  break;	
	    	case 404: System.out.println("El mensaje con id="+msgId+" no existe.");
			  break;	  
	    	default: System.out.println("No se ha podido servir la petición.");
    	}
    	response.close();
    }
    public static void editarMensaje(Scanner in, WebTarget target,
    		int authUserId) {
    	
    	System.out.println("Indique la id del mensaje a editar: ");
    	String msgId = in.nextLine();    	
		System.out.println("Indique el cuerpo del mensaje (una linea "
    			+ "de máximo 350 carcteres): ");
    	String msgText = in.nextLine();
    	Mensaje msg = new Mensaje(authUserId,msgText,new Date());
    	Response response = target.path("mensajes/"+msgId)
    			.queryParam("auth-user-id", authUserId)
    	        .request()
    	        .put(Entity.xml(msg));
    	switch (response.getStatusInfo().getStatusCode()) {
    	case 204: System.out.println("Mensaje editado con éxito");
    			  break;
    	case 403: System.out.println("Error: No puedes editar mensajes de "
    			+ "otros usuarios");
  		    	  break;
    	case 404: System.out.println("Error: El mensaje con id="+msgId+
    			" no existe");
		    	  break;		  
    	default: System.out.println("No se ha podido servir la petición.");
		}
    	response.close();
    }
    public static void verListaMensajes(Scanner in, WebTarget target,
    		int authUserId) {
    	System.out.println("Escribe la id del usuario a consultar: ");
    	String targetUserId = in.nextLine();
    	System.out.println("Escribe el número de la lista a partir del cual "
    			+ "mostrar resultados (paginación): ");
    	String inicio = in.nextLine();
    	System.out.println("Escribe el número máximo de resultados a mostrar "
    			+ "(paginación): ");
    	String maximo = in.nextLine();
    	System.out.println("Escribe la fecha de publicación más antigua a comprobar "
    			+ "\nFormato: yyyy-MM-dd\nFormato alternativo: yyyy-MM-dd HH:mm:ss"
    			+ "\n(pulsa ENTER para ver desde el más antiguo)");
    	String desde = in.nextLine();
    	if (desde.length() == 0) desde = "2000-01-01";
    	System.out.println("Escribe la fecha de publicación más reciente a comprobar "
    			+ "\nFormato: yyyy-MM-dd\nFormato alternativo: yyyy-MM-dd HH:mm:ss" 
    			+"\n(pulsa ENTER para ver hasta el más reciente)");
    	String hasta = in.nextLine();
    	if (hasta.length() == 0) hasta = "2099-01-01";
    	try {
    		Mensajes msgs = target.path("usuarios/"+targetUserId+"/mensajes")
    				.queryParam("auth-user-id", authUserId)
        	        .queryParam("inicio", inicio)
        	        .queryParam("maximo", maximo)
        	        .queryParam("desde", desde)
        	        .queryParam("hasta", hasta)
    				.request()
        	        .get(Mensajes.class);   
    		System.out.println("\nResultados:");
    		for(Mensaje msg : msgs.getMsgs()) {
    			System.out.println(msg.print()+"\n");
    		}        	
    	} catch (NotFoundException e) {
    		System.out.println("Ningún usuario satisface los criterios elegidos.");
    	} catch (ForbiddenException e) {
    		System.out.println("Error: No puedes ver los mensajes de un usuario "
    				+ "que no es tu amigo.");
    	}
    }
    public static void crearAmistad(Scanner in, WebTarget target,
    		int authUserId) {
    	System.out.println("Escribe la id del usuario con el que "
    			+ "entablar amistad:");
    	String s = in.nextLine();
    	int userId = Integer.parseInt(s);
    	int idSmall = Math.min(userId, authUserId);
    	int idBig = Math.max(userId, authUserId);
    	Amigo amigo = new Amigo(idSmall, idBig);
    	Response response = target.path("amigos")
    			.queryParam("auth-user-id", authUserId)
    	        .request()
    	        .post(Entity.xml(amigo));
    	switch (response.getStatusInfo().getStatusCode()) {
	    	case 201: String location = response.getLocation().toString();
	    			  System.out.println("Amistad creada con éxito.\nLocation: "+
	    					  location);
	    			  break;
	    	case 400: System.out.println("Error, el usuario con id="+userId+
	    			  " no existe");
					  break;	
	    	case 409: System.out.println("Error, la amistad con usuario "
	    			+ "id="+userId+" ya existe");
					  break;					  
	    	default: System.out.println("No se ha podido servir la petición.");
    	}
    	response.close();
    }
    public static void borrarAmigo(Scanner in, WebTarget target,
    		int authUserId) {
    	System.out.println("Escribe la id de la amistad a borrar: ");
    	String amistadId = in.nextLine();

    	Response response = target.path("amigos/"+amistadId)
    			.queryParam("auth-user-id", authUserId)
    	        .request()
    	        .delete();
    	switch (response.getStatusInfo().getStatusCode()) {
	    	case 204: System.out.println("Amistad eliminada con éxito.");
	    			  break;
	    	case 404: System.out.println("Error: La amistad no existe o es una "
	    			+ "amistad entre dos usuarios distintos al tuyo.");
			  		  break;	  
	    	default: System.out.println("No se ha podido servir la petición.");
    	}
    	response.close();
    }
    public static void verListaAmigos(Scanner in, WebTarget target,
    		int authUserId) {
    	System.out.println("Escribe un patrón de texto para filtrar los amigos "
    			+ "por nombre \nO bien pulsa <ENTER> directamente para continuar: ");
    	String patron = in.nextLine();
    	System.out.println("Escribe el número de la lista a partir del cual "
    			+ "mostrar resultados (paginación): ");
    	String inicio = in.nextLine();
    	System.out.println("Escribe el número máximo de resultados a mostrar "
    			+ "(paginación): ");
    	String maximo = in.nextLine();
    	try {
    		Usuarios users = target.path("amigos")
    				.queryParam("auth-user-id", authUserId)
    				.queryParam("patron", patron)
        	        .queryParam("inicio", inicio)
        	        .queryParam("maximo", maximo)
    				.request()
        	        .get(Usuarios.class);   
    		System.out.println("\nResultados:");
    		for(Usuario user : users.getUsers()) {
    			System.out.println(user.print());
    		}        	
    	} catch (NotFoundException e) {
    		System.out.println("Ningún usuario satisface los criterios elegidos.");
    	}
    }
    public static void crearMsgPrivado(Scanner in, WebTarget target,
    		int authUserId) {
    	System.out.println("Escribe la id del usuario al que mandar el privado:");
    	String idStr = in.nextLine();
    	int userId = Integer.parseInt(idStr);
    	System.out.println("Escriba una linea de 350 caracteres máximo "
    			+ "para mandar al usuario: ");
    	String msgText = in.nextLine();
    	MensajePrivado priv = new MensajePrivado(authUserId,userId,msgText,new Date());
    	Response response = target.path("usuarios/"+idStr+"/mensajes-privados")
    			.queryParam("auth-user-id", authUserId)
    	        .request()
    	        .post(Entity.xml(priv));
    	switch (response.getStatusInfo().getStatusCode()) {
	    	case 201: String location = response.getLocation().toString();
	    			  System.out.println("Mensaje privado mandado con éxito.\nLocation: "+
	    					  location);
	    			  break;
	    	case 403: System.out.println("Error, el usuario con id="+userId+
	    			  " no existe o no es amigo tuyo.");
					  break;					  
	    	default: System.out.println("No se ha podido servir la petición.");
    	}
    	response.close();
    }
    public static boolean borrarPerfil(Scanner in, WebTarget target,
    		int authUserId) {
    	while (true) {
	    	System.out.println("¿Estás segur@ de querer borrar tu perfil? Responder [si/no]:");
	    	String res = in.nextLine();
	    	if (res.equals("si")) {
	        	Response response = target.path("usuarios/"+authUserId)
	        			.queryParam("auth-user-id", authUserId)
	        	        .request()
	        	        .delete();
	        	switch (response.getStatusInfo().getStatusCode()) {
	    	    	case 204: System.out.println("Perfil eliminado con éxito. Buena suerte!");
	    	    			  break;	  
	    	    	default: System.out.println("No se ha podido servir la petición.");
	        	}
	        	response.close();
	        	return true;
	    	} else if (res.equals("no")){
	    		return false;
	    	} else {
	    		System.out.println("Respuesta no válida, por favor vuelva a elegir:");
	    	}
    	}
    }
    public static void verUltimosMensajesAmigos(Scanner in, WebTarget target,
    		int authUserId) {
    	System.out.println("Escribe un patrón de texto para filtrar los mensajes "
    			+ "por su contenido \nO bien pulsa <ENTER> directamente para continuar: ");
    	String patron = in.nextLine();
    	System.out.println("Escribe el número de la lista a partir del cual "
    			+ "mostrar resultados (paginación): ");
    	String inicio = in.nextLine();
    	System.out.println("Escribe el número máximo de resultados a mostrar "
    			+ "(paginación): ");
    	String maximo = in.nextLine();
    	try {
    		Mensajes msgs = target.path("amigos/ultimos-mensajes")
    				.queryParam("auth-user-id", authUserId)
    				.queryParam("patron", patron)
    				.queryParam("inicio", inicio)
        	        .queryParam("maximo", maximo)
    				.request()
        	        .get(Mensajes.class);   
    		System.out.println("\nResultados:");
    		for(Mensaje msg : msgs.getMsgs()) {
    			System.out.println(msg.print()+"\n");
    		}        	
    	} catch (NotFoundException e) {
    		System.out.println("Ningún usuario satisface los criterios elegidos.");
    	} catch (ForbiddenException e) {
    		System.out.println("Error: No puedes ver los mensajes de un usuario "
    				+ "que no es tu amigo.");
    	}
    }
    public static void verSnapshotMovil(Scanner in, WebTarget target,
    		int authUserId) {
    	try {
    		SnapshotMovil snapshot = target.path("usuarios/"+authUserId+"/snapshot-movil")
    				.queryParam("auth-user-id", authUserId)
        	        .request()
        	        .get(SnapshotMovil.class);   	
        	System.out.println("\n"+ snapshot.print());
    	} catch (NotFoundException e) {
    		System.out.println("No se ha podido servir la petición.");
    	}
    	
    }
    
    
    
    
    
    
    
}












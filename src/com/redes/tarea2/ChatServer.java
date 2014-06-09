package com.redes.tarea2;

import java.net.ServerSocket;
import java.net.Socket;
 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
 
import java.util.Hashtable;
 

public class ChatServer {
    private static int port = 8080; /* port to listen on */
 
  //metodo que comprueba si existe un usuario con el nombre especificado
  	public static boolean existe_usuario(String nombre)
  	{
  		String path_raiz=System.getProperty("user.dir");//obtenemos el path del directorio de trabajo
  		String path_usuario=path_raiz+"\\data\\users\\"+nombre;
  		File directorio = new File(path_usuario);//variable file para verificar directorios
  		if (!directorio.exists()) {
              System.out.println("La ruta " + directorio.getAbsolutePath() + " no existe :(");
              return false;
  		}else {
  			
  			System.out.println("La ruta " + directorio.getAbsolutePath() + " existe :)");
  			return true;
  		}		
  	} 
  	
  	//funcion que retorna el path a la carpeta del usuario(usuario debe existir)
  	String path_usuario(String nombre)
  	{
  		String path=System.getProperty("user.dir")+"\\dataserver\\users\\"+nombre;
  		return path;
  	}
    
  //metodo que crea un usuario
  	static void nuevo_usuario(String nick)
  	{
  		
  		String path_raiz=System.getProperty("user.dir");//obtenemos el path del directorio de trabajo
  		path_raiz=path_raiz+"\\data\\users\\"+nick;
  		File directorio = new File(path_raiz);
  		directorio.mkdir();
  		System.out.println(path_raiz+"\\contactos.pp");
  		File archivo=new File(path_raiz+"\\contactos.pp");
  		
  		try {
  		  // A partir del objeto File creamos el fichero físicamente
  		  if (archivo.createNewFile())
  		    System.out.println("El fichero se ha creado correctamente");
  		  else
  		    System.out.println("No ha podido ser creado el fichero");
  		} catch (IOException ioe) {
  		  ioe.printStackTrace();
  		}
  	}
  
  	//metodo que retorna la lista de contactos de un usuario
  	static String contactos_usuario(String nick)
  	{
  	String path_raiz=System.getProperty("user.dir");//obtenemos el path del directorio de trabajo
  	path_raiz=path_raiz+"\\data\\users\\"+nick+"\\contactos.pp";
  	return leerArchivo(path_raiz);
  	}
  	
  	//retorna el contenido de un archivo como un string
	static String leerArchivo(String path)
	{
		//Creamos un String que va a contener todo el texto del archivo
		String texto="";
		String contactos="";

		try
		{
			//Creamos un archivo FileReader que obtiene lo que tenga el archivo
			FileReader lector=new FileReader(path);
			System.out.println("el path es: "+path);
			//El contenido de lector se guarda en un BufferedReader
			BufferedReader contenido=new BufferedReader(lector);

			//Con el siguiente ciclo extraemos todo el contenido del objeto "contenido" y lo mostramos
			while((texto=contenido.readLine())!=null)
			{
				if(contactos.length()>1)
				contactos=contactos+"\n"+texto;
				else
				contactos=contactos+texto;
			}
			contenido.close();
		}

		//Si se causa un error al leer cae aqui
		catch(Exception e)
		{
			System.out.println("Error al leer");
		}
		return contactos;
	}  	
	
    //metodo que retorna un String con los contactos del usuario contacto separados por espacio
	public static String parsearContactos(String usuario)
	{	
		String contactos=ChatServer.contactos_usuario(usuario);
	    //System.out.println("parseando el texto: "+contactos);
		String listaContactos="";
		String[] tokens = contactos.split("\n");
		String[] subtokens;
		for (int i = 0; i < tokens.length; i++)
		{
		    subtokens=tokens[i].split(" ");
		    //System.out.println("El subtoken es: "+subtokens[0]);
		    if(listaContactos.length()>1)
		    listaContactos=listaContactos+" "+subtokens[0];
		    else
		    listaContactos=listaContactos+subtokens[0];
		    }
		//System.out.println("termine de parsear");
		return listaContactos;
	}
	//Metodo que identifica si existe un chat entre dos usuarios, y de ser asi, retorna el nombre del chat
	static String nombreChatUsuarios(String usuario1, String usuario2)
	{
		String contactos=ChatServer.contactos_usuario(usuario1);
		String[] tokens = contactos.split("\n");
		String[] subtokens;
		for (int i = 0; i < tokens.length; i++)
		{
		    subtokens=tokens[i].split(" ");
		    if(subtokens[0].compareTo(usuario2)==0)
		    return subtokens[1];
		    }
		return null;
		
	}
	
	static boolean actualizarChat(String usuario1,String usuario2,String msg)
	{
		String nombreChat=nombreChatUsuarios(usuario1,usuario2);
		
		String path=System.getProperty("user.dir");//obtenemos el path del directorio de trabajo
	  	path=path+"\\data\\chats\\"+nombreChat+".pp";
		
		//Escritura
		
		try{
		FileWriter w = new FileWriter(path,true);
		BufferedWriter bw = new BufferedWriter(w);
		PrintWriter wr = new PrintWriter(bw); 		
		wr.append("\n"+usuario1+": "+msg); //concatenamos en el archivo sin borrar lo existente
		
		        //ahora cerramos los flujos de canales de datos, al cerrarlos el archivo quedará guardado con información escrita
		
		       //de no hacerlo no se escribirá nada en el archivo
		wr.close();
		bw.close();
		}catch(IOException e){
			return false;
		};
		return true;
	}
	
	//metodo que retorna el chat entre la persona usuario1 y usuario2
	static String chatUsuarios(String usuario1, String usuario2){
		String path=System.getProperty("user.dir");//obtenemos el path del directorio de trabajo
  		String nombreChat=nombreChatUsuarios(usuario1, usuario2);
  		if(nombreChat!=null){ 
		path=path+"\\data\\chats\\"+nombreChat+".pp";
		System.out.println(path);
		return leerArchivo(path);
		}
		else 
			return "";  
			
	}
	
	static boolean crearChat(String usuario1,String usuario2)
	{
		String path_raiz=System.getProperty("user.dir");//obtenemos el path del directorio de trabajo
		String pathU1=path_raiz+"\\data\\users\\"+usuario1+"\\contactos.pp";//path a la lista de contactos del usuario1
		String pathU2=path_raiz+"\\data\\users\\"+usuario2+"\\contactos.pp";//path a la lista de contactos del usuari2
		String pathChat=path_raiz+"\\data\\chats\\"+usuario1+usuario2+".pp";

		
		//Se crea el archibo del chat vacio
		File directorio = new File(path_raiz);
		directorio.mkdir();
		System.out.println("Creando Chat"+pathChat);
		File archivo=new File(pathChat);
		
		try {
			// A partir del objeto File creamos el fichero físicamente
			if (archivo.createNewFile())
			System.out.println("El fichero se ha creado correctamente");
			else
			System.out.println("No ha podido ser creado el fichero");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		//Escritura

		//para los contactos del usuario1
		try{
			FileWriter w1 = new FileWriter(pathU1,true);
			BufferedWriter bw = new BufferedWriter(w1);
			PrintWriter wr = new PrintWriter(bw);   
			wr.append("\n"+usuario2+" "+usuario1+usuario2); //concatenamos en el archivo sin borrar lo existente
			//ahora cerramos los flujos de canales de datos, al cerrarlos el archivo quedará guardado con información escrita
			//de no hacerlo no se escribirá nada en el archivo
			wr.close();
			bw.close();
		}catch(IOException e){
			System.out.println("Error al actualizar la lista de contactos del usuario "+usuario1);
			return false;
		};

		//para los contactos del usuario2
		try{
			FileWriter w2 = new FileWriter(pathU2,true);
			BufferedWriter bw = new BufferedWriter(w2);
			PrintWriter wr = new PrintWriter(bw);   
			wr.append("\n"+usuario1+" "+usuario1+usuario2); //concatenamos en el archivo sin borrar lo existente
			//ahora cerramos los flujos de canales de datos, al cerrarlos el archivo quedará guardado con información escrita
			//de no hacerlo no se escribirá nada en el archivo
			wr.close();
			bw.close();
		}catch(IOException e){
			System.out.println("Error al actualizar la lista de contactos del usuario "+usuario2);
			return false;
		}; 
		return true;
	}
	
    public static void main (String[] args) throws IOException {
    	
    	//ChatServer.nuevo_usuario("seba");
        //System.out.println(ChatServer.parsearContactos("celeste"));
        //System.out.println("chat\n"+chatUsuarios("juanca","celeste"));
        //actualizarChat("celeste", "juanca", "un dos tres probando probando????");
        
        ServerSocket server = null;
        try {
            server = new ServerSocket(port); /* start listening on the port */
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.err.println(e);
            System.exit(1);
        }
 
        Socket client = null;
        while(true) {
            try {
                client = server.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.err.println(e);
                System.exit(1);
            }
            /* start a new thread to handle this client */
            Thread t = new Thread(new ClientConn(client));
            t.start();
        }
    }
}
 
class ChatServerProtocol {
    private String nick; //usuario
    private ClientConn conn;
    private String contactos = "0";
    
    /* a hash table from user nicks to the corresponding connections */
    private static Hashtable<String, ClientConn> nicks = 
        new Hashtable<String, ClientConn>();
 
    private static final String msg_OK = "OK";
    private static final String msg_NICK_IN_USE = "NICK IN USE";
    private static final String msg_SPECIFY_NICK = "SPECIFY NICK";
    private static final String msg_INVALID = "INVALID COMMAND";
    private static final String msg_SEND_FAILED = "FAILED TO SEND";
    //private static final String msg_INVALID_RECEIVER = "NO EXISTE A QUIEN SE LE ENVIA MENSAJE";
    private static final String msg_NICK_TIENE_CONTACTOS = "TIENE CONTACTOS";
    private static final String msg_NICK_EN_ARCHIVO = "NICK EN ARCHIVO";
    private static final String msg_NICK_CREA_CHAT = "NICK CREA CHAT";
    private static final String msg_CREATE_FAILED = "FAILED TO CREATE";
    /**
     * Adds a nick to the hash table 
     * returns false if the nick is already in the table, true otherwise
     */
    private static boolean add_nick(String nick, ClientConn c) {
        if (nicks.containsKey(nick)) {
            return false;
            //mostrar lista contactos
        } else {
            nicks.put(nick, c);
            return true;
            //crear usuario
        }
    }
 
    public ChatServerProtocol(ClientConn c) {
        nick = null;
        conn = c;
    }
 
    private void log(String msg) {
        System.err.println(msg);
    }
 
    public boolean isAuthenticated() {
        return ! (nick == null);
    }
 
    /**
     * Implements the authentication protocol.
     * This consists of checking that the message starts with the NICK command
     * and that the nick following it is not already in use.
     * returns: 
     *  msg_OK if authenticated
     *  msg_NICK_IN_USE if the specified nick is already in use
     *  msg_SPECIFY_NICK if the message does not start with the NICK command 
     */
    private String authenticate(String msg) { //revisa si existe el nick, si no lo esta
        if(msg.startsWith("NICK")) {
            String tryNick = msg.substring(5);
            
            //revisar si tryNick ya existe en ARCHIVO lista de usuarios, si no existe, sigue igual, si existe
            if(!ChatServer.existe_usuario(tryNick)){
            	if(add_nick(tryNick, this.conn)) {
                    log("Nick " + tryNick + " joined.");
                    this.nick = tryNick;
                    ChatServer.nuevo_usuario(tryNick);//agregar usuario a archivo
                    return msg_OK;
                } 
            	else{
            		return msg_NICK_IN_USE;
                }
            }
            else{ //si usuario en archivo
            	if(add_nick(tryNick, this.conn)) {
                    log("Nick " + tryNick + " joined.");
                    this.nick = tryNick;
                    
                } 
            	contactos = ChatServer.parsearContactos(tryNick);
            	
            	return msg_NICK_EN_ARCHIVO + contactos;
            }
        }
        else //mensaje no empieza con NICK o no hay nick
        	return msg_SPECIFY_NICK;
    }
 
    /**
     * Send a message to another user.
     * @recepient contains the recepient's nick
     * @msg contains the message to send
     * return true if the nick is registered in the hash, false otherwise
     */
    private boolean sendMsg(String recipient, String msg) {
        if (nicks.containsKey(recipient)) {
            ClientConn c = nicks.get(recipient);
            c.sendMsg(nick + ": " + msg);
            return true;
        } else {
            return false;
        }
    }
 
    /**
     * Process a message coming from the client
     */
    public String process(String msg) {
    	System.out.println("El mensaje es "+msg);
        if (!isAuthenticated())
            return authenticate(msg);
        	System.out.println("No entre a Autenticated");
        String[] msg_parts = msg.split(" ", 3);
        String msg_type = msg_parts[0];
        System.out.println("El mensaje parseado es "+msg_parts[0]);
        System.out.println("El mensaje parseado es de tamaño "+msg_parts.length);
        
        if(msg_type.equals("MSG")) { //entra a seccion de enviar mensajes
        	System.out.println("dentro de MSG");
        	System.out.println("el parseo es: --" + msg_parts[0] + "- -" + msg_parts[1] + "- -" + msg_parts[2] + "--");
            if(msg_parts.length < 3) return msg_INVALID;
            if(sendMsg(msg_parts[1], msg_parts[2]))
            	{
            	System.out.println("dentro de revision de MSG");
            	ChatServer.chatUsuarios(nick, msg_parts[1]);
            	ChatServer.actualizarChat(nick, msg_parts[1],msg_parts[2]);//guarda en archivo el mensaje msg_parts[2] desde el usuario conectado a msg_parts[1]
            	
            	return msg_OK;
            	}
            else return msg_SEND_FAILED;
        	} 
        
        	if(msg_type.equals("CRE")) { //entra a seccion de crear contacto de usuario
        		System.out.println("dentro de CREATE");
            	System.out.println("el parseo es: --" + msg_parts[0] + "- -" + msg_parts[1] + "- -" + msg_parts[2] + "--");
            	if(msg_parts.length < 3) return msg_INVALID;
            	
            	if(ChatServer.crearChat(nick, msg_parts[1])){
                	System.out.println("dentro de revision de CREATE");
            		ChatServer.chatUsuarios(nick, msg_parts[1]);
            	return msg_OK;
            	}
            	else
            		return msg_CREATE_FAILED;
        	}
        	if(msg_type.equals("CTS"))
            {
             return ChatServer.parsearContactos(msg_parts[1]);
            }
        	else
        		return msg_INVALID;
    }
}
 
class ClientConn implements Runnable {
    private Socket client;
    private BufferedReader in = null;
    private PrintWriter out = null;
 
    ClientConn(Socket client) {
        this.client = client;
        try {
            /* obtain an input stream to this client ... */
            in = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
            /* ... and an output stream to the same client */
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
    }
 
    public void run() {
        String msg, response;
        ChatServerProtocol protocol = new ChatServerProtocol(this);
        try {
            /* loop reading lines from the client which are processed 
             * according to our protocol and the resulting response is 
             * sent back to the client */
            while ((msg = in.readLine()) != null) {
                response = protocol.process(msg);
                out.println("SERVER: " + response); //aqui se procesa el return de protocol, lo cual lee chatClient
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
 
    public void sendMsg(String msg) {
        out.println(msg);
    }
}
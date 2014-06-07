package com.redes.tarea2;

import java.net.ServerSocket;
import java.net.Socket;
 
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
 
import java.util.Hashtable;
 
public class ChatServer {
    private static int port = 1001; /* port to listen on */
 
  //metodo que comprueba si existe un usuario con el nombre especificado
  	public boolean existe_usuario(String nombre)
  	{
  		String path_raiz=System.getProperty("user.dir");//obtenemos el path del directorio de trabajo
  		String path_usuario=path_raiz+"\\dataserver\\users\\"+nombre;
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
    
    
    public static void main (String[] args) throws IOException {
 
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
    private String nick;
    private ClientConn conn;
 
    /* a hash table from user nicks to the corresponding connections */
    private static Hashtable<String, ClientConn> nicks = 
        new Hashtable<String, ClientConn>();
 
    private static final String msg_OK = "OK";
    private static final String msg_NICK_IN_USE = "NICK IN USE";
    private static final String msg_SPECIFY_NICK = "SPECIFY NICK";
    private static final String msg_INVALID = "INVALID COMMAND";
    private static final String msg_SEND_FAILED = "FAILED TO SEND";
 
    /**
     * Adds a nick to the hash table 
     * returns false if the nick is already in the table, true otherwise
     */
    private static boolean add_nick(String nick, ClientConn c) {
        if (nicks.containsKey(nick)) {
            return false;
        } else {
            nicks.put(nick, c);
            return true;
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
    private String authenticate(String msg) {
        if(msg.startsWith("NICK")) {
            String tryNick = msg.substring(5);
            if(add_nick(tryNick, this.conn)) {
                log("Nick " + tryNick + " joined.");
                this.nick = tryNick;
                return msg_OK;
            } else {
                return msg_NICK_IN_USE;
            }
        } else {
            return msg_SPECIFY_NICK;
        }
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
        if(msg_type.equals("MSG")) {
            if(msg_parts.length < 3) return msg_INVALID;
            if(sendMsg(msg_parts[1], msg_parts[2])) return msg_OK;
            else return msg_SEND_FAILED;
        } else {
            return msg_INVALID;
        }
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
                out.println("SERVER: " + response);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
 
    public void sendMsg(String msg) {
        out.println(msg);
    }
}
package cs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDate;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket1 = new ServerSocket(2222);
        Server server = new Server(serverSocket1);
        server.startServer();


    }

    ServerSocket serverSocket;

    public Server(ServerSocket theServerSocket) throws IOException {
        this.serverSocket = theServerSocket;
    }

    public void startServer(){

        try{
            while(!serverSocket.isClosed()){

                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

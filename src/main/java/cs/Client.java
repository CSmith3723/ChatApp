package cs;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name for the chat:");
        String username = scanner.nextLine();

        Socket socket = new Socket("localhost", 2222);
        Client client = new Client(socket, username);
        client.listenForMessage();  //These are blocking operations because they will continue WHILE the socket is connected
        client.sendMessage();      //That's why we have them on separate threads.


    }


    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
       try {
           this.socket = socket;
           this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
           this.username = username;
       }catch(IOException e){
           closeEverything(socket, bufferedWriter, bufferedReader);
       }
    }

    public void sendMessage(){ //This is going to correspond with the ClientHandler waiting for the clientUserName in its constructor
        try{                    //When we create the ClientHandler, this.clientUserName = bufferedReader.newLine() will be waiting for
                                //this send message to pass the write username here.
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username+": "+ messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(socket, bufferedWriter,bufferedReader);
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() { //Separate thread so we can listen while waiting for a message that will be
                                    //broadcast from broadcastMessage. Each client will have a separate thread waiting
                                    // for the message that is sent out
            @Override
            public void run() {
                String messageFromGroupChat;

                while(socket.isConnected()){
                    try{
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    }catch (IOException e){
                        closeEverything(socket, bufferedWriter,bufferedReader);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){

        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

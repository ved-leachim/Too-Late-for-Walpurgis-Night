package ch.ffhs.swea.town.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection implements Runnable {
    private Socket server;
    private BufferedReader in;

    private ClientController myClient;

    public ServerConnection(ClientController c, Socket s) {
        try {
            myClient = c;
            server = s;
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                String serverResponse = in.readLine();
                if(serverResponse != null) myClient.receiveServerMessage(serverResponse);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

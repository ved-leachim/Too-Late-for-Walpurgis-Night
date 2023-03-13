package ch.ffhs.swea.town.server;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.PrintWriter;
        import java.net.Socket;

public class ClientHandler implements Runnable {
    private String playerName;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket clientsocket) {
        try {
            in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
            out = new PrintWriter(clientsocket.getOutputStream(), true);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String val) {
        if(val != null && !val.equals("")) playerName = val;
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                String request = in.readLine();
                System.out.println("client said: '" + request + "'");
                out.println(ServerController.handleClientMessage(this, request));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** send a message to this client
     * @param msg the text message
     */
    public void sendMessage(String msg) {
        out.println(msg);
    }
}

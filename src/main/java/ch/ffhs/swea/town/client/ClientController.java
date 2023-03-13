package ch.ffhs.swea.town.client;

import java.net.*;
import java.io.*;

public class ClientController implements Runnable  {
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 4999;
    private static InputStream keyboardStream = System.in;
    protected static PrintStream consoleOut = System.out;
    public HelloController myController;

    public ClientController(HelloController ctrl) {
        myController = ctrl;
    }

    /** output for local client's console
     * @param s output text
     */
    protected static void printLine(String s) {
        consoleOut.println(s);
    }

    protected static void setInputStream(InputStream is) {
        keyboardStream = is;
    }

    protected static void setConsoleOut(PrintStream os) {
        consoleOut = os;
    }

    @Override
    public void run() {
        try {
            Socket s = new Socket(SERVER_IP, SERVER_PORT);
            new Thread(new ServerConnection(this, s)).start();

            BufferedReader keyboard = new BufferedReader(new InputStreamReader(keyboardStream));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            while(!Thread.currentThread().isInterrupted()) {
                String msg = keyboard.readLine();
                if(msg != null && !msg.isEmpty()) {
                    out.println(msg); //sends to server
                }
            }

            s.close();
            System.exit(0);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveServerMessage(String msg) {
        //.printLine("Server: " + serverResponse);
        myController.onHelloButtonClick();
    }
}

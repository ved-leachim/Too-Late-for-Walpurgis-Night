package ch.ffhs.swea.town.server;

import java.net.*;
        import java.io.*;
        import java.io.File; // Import the File class
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Locale;
        import java.util.Scanner; // Import the Scanner class to read text files
import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;
        import java.util.concurrent.ThreadLocalRandom;
        import ch.ffhs.swea.town.client.*;

public class ServerController {
    private static final ArrayList<ClientHandler> clients = new ArrayList<>();
    private static final int PLAYERLIMIT = 2; // if playerlimit is reached, start the game
    private static final ExecutorService pool = Executors.newFixedThreadPool(PLAYERLIMIT); // one per player
    private static final HashMap<ClientHandler, Integer> leaderboard = new HashMap<>();
    private static int playerCount; // current
    private static int counter; // used to count seconds


    /** program entry point
     * @param args program start arguments
     */
    public static void main(String[] args) {
        Socket newclient;
        boolean reachedPlayerCount = false;

        try (ServerSocket ss = new ServerSocket(ClientController.SERVER_PORT)) {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("waiting for client...");
                newclient = ss.accept();

                if (playerCount <= PLAYERLIMIT && !reachedPlayerCount) {
                    ClientHandler cH = new ClientHandler(newclient);
                    cH.setPlayerName("Player " + (clients.size()+1));
                    clients.add(cH);
                    System.out.println("client connected!");
                    pool.execute(cH);
                    playerCount++;
                    System.out.println("currentplayercount: " + playerCount);
                    leaderboard.put(cH, 0);
                }

                if (!reachedPlayerCount && playerCount >= PLAYERLIMIT) {
                    reachedPlayerCount = true;
                    System.out.println("player limit reached!");
                    System.out.println("starting game...");
                    sendMessageToClients("Spiel beginnt!");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** public accessor for ClientHandler List
     * @return all clientHandlers available
     */
    public static List<ClientHandler> getClients() {
        return clients;
    }

    /** increase the score for this client by 1
     * @param handler the client
     */
    protected static void increaseScoreFor(ClientHandler handler) {
        for (var entry : leaderboard.entrySet()) {
            if (entry.getKey().equals(handler)) {
                entry.setValue(entry.getValue() + 1);
                break;
            }
        }
        printLeaderboard();
    }

    /**
     * print the standings with playername and their score to the console and send to clients
     */
    protected static void printLeaderboard() {
        StringBuilder sb = new StringBuilder("Spielstand:");
        for (var entry : leaderboard.entrySet()) {
            sb.append(System.lineSeparator());
            sb.append(entry.getKey().getPlayerName());
            sb.append(": ");
            sb.append(entry.getValue().toString());
        }

        sendMessageToClients(sb.toString());
    }

    /**
     * print countdown timer till next question
     */
   /* protected static void queueNextQuestion() {
        counter = 5;
        Timer t = new Timer();
        t.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (counter == 0) {
                            Server.poseQuestion();
                            System.out.println("posed next question");
                            t.cancel();
                            t.purge();
                        }
                        else Server.sendMessageToClients("nächste Frage in " + counter + "...");
                        counter--;
                    }
                },
                0, 1000);
    }*/

    /** parse string received by a client
     * @param handler the client
     * @param message the client's message
     * @return response from the server
     */
    public static synchronized String handleClientMessage(ClientHandler handler, String message) {
        String[] msgParts = message.toLowerCase(Locale.ROOT).split(" ");
        switch (msgParts[0]) {
            case "a":
            case "b":
            case "c":
                return evaluateAnswer(handler, msgParts[0]);
            case "spielstand":
                printLeaderboard();
                return null;
            default:
                return "Nachricht erhalten.";
        }
    }

    /** evaluate whether client's response was the true answer
     * @param handler the client
     * @param playerAnswer the client's guess
     * @return response to client whether they got it right
     */
    protected static String evaluateAnswer(ClientHandler handler, String playerAnswer) {
        if (playerAnswer == null) return "Antwort unzulässig!"; // or too late answer
        else {

            return "Antwort nicht verwertbar!";
        }
    }

    /** once the correct answer was given, queue up the next question
     * @param answer the answer identifier
     * @param handler increase the score for the client that gave the correct answer
     * @return tell the clients that the true answer was given
     */
    protected static String handleCorrectAnswer(char answer, ClientHandler handler) {
        System.out.println("correct answer was given, queueing next question...");
        new java.util.Timer()
                .schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                increaseScoreFor(handler);
                               //queueNextQuestion();
                            }
                        }, 1000);
        return answer + " ist korrekt!";
    }

    /** send a message string to all clients at once
     * @param msg the message to send
     */
    public static void sendMessageToClients(String msg) {
        clients.stream().parallel().forEach(c -> c.sendMessage(msg));
    }
}

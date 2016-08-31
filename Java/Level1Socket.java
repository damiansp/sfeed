/* Level1Socket.java */
import java.io.IOException;

public class Level1Socket {
  private int IQFEED_LEVEL1_PORT_DEFAULT = 5009; // adjustable in registry
  IQFeed_Socket C_Level1IQFeed_Socket;

  // Constr
  /**
   * Create a new Level1Socket
   */
  public Level1Socket() {
    boolean Connected = false;

    C_Level1IQFeed_Socket = new IQFeed_Socket();

    System.out.println("Connecting to Level 1 port...");

    // Request socket connection to localhost on port
    // IQFEED_LEVEL1_PORT_DEFAULT.  Return false if unable to connect
    if (!C_Level1IQFeed_Socket.connectSocket(IQFEED_LEVEL1_PORT_DEFAULT)) {
      System.out.println("Did you forget to log in first?");
      System.exit(1);
    }

    System.out.println("Connected to Level 1 port.");

    C_Level1IQFeed_Socket.createBuffers();

    // Init protocol-- prepares for commands to come and verifies that socket
    // is working as intended
    try {
      Java_Config config = new Java_Config();
      C_Level1IQFeed_Socket.brBufferedWriter.write(String.format(
        "S,SET PROTOCOL,%s\r\n", config.most_recent_protocol));
      C_Level1IQFeed_Socket.brBufferedWriter.flush();
      System.out.println("Message Posted-- Protocol set.");
    } catch (Exception e) {
      System.out.println("Error writing to socket");
      e.toString();
    }

    Level1Listener thread = new Level1Listener();
    thread.start();
  }

  // Send a message to the server
  private void sendMessage(String sCommand) {
    try {
      C_Level1IQFeed_Socket.brBufferedWriter.write(sCommand);
      C_Level1IQFeed_Socket.brBufferedWriter.flush();
      System.out.println(sCommand + " sent.");
    } catch (IOException e) {
      System.out.println("Error: " + sCommand);
      e.toString();
    }
  }

  // Begin tracking a stock
  public void watchStock(String stock) {
    String sCommand = "w" + stock + "\r\n";
    sendMessage(sCommand);
  }



  // Class to execute and listen to replies from server.
  class Level1Listener extends Thread {
    public void run() {
      String line;

      try {
        while ((line = C_Level1IQFeed_Socket.brBufferedReader.readLine())
               != null) {
          System.out.println(line);
        }
      } catch (Exception e) {
        System.out.println("Unable to read from socket.");
      }
    }
  }
}

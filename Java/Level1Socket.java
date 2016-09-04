/* Level1Socket.java */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Level1Socket {
  private int IQFEED_LEVEL1_PORT_DEFAULT = 5009; // adjustable in registry
  IQFeed_Socket C_Level1IQFeed_Socket;
  private LinkedList stocks = new LinkedList<String>();
  private BufferedReader input;
  private String line;
    

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

  // Read the stocks to be tracked from a file.  File should be formatted as
  // "STOCK1"\n
  // "STOCK2"\n
  // ...
  public void readStockFile(String filePath) {
    // Create input stream and open file
    try {
      input = new BufferedReader(new FileReader(filePath));
    } catch (IOException ioEx) {
      System.err.println("Error opening file");
      ioEx.printStackTrace();
    }

    try {
      while ((line = input.readLine()) != null) {
        stocks.add(line);
      }
    } catch (IOException ioEx) {
      System.err.printf("Error reading next line of file %s\n", filePath);
      ioEx.printStackTrace();
    } finally {
      try {
        input.close();
      } catch (IOException ioEx) {
        System.err.printf("Error closing file %s\n", filePath);
        ioEx.printStackTrace();
      }
    }


  }

  // Begin tracking a stock
  public void watchStock(String stock) {
    String sCommand = "w" + stock + "\r\n";
    sendMessage(sCommand);
  }

  // Watch all stocks
  public void watchAllStocks(int maxStocks) {
    int nStocks = 0;
    String nextStock;

    nextStock = (String) stocks.pop();
    
    while (nextStock != null && nStocks <= maxStocks) {
      // First "stock" read is ^VIX; ignore.
      nextStock = (String) stocks.pop();
      watchStock(nextStock);
      nStocks++;
    }
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

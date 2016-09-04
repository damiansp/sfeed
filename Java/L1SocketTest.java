/* L1SocketTest.java */
// Test the Level1Socket class
public class L1SocketTest {
  public static void main(String[] args) {
    Level1Socket sock = new Level1Socket();

    sock.readStockFile("stocks.csv");
    sock.watchAllStocks(3000);
  }
}


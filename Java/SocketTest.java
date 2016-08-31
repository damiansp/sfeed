/* SocketTest.java */
// Test IQFeed_Socket class
public class SocketTest {
  // NOTE: iqFeed must be running to connect
  public static void main(String[] args) {
    IQFeed_Socket sock = new IQFeed_Socket();

    sock.connectSocket(5009);
    sock.createBuffers();
    sock.closeBuffers();
    sock.disconnect();
  }
}

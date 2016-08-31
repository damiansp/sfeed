/* IQFeed_Socket.java */

// Website for Level1 information:
//   http://www.iqfeed.net/dev/api/docs/Level1viaTCPIP.cfm
import java.io.*;
import java.net.*;


public class IQFeed_Socket extends Socket {
	BufferedReader brBufferedReader;
	BufferedWriter brBufferedWriter;

	/**
   * Notes:  Default Constructor using parent Socket class
   */
	public IQFeed_Socket() {
		super();
	}

	/**
   * @params iPort - integer representing a socket port to connect to.
   * @return If connect goes through without error, return true, false if 
   *         there is any other issue.
   * Notes: Connects a socket to the localhost on the port sent
   *        Default values are located in the registry settings documentation 
   *        on the API site.
   */
	protected boolean connectSocket(int iPort) {
		try {
			this.connect(new InetSocketAddress("localhost", iPort));
		} catch(IOException eError) {
			System.out.printf("Unable to connect to socket on port, %d.\n", iPort);
			eError.printStackTrace();
		} finally {
			return this.isConnected();
		}
	}


  
	/**
   * Notes: Disconnect the socket and close both buffer streams
   */
	protected void disconnect() {
		try {
			this.closeBuffers();
			this.close();
		} catch(IOException eError) {
			System.out.printf("Unable to disconnect the socket.");
			eError.printStackTrace();
			System.exit(1);
		}
	}


  
	/**
   * Notes: Using a class references socket, implement both a buffered reader
   *	      and a buffered writer to pass data back and forth between the app 
   *        and IQConnect.
   */
	protected void createBuffers() {
		try {
			// create a buffer to read in socket data.
			brBufferedReader = new BufferedReader(new InputStreamReader(
        this.getInputStream()));
			// create a buffer in which to send commands to IQFeed on. 
			brBufferedWriter = new BufferedWriter(new OutputStreamWriter(
        this.getOutputStream()));	
		} catch(IOException eError) {
			System.out.printf("Unable to create readers.");
			eError.printStackTrace();
			System.exit(1);
		}
	}


  
  /**
   * Notes: Close both the read and write buffer.
   */
	protected void closeBuffers() {
		try {		
			brBufferedReader.close();
			brBufferedWriter.close();
		} catch(IOException eError) {
			System.out.printf("Unable to close readers.");
			eError.printStackTrace();
			System.exit(1);
		}
	}
}

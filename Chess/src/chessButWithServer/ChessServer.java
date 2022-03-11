package chessButWithServer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ChessServer {
	ServerSocket serverSocket;
	Socket cl1, cl2;
	DataInputStream in1, in2;
	DataOutputStream out1, out2;
	ChessManager manager;
	boolean cl1hacked = false, cl2hacked;
	
	public ChessServer(int port){
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server opened on port "+port);
			System.out.println("IPv4: "+InetAddress.getLocalHost().getHostAddress());
			cl1 = serverSocket.accept();
			System.out.println("Client 1 Connected");
			cl2 = serverSocket.accept();
			System.out.println("Client 2 Connected");
			
			out1 = new DataOutputStream(cl1.getOutputStream());
			out2 = new DataOutputStream(cl2.getOutputStream());
			
			in1 = new DataInputStream(cl1.getInputStream());
			in2 = new DataInputStream(cl2.getInputStream());

			out1.write(new byte[] {1});
			out2.write(new byte[] {0});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Successfully connected to both clients");
		System.out.println("Starting game");
		
		manager = new ChessManager();		
		
		playGame();
	}
	
	public void playGame() {
		boolean running = true;
		manager.reset();
		while(!manager.isWon()&&running) {
			byte[] move;
			try {	
				do { //get cl1 input
					move = new byte[4];
					try {
						in1.read(move);
					} catch(SocketException e) {
						running = false;
						byte[] msg = "Lost peer connection".getBytes();
						out2.writeInt(2);
						out2.writeInt(msg.length);
						out2.write(msg);
						System.out.println("Cancelled connection with cl2");
					}
				} while(!(manager.getPieceAt(move[0], move[1]).canMoveTo(move[2], move[3], manager))&&!manager.getPieceAt(move[0], move[1]).color());

				manager.movePiece(move[0], move[1], move[2], move[3], true);
				
				//write to both
				running = broadcastMove(move);
				
				if(!manager.isWon()&&running) {
					do { //get cl1 input
						move = new byte[4];
						try {
							in2.read(move);
						} catch(SocketException e) {
							running = false;
							byte[] msg = "Lost peer connection".getBytes();
							out1.writeInt(2);
							out1.writeInt(msg.length);
							out1.write(msg);
							System.out.println("Cancelled connection with cl1");
						}
					} while(!(manager.getPieceAt(move[0], move[1]).canMoveTo(move[2], move[3], manager))&&manager.getPieceAt(move[0], move[1]).color());
	
					manager.movePiece(move[0], move[1], move[2], move[3], true);
					
					//write to both
					running = broadcastMove(move);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		System.out.println(running);
		
		if(running) {
			try {
				out1.writeInt(1);
				out1.writeBoolean(manager.getWinner());
				out2.writeInt(1);
				out2.writeBoolean(manager.getWinner());
				
				boolean cont;
				cont = in1.readBoolean();
				cont = cont && in2.readBoolean();
	
				out1.writeBoolean(cont);
				out2.writeBoolean(cont);
				
				if(cont) {
					playGame();
				} else {
					cl1.close();
					cl2.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean broadcastMove(byte[] move) {
		boolean running = true;
		try {
			try {
				out1.writeInt(0);
				out1.write(move);
			} catch(SocketException e) {
				running = false;
				byte[] msg = "Lost peer connection".getBytes();
				out2.writeInt(2);
				out2.writeInt(msg.length);
				out2.write(msg);
				System.out.println("Cancelled connection with cl2");
			}
			try {
				out2.writeInt(0);
				out2.write(move);
			} catch(SocketException e) {
				running = false;
				byte[] msg = "Lost peer connection".getBytes();
				out1.writeInt(2);
				out1.writeInt(msg.length);
				out1.write(msg);
				System.out.println("Cancelled connection with cl1");
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return running;
	}
	
	public static void main(String[] args) {
		ChessServer server = new ChessServer(9999);
	}
}

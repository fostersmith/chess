package chessButWithServer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ChessClient extends JFrame implements MouseListener {
	
	private static final long serialVersionUID = 1l;
	
	private ChessManager manager;
	
	public final static int SQUARE_PX = 60;
	
	private final BufferedImage SQUARES_IMG;
	private BufferedImage canvas;
	private final static Color COLOR1 = new Color(255, 210, 110)/*Color.WHITE*/, COLOR2 = new Color(230, 140, 50);//new Color(85,150,80);
	
	private JPanel chessPanel;
	private JPanel containerPanel;
	private JLabel label;
	
	private int highlightedX = -1, highlightedY = -1;
	private int selectedX, selectedY;
	
	private boolean color;
	private boolean playing;
	private boolean selecting;
	private boolean running;

	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	private Thread listenToServerThread;
	
	public ChessClient(String ip, int port) {
		running = true;
		
		manager = new ChessManager();
		selecting = true;
		
		//initialize network components
		try {
			socket = new Socket(ip,port);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			color = in.readByte()==0?false:true;
			playing = color;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		listenToServerThread = new Thread() {
			@Override
			public void run() {
				while(running) {
					try {
						int type = in.readInt();
						if(type==0) {
							byte[] move = new byte[4];
							in.read(move);
							playing = manager.getPieceAt(move[0], move[1]).color()!=color;
							manager.movePiece(move[0],move[1],move[2],move[3],true);
							label.setText("White - "+manager.getWhiteScore()+" : Black - "+manager.getBlackScore());
							setTitle(playing?"Your turn":"Other Player's Turn");
							repaint();
						} else if(type == 1) {
							boolean winner = in.readBoolean();
							int cont = JOptionPane.showConfirmDialog(null, (winner?"White":"Black")+" wins. Play again?","Game Over",JOptionPane.YES_NO_OPTION);
							if(cont == JOptionPane.YES_OPTION) {
								out.writeBoolean(true);
							} else {
								out.writeBoolean(false);
							}
							
							if(in.readBoolean()) {
								manager.reset();
								playing = color;
								selecting = true;
								repaint();
							} else {
								running = false;
								socket.close();
								JOptionPane.showMessageDialog(null, "Rematch Cancelled");
								System.exit(0);
							}
						} else if(type == 2) {
							int len = in.readInt();
							byte[] msgBytes = new byte[len];
							in.read(msgBytes);
							System.out.println("Received message");
							JOptionPane.showMessageDialog(null, new String(msgBytes));
							socket.close();
							System.exit(0);
						}
					} catch(SocketException e) {
						JOptionPane.showMessageDialog(null, "Connection with server stopped unexpectedly. Shutting down");
						System.exit(1);
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			}
		};
		listenToServerThread.start();
		
		//initialize images
		SQUARES_IMG = new BufferedImage(ChessManager.BOARD_WIDTH*SQUARE_PX,ChessManager.BOARD_HEIGHT*SQUARE_PX,BufferedImage.TYPE_INT_ARGB);
		canvas = new BufferedImage(SQUARES_IMG.getWidth(),SQUARES_IMG.getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) SQUARES_IMG.getGraphics();
		for(int x = 0; x < SQUARES_IMG.getWidth(); x += SQUARE_PX)
			for(int y = 0; y < SQUARES_IMG.getHeight(); y +=SQUARE_PX) {
				g.setPaint((y+x)%(2*SQUARE_PX)==0?COLOR1:COLOR2);
				g.fillRect(x, y, x+SQUARE_PX, y+SQUARE_PX);
			}
		try {
			ImageIO.write(SQUARES_IMG, "png", new File("squares.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		repaint();

		//initialize GUI
		getContentPane().removeAll();
		containerPanel = new JPanel();
		containerPanel.setLayout(new BoxLayout(containerPanel,BoxLayout.Y_AXIS));
		
		label = new JLabel("White - 0 : Black - 0");

		chessPanel = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
		    public Dimension getPreferredSize() {
		        return new Dimension(canvas.getWidth(), canvas.getHeight());
		    }
		    
		    @Override
		    public void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        Graphics2D g2 = (Graphics2D) g;
		        g2.drawImage(SQUARES_IMG, null, null);
		        render(g2);
		    }
		};

		chessPanel.addMouseListener(this);
		chessPanel.setDoubleBuffered(true);
		
		containerPanel.add(label);
		containerPanel.add(chessPanel);
		
		add(containerPanel);
		
		setTitle("White's Turn");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	public void render(Graphics2D g) {
		for(int r = manager.getPieces().length-1; r >= 0; --r) {
			for(int c = manager.getPieces()[r].length-1; c >= 0; --c) {
				if(manager.getPieces()[r][c]!=null) {
					BufferedImage im = manager.getPieces()[r][c].getImage();
					g.drawImage(im, color?(ChessManager.BOARD_WIDTH-1-c)*SQUARE_PX:c*SQUARE_PX, color?(ChessManager.BOARD_HEIGHT-1-r)*SQUARE_PX:r*SQUARE_PX, null);
				}
			}
		}
		g.setStroke(new BasicStroke(3));
		g.setPaint(Color.yellow);
		if(highlightedX>=0&&highlightedY>=0)
			g.drawRect(highlightedX*SQUARE_PX, highlightedY*SQUARE_PX, SQUARE_PX, SQUARE_PX);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = color?(ChessManager.BOARD_HEIGHT-1-e.getX()/SQUARE_PX):(e.getX()/SQUARE_PX);
		int y = color?(ChessManager.BOARD_HEIGHT-1-e.getY()/SQUARE_PX):(e.getY()/SQUARE_PX);

		boolean highlightSelection = false;
		if(playing) {
			if(selecting) {
				if(manager.getPieceAt(x, y)!=null)
					if(manager.getPieceAt(x, y).color()==color) {
						selectedX = x;
						selectedY = y;
						selecting = false;
						highlightSelection = true;
					}
			} else {
				if(manager.getPieceAt(selectedX, selectedY).canMoveTo(x, y, manager)) {
					try {
						out.write(new byte[] {(byte)selectedX, (byte)selectedY, (byte)x, (byte)y});
						System.out.println("Wrote to Server");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					selecting = true;
				} else if(manager.getPieceAt(x, y)!=null) {
					if(manager.getPieceAt(x, y).color()==color) {
						selectedX = x;
						selectedY = y;
						highlightSelection = true;
					}
				} else {
					selecting = true;
				}
			}
		}
		
		if(highlightSelection) {
			highlightedX = e.getX()/SQUARE_PX;
			highlightedY = e.getY()/SQUARE_PX;
		} else {
			highlightedX = -1;
			highlightedY = -1;
		}
				
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	public static void main(String[] args) {
		String hostIP = null;
		Object[] options = {"Host a game","Join a game"};
		Object opt = JOptionPane.showInputDialog(null, "Welcome to Online Chess", "Welcome", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if(opt==options[0]) {
			try {
				Thread serverThread = new Thread() {
					@Override
					public void run() {
						ChessServer server = new ChessServer(9999);
					}
				};
				serverThread.start();
				JOptionPane.showMessageDialog(null, "Server started on port 9999. IP is "+InetAddress.getLocalHost().getHostAddress());
				hostIP = InetAddress.getLocalHost().getHostAddress();
			} catch (HeadlessException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} else {
			hostIP = JOptionPane.showInputDialog("Enter Host IP");			
		}
		@SuppressWarnings("unused")
		ChessClient f1 = new ChessClient(hostIP,9999);
	}
}

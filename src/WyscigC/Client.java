package WyscigC;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Wyscig.Member;
import Wyscig.MemberList;

public class Client extends Thread{
	
	public static MemberList ml;
	// komponenty polaczenia TCP
	public static String hostIP = "127.0.0.1";
	public static int port = 5555;
	public static boolean connected = false;
	public static Socket socket = null;
	public static BufferedReader in = null;
	public static PrintWriter out = null;

	// komponenty GUI
	public static JFrame mainFrame = null;
	public static JTextArea chatText = null;
	public static JTextField chatLine = null;
	public static JPanel statusBar = null;
	public static JLabel statusField = null;
	public static JTextField ipField = null;
	public static JTextField userField = null;
	public static JButton connectButton = null;
	public static JButton sendUserButton = null;
	public static JButton getSpecificDataButton = null;
	public static JButton startButton = null;

	// inijcalizacja GUI
	private static void initGUI() {
   	
		// pasek statutowy
		statusField = new JLabel();
		statusField.setText("");
		statusBar = new JPanel(new BorderLayout());
		statusBar.add(statusField, BorderLayout.CENTER);

		// panel z opcjami
		JPanel optionsPane = new JPanel(new GridLayout(10, 1));
		
		JPanel pane1 = null;
		pane1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane1.add(new JLabel("Nazwisko i Imiê:"));
		userField = new JTextField(20);
		pane1.add(userField);
		optionsPane.add(pane1);
		
		/*st¹d wyciê³am kod oznaczony nr 1 w pliku "klient.txt"*/
		
		//rejestracja u¿ytkownika
		sendUserButton = new JButton("Zarejestruj u¿ytkownika");
		// nasluchiwacz przycisku
		ActionAdapter connectListener2 = new ActionAdapter() {
			public void actionPerformed(ActionEvent a) {
				
					if (!connected) {
						statusField.setText("Musisz siê najpierw po³¹czyæ!");
					}
				
					sendUser();
					userField.setText("");
			}

			private void sendUser() {
				String s = userField.getText();
				if(!s.equals("")){
					userField.selectAll();
					out.print(s);
					out.flush();
				}
			}
		};      
		sendUserButton.addActionListener(connectListener2);       
		optionsPane.add(sendUserButton);      
		
		getSpecificDataButton = new JButton("Pobierz dane uczestnika");
		
		ActionAdapter connectListener3 = new ActionAdapter() {
			public void actionPerformed(ActionEvent a) {
				
					if (!connected) {
						statusField.setText("Musisz siê najpierw po³¹czyæ!");
					}
				
					getData();
					userField.setText("");
			}

			private void getData() {
				String s = userField.getText();
				if(!s.equals("")){
					userField.selectAll();
					out.print("pobierz" +s);
					out.flush();
				}
			}
		};
		getSpecificDataButton.addActionListener(connectListener3);
		optionsPane.add(getSpecificDataButton);
		
		startButton = new JButton("Zacznij wyœcig");
		
		ActionAdapter connectListener4 = new ActionAdapter() {
			public void actionPerformed(ActionEvent a) {
				
					if (!connected) {
						statusField.setText("Musisz siê najpierw po³¹czyæ!");
					}
				
					try {
						startRun();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}

			private void startRun() throws IOException {
				String s = "Start";
					out.print(s);
					out.flush();
						while(!in.equals("")){
							StringTokenizer st = new StringTokenizer(in.readLine());
							Member tmp = new Member(st.nextToken(), st.nextToken());
							ml.addMember(tmp);
						}
			}
		};
		startButton.addActionListener(connectListener4);
		optionsPane.add(startButton);
		
		// panel z chatem
		JPanel chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		JScrollPane chatTextPane = new JScrollPane(chatText,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// linijka edycyjna chata
		chatLine = new JTextField();
		// nasluchiwacz linii edycyjnej
		ActionAdapter chatListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent a) {
				if (connected)
				{
					String s = chatLine.getText();
					if (!s.equals("")) {
						chatLine.selectAll();
						out.print(s); 
						out.flush();
						chatText.append("Klient : " + s + '\n');
						statusField.setText("Wys³ano komunikat do serwera.");	  				
					}
				}
			}
		};
		chatLine.addActionListener(chatListener);
		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(200, 200));

		// glowny panel
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);

		// ramka glowna
		mainFrame = new JFrame("Java TCP Klient");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(300, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	/////////////////////////////////////////////////////////////////
	//	funkcja glowna - main
	/////////////////////////////////////////////////////////////////
	
	public static void main(String args[]) {
		StringBuffer instr = new StringBuffer("");
   	
		// inicjalizacja interfejsu
		initGUI();
      
		// petla glowna watku main
		while (true) {
			try { 
				// uspij na 100ms
				Thread.sleep(100);
			}
			// wyjatki...
			catch (InterruptedException e) {}
			
			//po³¹czenie
			try {
				if (!connected) {
					// stworzenie gniazda i powiazanie strumieni
					//hostIP = ipField.getText();
					//hostIP = "localhost";
					socket = new Socket(hostIP, port);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);
					statusField.setText("Po³¹czono z serwerem.");
					connected = true;
				}
			}
			// wyjatki...
			catch (UnknownHostException e) {
				statusField.setText("Nieznany serwer.");
				connected = false;
			}
			catch (IOException e) {
				statusField.setText("B³¹d przy ³¹czeniu z serwerem.");
				connected = false;
			}
			
			
			// sprawdz bufor odbiorczy
			try {
				if (connected) {
					// jesli gotowosc do odczytu...
					if (in.ready()) {
						instr.setLength(0);
						// czytaj poki cos jest w buforze
						while (in.ready())
							instr.append((char) in.read());
						chatText.append("Serwer : " + instr.toString() + '\n');
						statusField.setText("Odebrano komunikat od serwera.");               			
					}
				}
			}
			// wyjatki...
			catch (IOException e) {
				statusField.setText("B³¹d przy odbieraniu komunikatu.");        	
			}        
		}
	}
}

/////////////////////////////////////////////////////////////////
//	ActionAdapter dla ulatwienia kodowania nasluchiwaczy
/////////////////////////////////////////////////////////////////

class ActionAdapter implements ActionListener {
	public void actionPerformed(ActionEvent e) {}
}

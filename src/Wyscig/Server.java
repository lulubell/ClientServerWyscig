package Wyscig;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
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

/////////////////////////////////////////////////////////////////
//	klasa JavaTcpSerwer
/////////////////////////////////////////////////////////////////

public class Server {

	// komponenty polaczenia TCP
	public static int port = 5555;
	public static boolean connected = false;
	public static boolean connecting = false;
	public static Socket socket = null;
	public static ServerSocket serverSocket = null;
	public static BufferedReader in = null;
	public static PrintWriter out = null;

	// komponenty GUI
	public static JFrame mainFrame = null;
	public static JTextArea chatText = null;
	public static JTextField chatLine = null;
	public static JPanel statusBar = null;
	public static JLabel statusField = null;
	public static JButton connectButton = null;

	// inijcalizacja GUI
	private static void initGUI() {
   	
		// pasek statutowy
		statusField = new JLabel();
		statusField.setText("Java TCP Serwer");
		statusBar = new JPanel(new BorderLayout());
		statusBar.add(statusField, BorderLayout.CENTER);

		// panel z opcjami - adres IP 
		JPanel optionsPane = new JPanel(new GridLayout(4, 1));
		JPanel pane = null;
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Kliknij, ¿eby czekaæ na po³¹czenie"));
		optionsPane.add(pane);

		// przycisk "Polacz"
		connectButton = new JButton("Po³¹cz");
		// nasluchiwacz przycisku
		ActionAdapter connectListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent a) {
				if ((!connected) && (!connecting)) {
					statusField.setText("Oczekiwanie na po³¹czenie...");
					connecting = true;
				}
			}
		};      
		connectButton.addActionListener(connectListener);       
		optionsPane.add(connectButton);      

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
						chatText.append("Serwer : " + s + '\n');
						statusField.setText("Wys³ano komunikat do klienta.");	  				
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
		mainFrame = new JFrame("Java TCP Serwer");
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
	
	public static void main(String args[]) throws IOException {
		StringBuffer instr = new StringBuffer("");
		MemberList ml = new MemberList("nowaLista.txt");
		
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
        
			// sprawdz bufor odbiorczy
			try {
				if (connected) {
					// jesli gotowosc do odczytu...
					if (in.ready()) {
						instr.setLength(0);
						// czytaj poki cos jest w buforze
						while (in.ready())
							instr.append((char) in.read());
						String s = instr.toString();
						if(s.equals("Pobierz")){
							for(int i = 0; i < ml.members.size(); i++){
								out.print(ml.members.get(i).toString());
								out.flush();
							}
						} else if(s.equals("Start")){
							for(int i = 0; i < ml.members.size(); i++){
								out.print(ml.members.get(i).toString2());
								out.flush();
							}
						} else {
							StringTokenizer st = new StringTokenizer(s);
							String name = st.nextToken();
							String surname = st.nextToken();
							Member m = new Member(name, surname);
							ml.addMember(m);
							out.print("Zarejestrowano biegacza z numerem " +m.number +" - " +m.imie +" " +m.nazwisko);
							out.flush();
						}
						//chatText.append("Klient : " + instr.toString() + '\n');
						statusField.setText("Odebrano komunikat od klienta.");         			
					}
				}
			}
			// wyjatki...
			catch (IOException e) {
				statusField.setText("B³¹d przy odbieraniu komunikatu.");        	
			}
			
			// sprawdz gotowosc do polaczenia
			try {
				if (connecting) {
					//stworzenie gniazda serwera i powiazanie strumieni
					serverSocket = new ServerSocket(port);
					socket = serverSocket.accept();
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);
					connected = true;
					connecting = false;
					statusField.setText("Po³¹czono z klientem.");						
				}
			}
			
			// wyjatki...
			catch (UnknownHostException e) {
				statusField.setText("Nieznany serwer.");
				connected = connecting = false;
			}
			catch (IOException e) {
				statusField.setText("B³¹d przy ³¹czeniu z klientem.");
				connected = connecting = false;
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

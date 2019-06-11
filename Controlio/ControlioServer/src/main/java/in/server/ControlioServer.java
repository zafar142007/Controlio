package in.server;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ControlioServer {

  private String ip;
  private int PORT = 8079;
  private SystemTray tray;
  private PopupMenu popup;
  private JPanel panel;
  private final MenuItem settingsMenu = new MenuItem("Settings");
  private final MenuItem ipMenu = new MenuItem("Get IP of this machine");
  private final MenuItem portMenu = new MenuItem("Set port");
  private final MenuItem exitMenu = new MenuItem("Exit");
  private JTextField textField;
  private JLabel label, error;
  private JFrame alert;
  private JButton button;
  private Image image;
  private TrayIcon trayIcon;

  Thread master = null;
  Networker worker;

  public int getPort() {
    return PORT;
  }

  public void setPort(int port) {
    this.PORT = port;
    this.master.interrupt();
    worker = new Networker();
    //	worker.setRemote(remote);
    worker.setPort(port);
    this.master = new Thread(worker);
    this.master.start();
  }

  public ControlioServer() {
    try {
      ip = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  class MyListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(settingsMenu)) {

      }
      // execute default action of the application
      if (e.getSource().equals(exitMenu)) {
        System.out.println("Server: Exiting");
        System.exit(0);
      }
      if (e.getSource().equals(portMenu)) {
        System.out.println("Server: Context port menu clicked");
        button = new JButton("Set");
        textField = new JTextField(6);
        label = new JLabel("Port:");
        error = new JLabel("");
        alert = new JFrame();
        //alert.setLayout(new )
        alert.setSize(200, 110);
        alert.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        alert.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 200,
            Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 110);
        alert.setTitle("Set port");
        panel = new JPanel();
        //panel.setLayout(new BorderLayout());
        try {
          panel.add(label);
          panel.add(textField);
          panel.add(button);
          panel.add(error);
          alert.add(panel);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        button.addActionListener(listener);
        alert.setVisible(true);
        alert.setAutoRequestFocus(true);
      }
      if (e.getSource().equals(button)) {
        System.out.println("Button: I was pressed");
        try {
          String text = textField.getText();
          if (text.matches("[^0-9]*")) {
            System.out.println("Server: bad port");
            error.setText("Only numbers are allowed!");
          } else if (!textField.getText().equals("")) {
            System.out.println("Server: port set");
            error.setText("Success.");
            setPort(Integer.parseInt(textField.getText()));
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (e.getSource().equals(ipMenu)) {
        alert = new JFrame();
        alert.setSize(200, 70);
        alert.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        alert.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 200,
            Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 70);
        alert.setTitle("IP address");
        alert.add(new JLabel(ip));
        alert.setAutoRequestFocus(true);
        alert.setVisible(true);
      }
    }


  }

  MyListener listener = new MyListener();

  public static void main(String args[]) {

    ControlioServer server = new ControlioServer();

    if (SystemTray.isSupported()) {
      // get the SystemTray instance
      server.tray = SystemTray.getSystemTray();
      // load an image
      try {
        server.image = ImageIO
            .read(ControlioServer.class.getClassLoader().getResourceAsStream("icon.png"))
            .getScaledInstance(20, 20, Image.SCALE_SMOOTH);
      } catch (Exception e) {
        e.printStackTrace();
      }
      // create a action listener to listen for default action executed on the tray icon
      // create a popup menu
      server.popup = new PopupMenu();
      // create menu exitMenu for the default action
      server.ipMenu.addActionListener(server.listener);
      server.popup.add(server.ipMenu);
      server.portMenu.addActionListener(server.listener);
      server.popup.add(server.portMenu);
      server.settingsMenu.addActionListener(server.listener);
//			server.popup.add(server.settingsMenu);
      server.exitMenu.addActionListener(server.listener);
      server.popup.add(server.exitMenu);
      try {
        server.trayIcon = new TrayIcon(server.image, "Controlio", server.popup);
        //System.out.println(server.trayIcon==null);
        server.trayIcon.addActionListener(server.listener);
      } catch (Exception e) {
        e.printStackTrace();
      }

      // add the tray image
      try {
        server.tray.add(server.trayIcon);
      } catch (Exception e) {
        e.printStackTrace();
      }
      // ...
    } else {
      // disable tray option in your application or
      // perform other actions

    }

    server.worker = new Networker();
    //	server.worker.setRemote(server.remote);
    server.master = new Thread(server.worker);

    server.master.start();

  }
}

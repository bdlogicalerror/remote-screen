import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

import java.awt.Cursor;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class ScreenShare {
    public static GraphicsDevice[] screens;
    // move Mouse per five second while in client mode
    private boolean mouseMove;

    //selected screen
    private int selectedScreen;

    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        screens = ge.getScreenDevices();

        System.setProperty("java.awt.headless", "false");
    

        System.out.println("Welcome to Remote Screen Sharer");
        System.out.println("Total Monitor: " + screens.length);

        if (args.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                sb.append(args[i]).append(' ');
            }
            new ScreenShare().intepretCommand(sb.toString());
        } else {
            System.out.println("To set up server, type server [port] [monitor-id]");
            System.out.println("To Set up client, type client [server-addr] [port]");
             new ScreenShare().interactive();
        }
    
    }



    private void interactive() {
        Scanner s = new Scanner(System.in);
        while (true) {
            try {
                InetAddress addr = InetAddress.getByName("localhost");
                System.out.print(addr.getCanonicalHostName() + ">>> ");
                if (s.hasNext()) {
                    intepretCommand(s.nextLine());
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Intepret a given command
     * 
     * @param cmd
     */
    private void intepretCommand(String cmd) {
        StringTokenizer tokenizer = new StringTokenizer(cmd);
        String commandToken = tokenizer.nextToken();
        
        if (commandToken.equals("server")) {
            String port = tokenizer.nextToken();
            selectedScreen = Integer.parseInt(tokenizer.nextToken())-1;
            server(Integer.parseInt(port));
        } else if (commandToken.equals("client")) {
            String serverAddr = tokenizer.nextToken();
            String port = tokenizer.nextToken();
            client(serverAddr, Integer.parseInt(port));
        } else if (commandToken.equals("close")) {
            close();
        } else if (commandToken.equals("mousemove")) {
            this.mouseMove = true;
        } else if (commandToken.equals("nomousemove")) {
            this.mouseMove = false;
        } else {
            System.out.println("Unrecognized Command");
        }

    }

    private void client(String serverAddr, int port) {
        JFrame frame = new JFrame();
        ImagePanel panel = new ImagePanel();
        frame.setResizable(true);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        try {
            BufferedImage image;
            Robot r = new Robot();
            long startTime = System.currentTimeMillis();
            Random rand = new Random();
            while (true) {
                if (this.mouseMove && System.currentTimeMillis() - startTime > 5000) {
                    r.mouseMove(rand.nextInt(200), rand.nextInt(200));
                    startTime = System.currentTimeMillis();
                }

                socket = new Socket(serverAddr, port);
                
                socket.setSendBufferSize(250);
                socket.setReceiveBufferSize(250);

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                image = ImageIO.read(inputStream);
                panel.setImg(image);
                panel.repaint();
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    ServerSocket server;
    Socket socket;

    private void close() {
        try {
            socket.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void server(int port) {
    
        try {
            server = new ServerSocket(port);

            GraphicsDevice currentDevice = screens[selectedScreen];
            Robot r = new Robot();
            Rectangle screenBounds = currentDevice.getDefaultConfiguration().getBounds();

            System.out.println("Server Started On port "+port +" Screen "+(selectedScreen+1));

            while (true) {
                try {
                    socket = server.accept();
                    InetAddress addr = socket.getInetAddress();
                    long millis = System.currentTimeMillis() % 1000;
                    System.out.println(millis+
                            " Received Connection From " + addr.getCanonicalHostName() + " at " + addr.getHostAddress());
                    ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                    BufferedImage img;
                    img = r.createScreenCapture(screenBounds);
                    Point mouse = MouseInfo.getPointerInfo().getLocation();

                    GraphicsDevice currentGraphicsDevice = MouseInfo.getPointerInfo().getDevice();

                    if (currentGraphicsDevice == currentDevice) {
                        Graphics g = img.getGraphics();
                        g.setColor(Color.MAGENTA);
                        g.fillRect(mouse.x - screenBounds.x, mouse.y - screenBounds.y, 16, 16);
                    }

                    ImageIO.write(img, "jpg", outstream);

                    
                    socket.close();
                    outstream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

}
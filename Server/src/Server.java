import java.io.*;
import java.net.*;
import java.util.Properties;

public class Server {
    Socket sc;
    Properties properties;
    private static final String PROPERTIES_FILE = "config.properties";
    int port;

    public Server() {
        properties = new Properties();
    }

    public void startServer() {

        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(input);
            port = Integer.parseInt(properties.getProperty("puerto"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al leer el archivo de configuración. Se usarán valores predeterminados.");
        }

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Servidor iniciado en el puerto: " + port);

            while (true) {
                sc = server.accept(); // Espera a que el cliente se conecte
                System.out.println("Cliente conectado desde " + sc.getRemoteSocketAddress());

                new Thread(new ClientHandler(sc)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server servidor = new Server();
        servidor.startServer();
    }
}

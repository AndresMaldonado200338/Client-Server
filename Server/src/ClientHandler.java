import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private static final String MESSAGES_FILE = "messages.txt";

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream()) {

            // Leer el comando o mensaje del cliente
            byte[] buffer = new byte[1024];
            int bytesRead = input.read(buffer);

            if (bytesRead > 0) {
                String received = new String(buffer, 0, bytesRead).trim();
                System.out.println("Mensaje recibido del cliente: " + received);

                if (received.equals("MOSTRAR")) {
                    // Si el cliente pide mostrar mensajes, los enviamos
                    String mensajes = getMessages();
                    output.write(mensajes.getBytes());
                    System.out.println("Mensajes enviados al cliente.");
                } else {
                    // Si el cliente envía un mensaje, lo guardamos
                    saveMessage(received);
                    System.out.println("Mensaje guardado: " + received);
                }
            }

            socket.close();
            System.out.println("Conexión con el cliente cerrada.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void saveMessage(String message) throws IOException {
        try (FileWriter writer = new FileWriter(MESSAGES_FILE, true)) {
            writer.write(message + "\n");
        }
    }

    private synchronized String getMessages() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}

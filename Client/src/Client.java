import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client {
    private static String ipServidor;
    private static int puertoServidor;

    public static void main(String[] args) {
        if (mostrarPanelConexion()) {
            crearInterfaz();
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo conectar al servidor.");
        }
    }

    private static boolean mostrarPanelConexion() {
        JPanel panelConexion = new JPanel();
        JTextField campoIp = new JTextField(15);
        JTextField campoPuerto = new JTextField(5);

        panelConexion.add(new JLabel("IP:"));
        panelConexion.add(campoIp);
        panelConexion.add(Box.createHorizontalStrut(15));
        panelConexion.add(new JLabel("Puerto:"));
        panelConexion.add(campoPuerto);

        int resultado = JOptionPane.showConfirmDialog(null, panelConexion,
                "Ingrese la IP y el Puerto", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            ipServidor = campoIp.getText();
            try {
                puertoServidor = Integer.parseInt(campoPuerto.getText());
                return probarConexion();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Puerto inválido.");
            }
        }
        return false;
    }

    private static boolean probarConexion() {
        try (Socket socket = new Socket(ipServidor, puertoServidor)) {
            System.out.println("Conexión establecida con el servidor.");
            return true;
        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor.");
            return false;
        }
    }

    private static void crearInterfaz() {
        JFrame frame = new JFrame("Cliente");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Campo de texto y botón enviar
        JTextField campoTexto = new JTextField(20);
        JButton enviarBtn = new JButton("Enviar Mensaje");
        enviarBtn.addActionListener(e -> {
            enviarMensaje(campoTexto.getText());
            System.out.println("Mensaje enviado al servidor: " + campoTexto.getText());
        });

        // Panel para mostrar mensajes
        JTextArea areaMensajes = new JTextArea(10, 30);
        areaMensajes.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaMensajes);

        // Botón mostrar mensajes
        JButton mostrarBtn = new JButton("Mostrar");
        mostrarBtn.addActionListener(e -> {
            new Thread(() -> {
                String mensajes = mostrarMensajes();
                SwingUtilities.invokeLater(() -> {
                    areaMensajes.setText(mensajes);
                    System.out.println("Mensajes recibidos del servidor:\n" + mensajes);
                });
            }).start(); // Ejecutar la recepción de mensajes en un nuevo hilo
        });

        // Añadir componentes al panel principal
        panel.add(campoTexto);
        panel.add(enviarBtn);
        panel.add(mostrarBtn);
        panel.add(scrollPane);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void enviarMensaje(String mensaje) {
        try (Socket socket = new Socket(ipServidor, puertoServidor);
                OutputStream output = socket.getOutputStream()) {

            output.write(mensaje.getBytes());
            System.out.println("Mensaje enviado correctamente.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String mostrarMensajes() {
        StringBuilder mensajes = new StringBuilder();
        try (Socket socket = new Socket(ipServidor, puertoServidor);
                OutputStream output = socket.getOutputStream();
                InputStream input = socket.getInputStream()) {

            // Enviamos el comando "MOSTRAR" al servidor
            output.write("MOSTRAR".getBytes());

            // Esperamos la respuesta del servidor
            byte[] buffer = new byte[1024];
            int bytesRead = input.read(buffer);
            mensajes.append(new String(buffer, 0, bytesRead));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return mensajes.toString();
    }
}

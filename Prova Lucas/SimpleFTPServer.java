import java.io.*;
import java.net.*;

/**
 * SimpleFTPServer - Um servidor FTP básico que aceita conexões e comandos FTP padrão como USER, PASS, LIST e QUIT.
 */
public class SimpleFTPServer {
    private static final int CONTROL_PORT = 2121; // Porta para controle FTP

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(CONTROL_PORT)) {
            System.out.println("Servidor FTP iniciado na porta " + CONTROL_PORT);

            while (true) {
                // Aguarda conexões de clientes
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nova conexão aceita: " + clientSocket.getInetAddress());

                // Cria uma nova thread para tratar cada cliente
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor FTP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gerencia a comunicação com um cliente FTP.
     *
     * @param clientSocket O socket do cliente conectado
     */
    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            out.write("220 Bem-vindo ao Servidor FTP Simples\r\n");
            out.flush();

            boolean isAuthenticated = false;
            String command;

            // Processa os comandos enviados pelo cliente
            while ((command = in.readLine()) != null) {
                System.out.println("Comando recebido: " + command);
                String[] parts = command.split(" ", 2);
                String cmd = parts[0].toUpperCase();
                String argument = parts.length > 1 ? parts[1] : null;

                switch (cmd) {
                    case "USER":
                        if ("user".equals(argument)) {
                            out.write("331 Password required\r\n");
                        } else {
                            out.write("530 Invalid username\r\n");
                        }
                        out.flush();
                        break;
                    case "PASS":
                        if ("pass".equals(argument)) {
                            isAuthenticated = true;
                            out.write("230 User logged in\r\n");
                        } else {
                            out.write("530 Login incorrect\r\n");
                        }
                        out.flush();
                        break;
                    case "LIST":
                        if (!isAuthenticated) {
                            out.write("530 Not logged in\r\n");
                            out.flush();
                            break;
                        }
                        handleList(out);
                        break;
                    case "QUIT":
                        out.write("221 Goodbye\r\n");
                        out.flush();
                        return; // Termina a conexão
                    default:
                        out.write("500 Command not understood\r\n");
                        out.flush();
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao comunicar com o cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar o socket do cliente: " + e.getMessage());
            }
        }
    }

    /**
     * Trata o comando LIST enviando arquivos fictícios ao cliente via conexão de dados.
     *
     * @param out O BufferedWriter para enviar respostas ao cliente
     */
    private static void handleList(BufferedWriter out) throws IOException {
        try (ServerSocket dataSocket = new ServerSocket(0)) { // Cria socket para a conexão de dados
            int port = dataSocket.getLocalPort();
            int p1 = port / 256;
            int p2 = port % 256;

            // Envia informações de modo passivo para o cliente
            out.write("227 Entering Passive Mode (127,0,0,1," + p1 + "," + p2 + ")\r\n");
            out.flush();

            // Aguarda a conexão do cliente na porta de dados
            Socket dataClientSocket = dataSocket.accept();
            try (BufferedWriter dataOut = new BufferedWriter(new OutputStreamWriter(dataClientSocket.getOutputStream()))) {
                // Envia lista fictícia de arquivos
                dataOut.write("file1.txt\r\nfile2.txt\r\n");
                dataOut.flush();
            }

            // Indica ao cliente que a transferência foi concluída
            out.write("226 Transfer complete\r\n");
            out.flush();
        }
    }
}

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor implements DatosConexion
{
    public static void main(String[] args)
    {
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(port);
            Socket socket = null;
            System.out.println("Servidor en línea");
            while (true)
            {
                try
                {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // new thread for a client
                new HiloServidor(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

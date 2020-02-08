import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HiloServidor extends Thread
{
    protected Socket socket;
    PartidaServidor partida;
    ObjectInputStream ois;

    public HiloServidor(Socket clientSocket)
    {
        this.socket = clientSocket;
    }

    private void finalizar()
    {
        try
        {
            ois.close();
            socket.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        int opcion = 0;
        boolean fin = false;

        System.out.println("Creado nuevo hilo (Jugador conectado). Datos: " + socket);
        try
        {
            partida = new PartidaServidor(new ObjectOutputStream(socket.getOutputStream()));
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        while (socket.isConnected() && !fin)
        {
            try {
                opcion = (int) ois.readObject();
            } catch (Exception e)
            {
                System.out.println("Error al leer: " + e);
            }
            System.out.println("Recibido codigo " + opcion + "  de: " + socket);
            switch (opcion)
            {
                case 1: partida.iniciarPartida();
                    break;
                case 2:
                    try {
                        partida.movimientoHumano((int) ois.readObject());
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    partida.movimientoCPU();
                    break;
                case 4:
                    partida.hayGanador();
                    break;
                case 5: partida.finalizar();
                        finalizar();
                        fin = true;
                        break;
            }
        }
    }
}

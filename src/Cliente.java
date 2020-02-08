import java.io.IOException;
import java.net.Socket;

public class Cliente implements DatosConexion
{
    public static void main(String[] args)
    {
        System.out.println("Conectando con el servidor....");
        Socket s;
        try
        {
            s = new Socket(host,port);
            System.out.println("Conexión realizada con éxito");
            PartidaCliente pc = new PartidaCliente(s);
            pc.juego();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

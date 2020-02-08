import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class PartidaCliente implements IJuego
{
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private Scanner scanner;

    private char[] tablero;
    private int ganador = 0;

    PartidaCliente(Socket s) throws IOException
    {
        socket = s;
        oos = new ObjectOutputStream(s.getOutputStream());
        ois = new ObjectInputStream(s.getInputStream());
        scanner = new Scanner(System.in);
        tablero = new char[9];
    }

    private String rayador() //funcion auxiliar para generar las lineas de arriba y abajo que cierran el tablero
    {
        String resultado = "-";
        String raya = "----";
        for (int indice = 0; indice < 3; indice++)
        {
            resultado = resultado.concat(raya);
        }
        return resultado;
    }

    private void dibujarTablero()
    {
        System.out.println(rayador());
        for (int indice = 0; indice < 9; indice++)
        {
            System.out.printf("| %c ", tablero[indice]);
            if((indice % 3) == 2)
                System.out.println("|");
        }
        System.out.println(rayador());
    }

    public void juego() {
        System.out.println("Bienvenido al tres en raya");
        do {
            System.out.println("¡La partida comienza!");
            iniciarPartida();
            dibujarTablero();
            do {
                movimientoHumano(realizarMovimiento());
                dibujarTablero();
                hayGanador();
                if(ganador == NO_GANADOR)
                {
                    System.out.println("Turno de la CPU");
                    movimientoCPU();
                    dibujarTablero();
                    hayGanador();
                }
            }while (ganador == NO_GANADOR);
            resultados();
        }while (rejugar());

        finalizar();
    }

    public void finalizar()
    {
        try {
            oos.writeObject(5);
            ois.close();
            oos.close();
            socket.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean rejugar()
    {
        System.out.println("¿Desea jugar de nuevo? Si para jugar, otra cosa para no");
        String respuesta = scanner.next();
        return respuesta.toUpperCase().equals("SI");
    }

    private void resultados()
    {
        switch (ganador){
            case GANADOR_JUGADOR: System.out.println("¡Has ganado!");
                    break;
            case GANADOR_CPU: System.out.println("¡Has perdido!");
                    break;
            default:
                System.out.println("Se ha producido un empate");
        }
    }

    @Override
    public void iniciarPartida()
    {
        try
        {
            oos.writeObject(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ganador = 0;
        for(int indice = 0; indice < 9; indice++)
        {
            tablero[indice] = CARACTER_POR_DEFECTO;
        }
    }

    @Override
    public void movimientoHumano(int posicion)
    {
        try
        {
            oos.writeObject(2);
            oos.writeObject(posicion);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void movimientoCPU()  {
        try {
            oos.writeObject(3);
            tablero[(int) ois.readObject()] = CPU;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hayGanador()
    {
        try {
            oos.writeObject(4);
            ganador = (int) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int realizarMovimiento()
    {
        int fila, columna, posicion;
        boolean datosCorrectos = true;
        do {
            if(!datosCorrectos)
                System.out.println("La casilla anterior no es válida. Inténtelo de nuevo");
            System.out.printf("Introduce la fila. Recuerde, de 1 a 3\n");
            fila = scanner.nextInt() - 1;
            System.out.printf("Introduce la columna. Recuerde, de 1 a 3\n");
            columna = scanner.nextInt() - 1;
            posicion = (fila * 3) + columna;
            datosCorrectos = revisarMovimiento(posicion);
        } while (!datosCorrectos);
        tablero[posicion] = JUGADOR;
        return posicion;
    }

    private boolean revisarMovimiento(int posicion)
    {
        return (posicion < 9 && posicion >= 0 && tablero[posicion] == CARACTER_POR_DEFECTO);
    }

}

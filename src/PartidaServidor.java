import java.io.IOException;
import java.io.ObjectOutputStream;

public class PartidaServidor implements IJuego
{
    private ObjectOutputStream oos;
    private char[] tablero;
    private final int[] mejoresMovimientos  = { 4, 0, 2, 6, 8, 1, 3, 5, 7 };
    private int movimientos;

    PartidaServidor(ObjectOutputStream oos) {
        this.oos = oos;
        tablero = new char[9];
    }

    @Override
    public void iniciarPartida()
    {
        for(int indice = 0; indice < 9; indice++)
        {
            tablero[indice] = CARACTER_POR_DEFECTO;
        }
        movimientos = 0;
    }

    @Override
    public void movimientoHumano(int posicion)
    {
        tablero[posicion] = JUGADOR;
        movimientos++;
    }

    @Override
    public void movimientoCPU()
    {
        boolean movimientoRealizado = false;
        int casillaOcupada = -1;
        for(int indice = 0; indice < 9 && !movimientoRealizado; indice++)
        {
            if(tablero[mejoresMovimientos[indice]] == CARACTER_POR_DEFECTO)
            {
                casillaOcupada = mejoresMovimientos[indice];
                tablero[casillaOcupada] = CPU;
                movimientos++;
                movimientoRealizado = true;
            }
        }
        try {
            oos.writeObject(casillaOcupada);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hayGanador()
    {
        int ganador = evaluaHorizontal();
        if(ganador == NO_GANADOR)
            ganador = evaluaVertical();
        if(ganador == NO_GANADOR)
            ganador = evaluaDiagonalPrincipal();
        if(ganador == NO_GANADOR)
            ganador = evaluaDiagonalSecundaria();
        if(ganador == NO_GANADOR && !movimientoPosible())
            ganador = EMPATE;
        try
        {
            oos.writeObject(ganador);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finalizar() {
        try
        {
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean movimientoPosible()
    {
        return movimientos < 9;
    }

    private int evaluaHorizontal()
    {
        int ganador = NO_GANADOR;
        for(int fila = 0; fila < 3 && ganador == NO_GANADOR; fila++)
        {
            if((tablero[fila * 3] != CARACTER_POR_DEFECTO) && (tablero[fila * 3] == tablero[(fila * 3) + 1]) && (tablero[fila * 3] == tablero[(fila * 3) + 2]))
            {
                if(tablero[fila * 3] == 'X')
                    ganador = GANADOR_JUGADOR;
                else
                    ganador = GANADOR_CPU;
            }
        }
        return ganador;
    }

    private int evaluaVertical()
    {
        int ganador = NO_GANADOR;
        for(int columna = 0; columna < 3 && ganador == NO_GANADOR; columna++)
        {
            if((tablero[columna] != CARACTER_POR_DEFECTO) && (tablero[columna] == tablero[columna + 3]) && (tablero[columna] == tablero[columna + 6]))
            {
                if(tablero[columna] == 'X')
                    ganador = GANADOR_JUGADOR;
                else
                    ganador = GANADOR_CPU;
            }
        }
        return ganador;
    }

    private int evaluaDiagonalPrincipal()
    {
        int ganador = NO_GANADOR;
        if((tablero[0] != CARACTER_POR_DEFECTO) && (tablero[0] == tablero[4]) && (tablero[0] == tablero[8]))
        {
            if(tablero[0] == 'X')
                ganador = GANADOR_JUGADOR;
            else
                ganador = GANADOR_CPU;
        }
        return ganador;
    }

    private int evaluaDiagonalSecundaria()
    {
        int ganador = NO_GANADOR;
        if((tablero[2] != CARACTER_POR_DEFECTO) && (tablero[2] == tablero[4]) && (tablero[2] == tablero[6]))
        {
            if(tablero[2] == 'X')
                ganador = GANADOR_JUGADOR;
            else
                ganador = GANADOR_CPU;
        }
        return ganador;
    }

}

public interface IJuego
{
    void iniciarPartida();
    void movimientoHumano(int posicion);
    void movimientoCPU();
    void hayGanador();
    void finalizar();
    char CARACTER_POR_DEFECTO = ' ';
    char JUGADOR = 'X';
    char CPU = 'O';
    int NO_GANADOR = 0;
    int GANADOR_JUGADOR = 1;
    int GANADOR_CPU = 2;
    int EMPATE = 3;
}

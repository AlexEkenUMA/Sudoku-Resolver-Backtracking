// ALUMNO: ALEJANDRO REQUENA GARCÍA
// GRUPO: 

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TableroSudoku implements Cloneable {
	
	// constantes relativas al nº de filas y columnas del tablero
	protected static final int MAXVALOR=9;
	protected static final int FILAS=9;
	protected static final int COLUMNAS=9;
	protected static final int RAIZFILAS= (int) Math.sqrt(FILAS);
	protected static final int RAIZCOLUMNAS=(int) Math.sqrt(COLUMNAS);

	protected static Random r = new Random();

	protected int celdas[][]; // una celda vale cero si est\u00E1 libre.

	public TableroSudoku() {
		celdas = new int[FILAS][COLUMNAS]; //todas a cero.
	}

	// crea una copia de su par\u00E1metro
	public TableroSudoku(TableroSudoku uno) {
		TableroSudoku otro = (TableroSudoku) uno.clone();
		this.celdas = otro.celdas;
	}

	// crear un tablero a parir de una configuraci\u00D3n inicial (las celdas vac\u00EDas
	// se representan con el caracter ".".
    public TableroSudoku(String s) {
    	this();
    	if(s.length() != FILAS*COLUMNAS) {
    		throw new RuntimeException("Construcci\u00D3n de sudoku no v\u00E1lida.");
    	} else {
    		for(int f=0;f<FILAS;f++)
				for(int c=0;c<COLUMNAS;c++) {
					Character ch = s.charAt(f*FILAS+c);
					celdas[f][c] = (Character.isDigit(ch) ? Integer.parseInt(ch.toString()) : 0 );
				}
		}
    }


	/* Realizar una copia en profundidad del objeto
	 * @see java.lang.Object#clone()
	 */
	public Object clone()  {
		TableroSudoku clon;
		try {
			clon = (TableroSudoku) super.clone();
			clon.celdas = new int[FILAS][COLUMNAS];
			for(int i=0; i<celdas.length; i++)
				System.arraycopy(celdas[i], 0, clon.celdas[i], 0, celdas[i].length);
		} catch (CloneNotSupportedException e) {
			clon = null;
		}
		return clon;
	}

	/* Igualdad para la clase
	 * @see java.lang.Object#equals()
	 */
	public boolean equals(Object obj) {
		if (obj instanceof TableroSudoku) {
			TableroSudoku otro = (TableroSudoku) obj;
			for(int f=0; f<FILAS; f++)
				if(!Arrays.equals(this.celdas[f],otro.celdas[f]))
					return false;
			return true;
		} else
			return false;
	}

	public String toString() {
		String s = "";

		for(int f=0;f<FILAS;f++) {
			for(int c=0;c<COLUMNAS;c++)
				s += (celdas[f][c]==0 ? "." : String.format("%d",celdas[f][c]));
		}
		return s;
	}


	// devuelva true si la celda del tablero dada por fila y columna est\u00E1 vac\u00EDa.
	protected boolean estaLibre(int fila, int columna) {
		return celdas[fila][columna] == 0;
	}

	// devuelve el número de casillas libres en un sudoku.
	protected int numeroDeLibres() {
		int n=0;
	    for (int f = 0; f < FILAS; f++)
	        for (int c = 0; c < COLUMNAS; c++)
	        	if(estaLibre(f,c))
	        		n++;
	    return n;
	}

	protected int numeroDeFijos() {
		return FILAS*COLUMNAS - numeroDeLibres();
	}

	// Devuelve true si @valor ya esta en la fila @fila.
	protected boolean estaEnFila(int fila, int valor) {
		boolean ok = false;
		for (int c = 0; c < COLUMNAS; c++){
			if(celdas[fila][c] == valor){
				ok = true;
				break;
			}
		}

		return ok;
	}

	// Devuelve true si @valor ya esta en la columna @columna.
	protected boolean estaEnColumna(int columna, int valor) {
		boolean ok = false;
		for (int f = 0; f < COLUMNAS; f++){
			if(celdas[f][columna] == valor){
				ok = true;
				break;
			}
		}

		return ok;
	}


	// Devuelve true si @valor ya esta en subtablero al que pertence @fila y @columna.
	protected boolean estaEnSubtablero(int fila, int columna, int valor) {
		boolean ok = false;
		//para saber en que subtablero estamos
		int cuadroX=fila/RAIZFILAS;
		int cuadroY=columna/RAIZCOLUMNAS;
		int posIniX=cuadroX * RAIZFILAS;//calculo la posicion inicial del subtablero en la fila
		int posIniY=cuadroY * RAIZCOLUMNAS;//posicion inicial del subtablero en la columna
		//recorremos el  subtablero en busca de algun elemento repetido

		for(int i=posIniX; i<posIniX+RAIZFILAS; i++){
			for(int j=posIniY; j<posIniY+RAIZCOLUMNAS; j++){
				if(celdas[i][j]==valor) {
					ok = true;
					break;
				}
			}
		}
		return ok;
	}


	// Devuelve true si se puede colocar el @valor en la @fila y @columna dadas.
	protected boolean sePuedePonerEn(int fila, int columna, int valor) {
		boolean ok = false;
		if(!estaEnFila(fila, valor) && !estaEnColumna(columna, valor) && !estaEnSubtablero(fila, columna, valor)){
			ok = true;
		}
		return ok;
	}




	protected void resolverTodos(List<TableroSudoku> soluciones, int fila, int columna) {
		if(numeroDeLibres() == 0){ //Hemos llegado a una solución
			soluciones.add(new TableroSudoku(this));
		}
		else if (!estaLibre(fila, columna)){ //La casilla actual esta ocupada
			if(columna == 8){
				resolverTodos(soluciones, fila+1, 0);
			}
			else{
				resolverTodos(soluciones, fila, columna+1);
			}
		}
		else{ //La casilla actual está libre
			int[] posiblesvalores = {1, 2, 3, 4, 5, 6, 7, 8, 9};
			for(int valor : posiblesvalores){
				if(sePuedePonerEn(fila, columna, valor)){
					TableroSudoku copia = new TableroSudoku(this);
					copia.celdas[fila][columna] = valor;
					if(columna == 8){
						copia.resolverTodos(soluciones, fila+1, 0);
					}
					else{
						copia.resolverTodos(soluciones, fila, columna+1);
					}
				}
			}
		}
	}

	public List<TableroSudoku> resolverTodos() {
        List<TableroSudoku> sols  = new LinkedList<TableroSudoku>();
        resolverTodos(sols, 0, 0);
		return sols;
	}

	public static void sudokuSimpleDisplay (TableroSudoku t){
		int[][] celdas = t.celdas;

		for(int i = 0; i< FILAS; i++){
			if(i % 3 == 0){
				System.out.println("- - - - - - - - - - -");
			}
			for(int j = 0; j< COLUMNAS; j++){
				if(j % 3 == 0 && j > 0){
					System.out.print("| ");
				}
				if(celdas[i][j] == 0){
					System.out.print(". ");
				}
				else{
					System.out.print(celdas[i][j] + " ");
				}

			}
			System.out.println();
		}
		System.out.println("- - - - - - - - - - -");
	}

	public static void main(String arg[]) {

		TableroSudoku t = new TableroSudoku(
				//ADD INPUT HERE (STRING FORMAT, '.' CHARACTER IS A BLANK CELL) :-)
				"8........" +
				"..36....." +
				".7..9.2.." +
				".5...7..." +
				"....457.." +
				"...1...3." +
				"..1....68" +
				"..85...1." +
				".9....4.."
		);
		System.out.println("UNSOLVED SUDOKU");
		sudokuSimpleDisplay(t);
		Temporizador crono = new Temporizador();

		crono.resetear();
		crono.iniciar();
		List<TableroSudoku> lt = t.resolverTodos();
		crono.parar();
		System.out.println("ALGORITHM EXECUTION TIME TO FIND ALL SOLUTIONS: " + TimeUnit.MILLISECONDS.convert(crono.tiempoPasado(), TimeUnit.NANOSECONDS) + " ms");
		System.out.println("DIFFERENT SOLUTIONS: " + lt.size());
		int cnt = 1;
		for(Iterator<TableroSudoku> i= lt.iterator(); i.hasNext();) {
			System.out.println("SOLUTION " + cnt);
			TableroSudoku ts = i.next();
			sudokuSimpleDisplay(ts);
			++cnt;

		}
	}

}

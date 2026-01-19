package tp_buscador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner; // maneja las entradas por teclado.

public class Main {
	public static void main(String[] args) throws IOException {
        Scanner entrada = new Scanner(System.in);

        String target;
        String start;

        System.out.print("Ingrese el nombre del archivo a buscar: ");
        target = entrada.nextLine();// lo que ingrese en la proxima linea se
        System.out.print("Ingrese el directorio inicial de búsqueda: ");
        start = entrada.nextLine();

        Path inicio = Paths.get(start);
        //Validacion de que el camino de inicio exista. 
        //Si no, que tire excepcion con mensaje de directorio invalido. 
        try {
            if (!Files.exists(inicio) || !Files.isDirectory(inicio)) {
                throw new NoSuchFileException(start);
            }

            Clasificador clasificador = new Clasificador(4, target);
            clasificador.procesarDirectorio(inicio);
            clasificador.cerrar();

        } catch (NoSuchFileException e) {
            System.out.println("Directorio inválido.");
        } finally {
			entrada.close();
		}

    }
}

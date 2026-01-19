package tp_buscador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;


public class Clasificador {
	private ExecutorService pool;
    private final AtomicBoolean found = new AtomicBoolean(false); //inicia los Hilos en 0. 
    private final String targetName;

    public Clasificador(int hilos, String targetName) {// crea el constructor de la clase. 
        this.pool = Executors.newFixedThreadPool(hilos); //designa cantidad de hilos que va a tener nuestro pool. 
        this.targetName = targetName; // guarda el nombre del archivo. 
    }

    public void procesarDirectorio(Path dir) throws IOException {
        try (Stream<Path> stream = Files.walk(dir)) { //recorre el directorio. 
            stream.forEach(path -> { //recorre cada ruta encontrada. 
                if (!found.get()) {
                    pool.submit(new Buscador(path, targetName, found, pool)); // si no encontramos, que haga una nueva busqueda. 
                }
            });
        }
    }

    public void cerrar() {
        pool.shutdown();
        try {
            // esperar razonablemente; si tarda demasiado forzar shutdown
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) { // si pasa mas de 60 segundos en terminar la tarea los hilos que estan activos, fuerza el cerrado de estos. . 
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {// Si interrumpieron al hilo que cierra todo, vuele a intentar cerrar.  
            Thread.currentThread().interrupt();
        }

        // Si después de esperar no se encontró, lanzar excepción
        if (!found.get()) {
            throw new RuntimeException("Archivo no encontrado: " + targetName);
        }
    }

}

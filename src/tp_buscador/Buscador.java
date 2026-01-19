package tp_buscador;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService; //interfaz para manejar hilos. 
import java.util.concurrent.atomic.AtomicBoolean;

public class Buscador implements Runnable{
	private Path archivo; // ruta en la que va a comenzar la busqueda del arquivo. 
    private final String targetName;
    private final AtomicBoolean found;// variable para que cuando el hilo que encuentre al archivo, no pueda ser interrumpido por otro hilo.Valor compartido entre todos los hilos.  
    private final ExecutorService pool;// crea los hilos una sola vez, le asigna una tarea y cuando terminan los deja en un estado de espera. Cuando llega otra tarea, se la asigna a este hilo que estaba esperando. 

    public Buscador(Path archivo, String targetName, AtomicBoolean found, ExecutorService pool) { // constructor de la clase. 
        this.archivo = archivo; 
        this.targetName = targetName;
        this.found = found;
        this.pool = pool;
    }

    @Override
    public void run() {
        if (found.get()) //Si otro hilo ya encontro al archivo, entonces found vale tru. Entonces retorna para que el hilo no siga trabajando. 
            return;

        try {//Entra si es el archivo que estamos buscando
            if (Files.isRegularFile(archivo) && archivo.getFileName().toString().equalsIgnoreCase(targetName)) { // Si lo que encontramos es un archivo y luego compara si el nombre del archivo encontrado coincide con el nombre del archivo que estamos buscando. 
                System.out.println("[Buscador] Encontrado por: " + Thread.currentThread().getName() + ".Ruta: "
                        + archivo.toAbsolutePath()); // imprime el hilo que lo encontro junto con el camino hacia donde esta ese archivo. 
                found.set(true); // aca pone la bandera en true para que el resto de hilos dejen de trabajar. 
                pool.shutdownNow(); // detener otras tareas lo antes posible. 
            }
            //else {
                //System.out.println("No es el archivo buscado");
            //} Esta seccion de codigo sera inservible ya que imprimiria "No es el archivo buscado" hasta que un hilo encuentre el archivo buscado. 

        } catch (Exception e) {//Muestra el error y que no existe un archivo en caso de error. 
            // ignorar errores de acceso para no llenar la salida
        }
    }
}

//Se utilizo ExecutorService para optimizar el uso delos recursos, ya que al usar hilos de manera manual con start y close se debian crear hilos manualmente cada vez que se quieran ingresar a un archivo/directorio para buscar lo que querramos. 
//O sea, si se quiere buscar en un muy grande se crearan muchos hilos, resultando de manera costosa el uso de CPU.
//Te ahorra estar creando/eliminando hilos constantemente. 
//Fuente: https://www.youtube.com/watch?v=hJ31aSbe-Bs


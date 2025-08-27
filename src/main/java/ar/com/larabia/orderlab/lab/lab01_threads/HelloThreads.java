package ar.com.larabia.orderlab.lab.lab01_threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lab01: hilos "a mano".
 * Objetivo: crear N hilos que incrementan un contador compartido y esperar a que terminen.
 * Conceptos: start() vs run(), join(), ThreadFactory, AtomicInteger (sin race condition).
 */
public class HelloThreads {

    /**
     * Lanza 'workers' hilos; cada hilo incrementa el contador 'iterations' veces.
     * Devuelve el valor final del contador cuando todos terminaron.
     */
    public static int doWorkConcurrently(int workers, int iterations) {
        // Estado compartido entre hilos. ¿Por qué AtomicInteger?
        //   - El incremento x = x + 1 NO es atómico (leer-sumar-escribir).
        //   - AtomicInteger.incrementAndGet() usa CAS (compare-and-swap) → atómico + visible.
        AtomicInteger counter = new AtomicInteger(0);

        // ThreadFactory: centraliza cómo creamos hilos (nombres útiles para debug).
        ThreadFactory tf = new ThreadFactory() {
            private final AtomicInteger seq = new AtomicInteger(1);
            @Override public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "lab01-" + seq.getAndIncrement());
                // Daemon vs non-daemon:
                //   - daemon: la JVM puede apagarse aunque el hilo siga vivo (background).
                //   - non-daemon (false): la JVM espera a que terminen → útil para labs y pruebas.
                t.setDaemon(false);
                return t;
            }
        };

        // Creamos y arrancamos los hilos
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < workers; i++) {
            // El "trabajo" de cada hilo: hacer 'iterations' incrementos atómicos
            Thread t = tf.newThread(() -> {
                for (int j = 0; j < iterations; j++) {
                    counter.incrementAndGet(); // operación atómica (sin perder incrementos)
                }
            });

            threads.add(t);
            // start() crea un hilo NUEVO y ejecuta el Runnable allí.
            // Si llamaras run(), se ejecutaría en el hilo actual → NO hay concurrencia.
            t.start();
        }

        // join(): sincronización básica → el hilo actual espera a que cada hilo termine
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // Si nos interrumpen mientras esperamos, restauramos el flag y fallamos de forma controlada.
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrumpido esperando join", e);
            }
        }

        // Si no hubo pérdidas (gracias a AtomicInteger), total = workers * iterations
        return counter.get();
    }
}
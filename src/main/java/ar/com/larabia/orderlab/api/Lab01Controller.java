package ar.com.larabia.orderlab.api;

import ar.com.larabia.orderlab.lab.lab01_threads.HelloThreads;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/lab/01") // prefijo del laboratorio 01
public class Lab01Controller {

    @GetMapping("/run") // GET /lab/01/run?workers=4&iterations=1000
    public Map<String, Object> run(
            @RequestParam(defaultValue = "4") int workers,
            @RequestParam(defaultValue = "1000") int iterations) {

        int total = HelloThreads.doWorkConcurrently(workers, iterations);
        return Map.of(
                "workers", workers,
                "iterations", iterations,
                "expected", workers * iterations, // si no se pierden incrementos
                "result", total,                  // lo que realmente obtuvimos
                "ok", total == workers * iterations
        );
    }
}
package ar.com.larabia.orderlab.lab.lab01_threads;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class HelloThreadsTest {

    @Test
    void counterShouldReachExpectedValue() {
        int workers = 8, iterations = 5_000;
        int total = HelloThreads.doWorkConcurrently(workers, iterations);
        assertThat(total).isEqualTo(workers * iterations);
    }
}
package cl.taller;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CalculadoraTest {
    @Test
    public void sumaDosEnteros() {
        assertEquals(8, new Calculadora().sumar(5, 3));
    }

    @Test
    public void restaDosEnteros() {
        assertEquals(2, new Calculadora().restar(5, 3));
    }
}

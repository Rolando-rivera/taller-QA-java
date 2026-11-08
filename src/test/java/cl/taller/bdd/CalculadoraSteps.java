package cl.taller.bdd;
import cl.taller.Calculadora;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.Assert.assertEquals;
public class CalculadoraSteps {
    private final Calculadora calculadora = new Calculadora();
    private int primero;
    private int segundo;
    private int resultado;
    @Given("los operandos {int} y {int}")
    public void definirOperandos(int primero, int segundo) {
        this.primero = primero;
        this.segundo = segundo;
    }
    @When("sumo los operandos")
    public void sumar() {
        resultado = calculadora.sumar(primero, segundo);
    }
    @When("resto los operandos")
    public void restar() {
        resultado = calculadora.restar(primero, segundo);
    }
    @Then("el resultado es {int}")
    public void verificarResultado(int esperado) {
        assertEquals(esperado, resultado);
    }
}

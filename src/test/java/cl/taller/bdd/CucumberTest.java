package cl.taller.bdd;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features",
    glue = "cl.taller.bdd",
    plugin = {"pretty", "html:target/cucumber.html"},
    monochrome = true
)
public class CucumberTest {
}

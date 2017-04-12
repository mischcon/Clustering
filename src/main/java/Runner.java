import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;


public class Runner {
    public static void main(String[] args) {
        Computer computer = new Computer();
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.run(computer, JUnitTests.class);
    }
}

import java.lang.reflect.Method;

/**
 * Created by mischcon on 27.03.2017.
 */
public class TestClass {

    public void TestMethod(){
        System.out.println("TestMethod");
    }

    public Method getTestMethod() throws NoSuchMethodException {
        Method m = this.getClass().getMethod("TestMethod");
        return m;
    }
}

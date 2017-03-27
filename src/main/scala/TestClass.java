import java.lang.reflect.Method;

/**
 * Created by mischcon on 27.03.2017.
 */
public class TestClass {

    public void TestMethodSuccess(){
        System.out.println("JAVA REFLECTION INVOCATION SUCCESS - TestMethod");
    }

    public void TestMethodFail(){
        System.out.println("JAVA REFLECTION INVOCATION FAIL - TestMethod");

        throw new ArithmeticException();
    }

    public Method getTestMethodSuccess() throws NoSuchMethodException {
        Method m = this.getClass().getMethod("TestMethodSuccess");
        return m;
    }
    public Method getTestMethodFail() throws NoSuchMethodException {
        Method m = this.getClass().getMethod("TestMethodFail");
        return m;
    }
}

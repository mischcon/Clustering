/**
 * Created by mischcon on 3/20/17.
 */
public class test {

    @Clustering(Path= "nope")
    public int test(){
        System.out.print("kartoffel");
        return 13;
    }

    public static void main(String[] args){
        int i = test();
    }
}

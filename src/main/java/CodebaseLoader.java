import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class TestEntry {
    String classname;
    String methodname;
    Clustering annotation;

    public TestEntry(String classname, String methodname, Clustering annotation) {
        this.classname = classname;
        this.methodname = methodname;
        this.annotation = annotation;
    }
}

class TestingCodebaseLoader extends ClassLoader{

    private String path;

    private Map<String, Class> classRaw = new HashMap<>();
    private List<TestEntry> classClusterMethods = new LinkedList<>();

    private byte[] jar;

    public TestingCodebaseLoader(String path) throws IOException {
        this(Files.readAllBytes(Paths.get(path)));
        this.path = path;
    }

    public TestingCodebaseLoader(byte[] jar) throws IOException {
        this.jar = jar;
        createClassRawMap(jar);
        getMethods();
    }

    private void createClassRawMap(byte[] jar) throws IOException {
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(jar));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                String classname = entry.getName().replace('/', '.');
                classname = classname.substring(0, classname.length() - ".class".length());
                ByteArrayOutputStream bout = new ByteArrayOutputStream();

                int len;
                byte[] buffer = new byte[1024];
                while((len = zip.read(buffer)) > 0){
                    bout.write(buffer, 0, len);
                }
                bout.close();
                byte[] raw_class = bout.toByteArray();
                Class cls = defineClass(classname, raw_class, 0, raw_class.length);
                classRaw.put(classname, cls);
            }
        }
    }

    private void getMethods() {
        for(String key : classRaw.keySet()){
            Class cls = classRaw.get(key);
            for(Method m : cls.getMethods()){
                Annotation an = m.getAnnotation(Clustering.class);
                if(an != null) {
                    classClusterMethods.add(new TestEntry(cls.getName(), m.getName(), (Clustering) an));
                }
            }
        }
    }

    public List<TestEntry> getClassClusterMethods(){
        return this.classClusterMethods;
    }

    public Class getTestClass(String classname){
        return this.classRaw.get(classname);
    }
}

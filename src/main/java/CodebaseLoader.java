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

class TestingCodebaseLoader extends ClassLoader{

    private String path;

    private Map<String, Class> classRaw = new HashMap<>();
    private Map<String, List<String>> classClusterMethods = new HashMap<>();

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
                    if(!classClusterMethods.containsKey(key))
                        classClusterMethods.put(key, new LinkedList<>());
                    List list = classClusterMethods.get(key);
                    list.add(m.getName());
                    classClusterMethods.put(key, list);
                }
            }
        }
    }

    public Map<String, List<String>> getClassClusterMethods(){
        return this.classClusterMethods;
    }

    public Class getTestClass(String classname){
        return this.classRaw.get(classname);
    }
}

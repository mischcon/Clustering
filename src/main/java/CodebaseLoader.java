import sun.misc.IOUtils;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class TestingCodebaseLoader extends ClassLoader{

    String path;

    Map<String, Class> classRaw = new HashMap<>();
    Map<String, String> classClusterMethods = new HashMap<>();

    byte[] jar;

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
                if(an != null)
                    classClusterMethods.put(key, m.getName());
            }
        }
    }

    public Map<String, String> getClassClusterMethods(){
        return this.classClusterMethods;
    }

    public Class getTestClass(String classname){
        return this.classRaw.get(classname);
    }
}

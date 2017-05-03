package de.oth.clustering.java;

import vm.VmEnvironment;
import vm.vagrant.configuration.VagrantEnvironmentConfig;
import vm.vagrant.configuration.VagrantVmConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by mischcon on 22.04.2017.
 */
public abstract class BaseCodebaseLoader<T extends Entry> extends ClassLoader implements CodebaseLoaderIF {

    String path;

    Map<String, byte[]> classRaw = new HashMap<>();
    List<T> classClusterMethods = new LinkedList<>();
    List<Class> classList = new LinkedList<>();

    byte[] jar;

    public BaseCodebaseLoader(){}

    public BaseCodebaseLoader(String path) throws IOException {
        this(Files.readAllBytes(Paths.get(path)));
        this.path = path;
    }

    public BaseCodebaseLoader(byte[] jar) throws IOException {
        this.jar = jar;
        createClassRawMap(jar);
        fillMethodsList();
    }

    public void createClassRawMap(byte[] jar) throws IOException {
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
                classRaw.put(classname, raw_class);
            }
        }

        getClasses();
    }

    private void getClasses(){
        for(String key : classRaw.keySet()) {
            byte[] raw = classRaw.get(key);
            Class cls = defineClass(key, raw, 0, raw.length);
            classList.add(cls);
        }
    }

    public Class getClassFromByte(byte[] raw_class, String classname){
        return defineClass(classname, raw_class, 0, raw_class.length);
    }

    public List<T> getClassClusterMethods(){
        return this.classClusterMethods;
    }

    public VagrantEnvironmentConfig getVmConfig() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class configClass = null;
        for(Class cls : classList){
            if(VmEnvironment.class.isAssignableFrom(cls)){
                if(configClass != null){
                    throw new ClassNotFoundException("multiple classes that implement VmEnvironment.class found") ;
                }
                configClass = cls;
            }
        }
        if(configClass == null)
            throw new ClassNotFoundException("no class that implement VmEnvironment.class found") ;

        VmEnvironment env = (VmEnvironment)configClass.newInstance();
        return env.createEnvironment();
    }

}


package de.oth.clustering.java.utils;

import de.oth.clustering.java.vm.VmEnvironment;
import de.oth.clustering.java.vm.vagrant.configuration.VagrantEnvironmentConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Baseclass for loading jar files that contain tasks and configs
 * @param <T> Defines the container type - needs to be extended from {@link Entry Entry}
 */
public abstract class BaseCodebaseLoader<T extends Entry> extends ClassLoader implements CodebaseLoaderIF {

    String path;

    Map<String, byte[]> classRaw = new HashMap<>();
    List<T> classClusterMethods = new LinkedList<>();
    List<Class> classList = new LinkedList<>();

    byte[] jar;

    public BaseCodebaseLoader(){}

    /**
     * BasCodebaseLoader constructor - loads classes from jar file
     * @param path path to jar
     * @throws IOException
     */
    public BaseCodebaseLoader(String path) throws IOException {
        this(Files.readAllBytes(Paths.get(path)));
        this.path = path;
    }

    /**
     * BaseCodebaseLoader constructor - loads classes from the ByteArray representation of a jar file
     * @param jar ByteArray representation of a jar file
     * @throws IOException
     */
    public BaseCodebaseLoader(byte[] jar) throws IOException {
        this.jar = jar;
        createClassRawMap(jar);
        fillMethodsList();
    }

    /**
     * Opens the ByteArray representation of a jar file and creates a 'classname' -> 'ByteArray representation of the class' map
     * @param jar ByteArray representation of a jar file
     * @throws IOException
     */
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

    /**
     * Creates a Class from a given ByteArray representation of a class and the classname
     * @param raw_class ByteArray representation of a class
     * @param classname Name of the class
     * @return Class
     */
    public Class getClassFromByte(byte[] raw_class, String classname){
        return defineClass(classname, raw_class, 0, raw_class.length);
    }

    /**
     * Returns a list of all cluster tasks / methods
     * @return
     */
    public List<T> getClassClusterMethods(){
        return this.classClusterMethods;
    }

    /**
     * Returns the VM deploy info used for deploying virtual machines
     * @return
     * @throws ClassNotFoundException Either no or more than one deploy info was found
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public DeployInfoInterface getVmConfig() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
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
        VagrantEnvironmentConfig vaenv = env.createEnvironment();
        if(vaenv.version() == null){
            vaenv.setVersion(Objects.toString(new Random().nextLong()));
        }
        return vaenv;
    }

}


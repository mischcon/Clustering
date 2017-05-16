package de.oth.clustering.java.utils;

import de.oth.clustering.java.clustering.Clustering;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Example class that uses the {@link BaseCodebaseLoader} in combination with
 * the {@link TestEntry TestEntry} that extends the regular {@link Entry Entry}.
 */
public class TestingCodebaseLoader extends BaseCodebaseLoader<TestEntry>{

    public TestingCodebaseLoader(){}

    public TestingCodebaseLoader(String path) throws IOException {
        super(path);
    }

    public TestingCodebaseLoader(byte[] jar) throws IOException {
        super(jar);
    }

    @Override
    public void fillMethodsList() {
        for(Class cls : classList){
            for(Method m : cls.getMethods()){
                Annotation an = m.getAnnotation(Clustering.class);
                if(an != null) {
                    classClusterMethods.add(new TestEntry(cls.getName(), m.getName(), (Clustering) an));
                }
            }
        }
    }

    public byte[] getRawTestClass(String classname){
        return this.classRaw.get(classname);
    }
}

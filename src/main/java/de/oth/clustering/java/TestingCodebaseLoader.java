package de.oth.clustering.java;

import clustering.Clustering;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Example class that uses the {@link de.oth.clustering.java.BaseCodebaseLoader} in combination with
 * the {@link de.oth.clustering.java.TestEntry TestEntry} that extends the regular {@link de.oth.clustering.java.Entry Entry}.
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

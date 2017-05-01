package de.oth.clustering.java;

import clustering.Clustering;

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

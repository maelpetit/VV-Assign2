package fr.istic.vv;

import javassist.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MyTranslator implements Translator {

    private Set<CtClass> ctClasses = new HashSet<CtClass>();

    public void start(ClassPool classPool) throws NotFoundException, CannotCompileException {
    }

    public void onLoad(ClassPool classPool, String className) throws NotFoundException, CannotCompileException {
        ctClasses.add(classPool.get(className));
    }

    public Set<CtClass> getCtClasses() {
        return ctClasses;
    }
}

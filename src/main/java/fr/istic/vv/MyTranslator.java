package fr.istic.vv;

import javassist.*;

public class MyTranslator implements Translator {

    public void start(ClassPool classPool) throws NotFoundException, CannotCompileException {
        System.out.println("Starting");
    }

    public void onLoad(ClassPool classPool, String className) throws NotFoundException, CannotCompileException {
        if(className.contains("Addition")){
            CtMethod operate = classPool.get(className).getDeclaredMethod("operate");
            //operate.setBody("return FirstTerm - SecondTerm;");
        }

    }
}

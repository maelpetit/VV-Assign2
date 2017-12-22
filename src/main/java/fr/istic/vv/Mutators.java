package fr.istic.vv;

import javassist.*;
import javassist.bytecode.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Mutators {

    public static boolean HAS_MUTATED;

    public static CtClass replaceReturnInDoubleMethods(CtClass ctClass) throws CannotCompileException, ClassNotFoundException, NotFoundException {
        if(ctClass.isInterface()){
            return ctClass;
        }
        for(CtMethod ctMethod : ctClass.getDeclaredMethods()){
            if(ctMethod.getReturnType().getName().equals("double")){
                ctMethod.instrument(new ExprEditor() {
                    @Override
                    public void edit(FieldAccess f) throws CannotCompileException {
                        f.replace("{ $_ = 0; }");
                    }
                });
                System.out.println("Mutators.replaceReturnInDoubleMethods return 0");
                HAS_MUTATED = true;

            }
        }

        return ctClass;
    }

    public static CtClass setBooleanMethodsTo(CtClass ctClass, final boolean bool){
        try{
            if(ctClass.isInterface()){
                return ctClass;
            }

            for(CtMethod ctMethod : ctClass.getDeclaredMethods()){
                try {
                    if(ctMethod.getReturnType().getName().equals("boolean")){
                        ctMethod.setBody("return " + bool + ";");
                        System.out.println("Mutators.setBooleanMethodsTo " + bool);
                        HAS_MUTATED = true;
                    }
                } catch (NotFoundException | NullPointerException | CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        }catch(RuntimeException e){
            e.printStackTrace();
        }
        return ctClass;
    }

    public static CtClass replace(CtClass ctClass, int oldBytecode, int newBytecode){
        if(ctClass.isInterface()){
            return ctClass;
        }

        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            replace(ctMethod, oldBytecode, newBytecode);
        }

        return ctClass;
    }

    private static CtMethod replace(CtMethod ctMethod, int oldBytecode, int newBytecode){
        try{
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeIterator codeIterator = methodInfo.getCodeAttribute().iterator();
            while(codeIterator.hasNext()){
                int pos = codeIterator.next();
                if(codeIterator.byteAt(pos) == oldBytecode){
                    codeIterator.writeByte(newBytecode, pos);
                    System.out.println("Mutators.replace bytecode " + oldBytecode + " -> " + newBytecode);
                    HAS_MUTATED = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return ctMethod;
    }

    public static void deleteTargetClasses(Set<CtClass> toDelete, File classDir){
        for(CtClass ctClass : toDelete){
            ctClass.defrost();
            String fileName = ctClass.getName().replace('.', '/') + ".class";
            File file = new File(classDir + "/" + fileName);
            file.delete();
        }

    }
}

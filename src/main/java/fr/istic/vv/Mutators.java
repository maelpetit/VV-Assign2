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
                        System.out.println("Mutators.setBooleanMethodsTo body modified");
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        }catch(RuntimeException e){
            e.printStackTrace();
        }
        return ctClass;
    }

    public static CtClass arithmeticMutations(CtClass ctClass) throws BadBytecode {
        if(ctClass.isInterface()){
            return ctClass;
        }
        int oldBytecode = 0;
        int newBytecode = 0;
        if(ctClass.getName().contains("Addition")) {
            oldBytecode = Opcode.DADD;
            newBytecode = Opcode.DSUB;
        }else if(ctClass.getName().contains("Subtraction")){
            oldBytecode = Opcode.DSUB;
            newBytecode = Opcode.DADD;
        }else if(ctClass.getName().contains("Multiplication")){
            oldBytecode = Opcode.DMUL;
            newBytecode = Opcode.DDIV;
        }else if(ctClass.getName().contains("Division")){
            oldBytecode = Opcode.DDIV;
            newBytecode = Opcode.DMUL;
        }

        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            replace(ctMethod, oldBytecode, newBytecode);
        }

        return ctClass;
    }

    private static CtMethod replace(CtMethod ctMethod, int oldBytecode, int newBytecode) throws BadBytecode {
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeIterator codeIterator = methodInfo.getCodeAttribute().iterator();
        while(codeIterator.hasNext()){
            int pos = codeIterator.next();
            if(codeIterator.byteAt(pos) == oldBytecode){
                codeIterator.writeByte(newBytecode, pos);
            }
        }
        return ctMethod;
    }

    public static void deleteTargetClasses(Set<CtClass> toDelete, File classDir){
        for(CtClass ctClass : toDelete){
            ctClass.defrost();
            String fileName = ctClass.getName().replace('.', '/') + ".class";
            File file = new File(classDir + "/" + fileName);
            System.out.println(file.getPath());
            System.out.println(file.exists());
            file.delete();
        }

    }
}

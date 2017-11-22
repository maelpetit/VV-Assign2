package fr.istic.vv;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.io.File;
import java.util.Set;

public class Mutators {

    public static CtClass replaceReturnInDoubleMethods(CtClass ctClass){
        for(CtMethod ctMethod : ctClass.getDeclaredMethods()){
            try {
                if(ctMethod.getReturnType().getName().equals("double")){
                    ctMethod.instrument(new ExprEditor() {
                        @Override
                        public void edit(FieldAccess f) throws CannotCompileException {
                            f.replace("{ $_ = 0; }");
                        }
                    });
                }
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }

        return ctClass;
    }

    public static CtClass setBooleanMethodsTo(CtClass ctClass, final boolean bool){
        if(ctClass.isInterface()){
            return ctClass;
        }
        for(CtMethod ctMethod : ctClass.getDeclaredMethods()){
            try {
                if(ctMethod.getReturnType().getName().equals("boolean")){
                    ctMethod.setBody("return " + bool + ";");
                    /*ctMethod.instrument(new ExprEditor() {
                        @Override
                        public void edit(MethodCall mc) throws CannotCompileException {
                            mc.replace("{ $_ = true; }");
                        }
                    });*/
                }
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
        return ctClass;
    }

    public static void deleteTargetClasses(Set<CtClass> toDelete){
        File target = new File("target/classes/fr/istic/vv");
        for(String file : target.list()){
            for(CtClass ctClass : toDelete){
                if(file.contains(ctClass.getSimpleName())){
                    File currentFile = new File(target.getPath(),file);
                    currentFile.delete();
                }
            }
        }
    }
}

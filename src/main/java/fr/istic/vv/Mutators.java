package fr.istic.vv;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import java.io.File;

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

    public static void revertAllChanges(){
        File target = new File("target/classes/fr/istic/vv");
        for(String file : target.list()){
            File currentFile = new File(target.getPath(),file);
            currentFile.delete();
        }
    }
}

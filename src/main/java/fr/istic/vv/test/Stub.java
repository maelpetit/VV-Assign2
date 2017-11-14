package fr.istic.vv.test;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Loader;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class Stub {
    public static void main(String[] args) {
        try {
            // load the class into the ClassPool.  this causes the system
            // classloader to load Foo, making it unmodifiable.  it makes no
            // difference to use a literal string here instead of referencing
            // the class; ClassPool loads it using the system classloader.
            final CtClass ctClass = ClassPool.getDefault().get(Foo.class.getName());
            final CtMethod ctMethod = ctClass.getDeclaredMethod("setValue");
            ctMethod.instrument(new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    // modify the class to print a message when the setter is called
                    f.replace("{ System.out.println(\"setter called\"); $_ = $proceed($$); }");
                }
            });

            ctClass.writeFile();

            // create a classloader that gets its class definitions from the ClassPool
            Loader loader = new Loader(ClassPool.getDefault());

            // dynamically load a class in which test.Foo is the modified one
            Class<?> klass = loader.loadClass("fr.istic.vv.test.Test2");
            Runnable r = (Runnable) klass.newInstance();
            r.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}


package fr.istic.vv.test;

public class Test2 implements Runnable {

    public void run() {
        Foo foo = new Foo();
        foo.setValue(42);
        System.out.println(foo.getValue());
    }
}

package jniTest;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/30 10:40
 */
public class HelloJNI {
    static {
        System.loadLibrary("hello"); // Load native library at runtime
        // hello.dll (Windows) or libhello.so (Unixes)
    }

    // Declare a native method sayHello() that receives nothing and returns void
    private native void sayHello();

    // Test Driver
    public static void main(String[] args) {
        new HelloJNI().sayHello();  // invoke the native method
    }
}

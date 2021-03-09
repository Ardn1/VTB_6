import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TestSynchronize {
    static final int SIZE = 10000000;
    static final int HALF = SIZE / 2;

    class Method2Class implements Runnable {
        float[] arr;
        int indexStart = 0;
        public Method2Class(float[] arr, int indexStart) {
            this.arr = arr;
            this.indexStart = indexStart;
        }

        @Override
        public void run() {
            for (int i = indexStart; i < arr.length + indexStart; i++) {
                arr[i-indexStart] = (float)(arr[i-indexStart] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) *
                        Math.cos(0.4f + i / 2));
            }
        }
    }

    public void method1() {
        float[] arr = new float[SIZE];
        for ( int i = 0; i < arr.length; i++) {
            arr[i] = 1;
        }
        long timeStart = System.currentTimeMillis();

        for ( int i = 0; i < arr.length; i++) {
            arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) *
                    Math.cos(0.4f + i / 2));
        }
        System.out.println("method1: " + (System.currentTimeMillis() - timeStart) + arr[10]);
    }

    private Object lock1 = new Object();

    public void method2() {
        float[] arr = new float[SIZE];
        for ( int i = 0; i < arr.length; i++) {
            arr[i] = 1;
        }
        long timeStart = System.currentTimeMillis();

        float[] a1 = new float[HALF];
        float[] a2 = new float[HALF];

        System.arraycopy(arr, 0, a1, 0, HALF);
        System.arraycopy(arr, HALF, a2, 0, HALF);
        Thread first = new Thread(new Method2Class(a1, 0));
        first.start();
        Thread second = new Thread(new Method2Class(a2, HALF));
        second.start();
        try {
            first.join();
            second.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.arraycopy(a1, 0, arr, 0, HALF);
        System.arraycopy(a2, 0, arr, HALF, HALF);

        System.out.println("method2: " + (System.currentTimeMillis() - timeStart) + " " + arr[10]);
    }
}

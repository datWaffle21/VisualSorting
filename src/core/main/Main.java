package core.main;

import core.util.Constants;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Main extends Canvas implements Runnable {

    private Thread thread;

    SourceDataLine line;

    private static int FAST = 0;
    private static int SLOW = 1;

    private int i = 0;
    private int currentComp = 0;
    private boolean go = false;

    private int totalComps = 0;
    private int swaps = 0;

    private boolean running;

    public int[] nums = new int[Constants.numberOfBars];

    private void renderBars(Graphics g) {
        if(true) {
            //g.drawString(currentComp + "", 30, 100);
            for (int i = 0; i < nums.length; i++) {

                /*if (i % 2 == 0)
                    g.setColor(Color.WHITE);
                else
                    g.setColor(Color.BLUE);*/

                g.setColor(new Color(0, 255 * nums[i] / nums.length, 200));

                int width = Constants.WIDTH / nums.length;
                int height;

                if (Constants.numberOfBars < 700) {
                    //TODO -- generalize so that nums[0] is barely seen and nums[nums.length - 1] is at the top of the screen
                    height = nums[i] + i;
                } else {
                    height = (nums[i] / 2);
                }

                int x = (i * width);
                int y = (Constants.HEIGHT - height) - 39;


                g.fillRect(x, y, width, height);
            }
        } /*else {
            for (int i = 0; i < nums.length; i++) {
                int width = Constants.WIDTH / nums.length;
                int height;

                if (Constants.numberOfBars < 700) {
                    //TODO -- generalize so that nums[0] is barely seen and nums[nums.length - 1] is at the top of the screen
                    height = nums[i] + i;
                } else {
                    height = (nums[i] / 2);
                }

                int x = (i * width);
                int y = (Constants.HEIGHT - height) - 39;

                g.setColor(Color.GREEN);
                g.fillRect(x, y, width, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
            }
        }*/
    }

    public Main() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                go = !go;
            }
        });

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        for (int i = 0; i < nums.length; i++) {
            nums[i] = i;
        }

        for (int i = 0; i < 7; i++) {
            nums = randomizeArray(nums);
        }

        new Window(Constants.WIDTH, Constants.HEIGHT, "Visual Sorter", this);

        try {
            final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, true);
            line = AudioSystem.getSourceDataLine(af);
            line.open(af, Note.SAMPLE_RATE);
            line.start();
            /*for  (Note n : Note.values()) {
                play(line, n, 500);
                play(line, Note.REST, 10);
            }*/
            line.drain();
            line.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }


    }

    private void selection() {
        if(!isSorted(nums)) {
            int smallest = nums[i];
            int smallestIndex = i;
            for (int j = i + 1; j < nums.length; j++) {
                // gets the smallest int
                totalComps++;
                if (nums[j] < smallest) {
                    smallest = nums[j];
                    smallestIndex = j;
                }
            }

            swaps++;
            int temp = nums[i];
            nums[i] = nums[smallestIndex];
            nums[smallestIndex] = temp;
            currentComp = smallestIndex;
            i++;
        }
    }

    private void bubble() {
        if(!isSorted(nums)) {
            int count = 0;
            for (int i = 0; i < nums.length - 1 - count; i++) {
                int a = nums[i];
                int b = nums[i + 1];
                currentComp = i;
                totalComps++;
                if (a > b) {
                    int temp = a;
                    render();
                    a = b;
                    b = temp;
                    nums[i] = a;
                    nums[i + 1] = b;
                    swaps++;
                }
            }
            count++;
        }
    }

    public static int[] randomizeArray(int[] array) {
        Random rgen = new Random();  // Random number generator

        for (int i = 0; i < array.length; i++) {
            int randomPosition = rgen.nextInt(array.length);
            int temp = array[i];
            array[i] = array[randomPosition];
            array[randomPosition] = temp;
        }

        return array;
    }

    public void tick() {
        // This will be the sorting method.
        if (go) {
            //bubble();
            //selection();
            //insertionSort(nums, FAST);
            //cocktailSort(nums);
            //quickSort(nums, 0, nums.length - 1);
            //mergeSort(nums, 0 ,nums.length -1);
            radixSort(nums);
            //bogoSort(nums);
            //sleep(8);
        }
    }

    public void render() {
        // This will render each bar.
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);

        renderBars(g);

        if (!go) {
            g.setColor(Color.RED);
            g.fillRect(30, 30, 32, 32);
        }

        g.setColor(Color.WHITE);
        g.drawString("Total Comparisions: " + totalComps, 30, 100);
        g.drawString("Total Swaps: " + swaps, 30, 115);

        g.dispose();
        bs.show();
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop() {
        try {
            thread.join();
            running = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running) {
                render();
                frames++;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        stop();
    }

    public static void main(String[] args) {
        new Main();
    }

    private void sleep(int millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (Exception e) {}
    }

    public void quickSort(int arr[], int begin, int end) {
        if ((begin < end) && !isSorted(nums)) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, end);
        }
    }

    private int partition(int arr[], int begin, int end) {
        int pivot = arr[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            totalComps++;
            if (arr[j] <= pivot) {
                i++;
                swaps++;
                int swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;

                render();
            }
        }

        render();

        swaps++;
        int swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;

        return i + 1;
    }

    void merge(int[] arr, int l, int m, int r) {
        if(!isSorted(nums)) {
            // Find sizes of two subarrays to be merged
            int n1 = m - l + 1;
            int n2 = r - m;

            /* Create temp arrays */
            int[] L = new int[n1];
            int[] R = new int[n2];

            /*Copy data to temp arrays*/
            for (int i = 0; i < n1; ++i)
                L[i] = arr[l + i];
            for (int j = 0; j < n2; ++j)
                R[j] = arr[m + 1 + j];


            /* Merge the temp arrays */

            // Initial indexes of first and second subarrays
            int i = 0, j = 0;

            // Initial index of merged subarry array
            int k = l;
            while (i < n1 && j < n2) {
                totalComps++;
                if (L[i] <= R[j]) {
                    swaps++;
                    arr[k] = L[i];
                    i++;
                } else {
                    swaps++;
                    arr[k] = R[j];
                    j++;
                }
                k++;
                //sleep(1, 0);
                render();
            }

            /* Copy remaining elements of L[] if any */
            while (i < n1) {
                arr[k] = L[i];
                i++;
                k++;
            }

            /* Copy remaining elements of R[] if any */
            while (j < n2) {
                arr[k] = R[j];
                j++;
                k++;
            }
            render();
        }
    }

    void mergeSort(int[] arr, int l, int r) {
        if (l < r) {
            // Find the middle point
            int m = (l + r) / 2;

            // Sort first and second halves
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }

    void insertionSort(int[] arr, int speed) {
        if(!isSorted(nums)) {
            int n = arr.length;
            for (int i = 1; i < n; ++i) {
                render();
                int key = arr[i];
                int j = i - 1;
                totalComps++;

            /* Move elements of arr[0..i-1], that are
               greater than key, to one position ahead
               of their current position */
                while (j >= 0 && arr[j] > key) {
                    if(speed == 1) render();
                    arr[j + 1] = arr[j];
                    j = j - 1;
                    swaps++;
                }
                arr[j + 1] = key;
            }
        }
    }

    void cocktailSort(int[] a) {
        boolean swapped = true;
        int start = 0;
        int end = a.length;
        if(!isSorted(nums)) {
            while (swapped == true) {
                // reset the swapped flag on entering the
                // loop, because it might be true from a
                // previous iteration.
                swapped = false;

                // loop from bottom to top same as
                // the bubble sort
                for (int i = start; i < end - 1; ++i) {
                    totalComps++;
                    if (a[i] > a[i + 1]) {
                        swaps++;
                        render();
                        int temp = a[i];
                        a[i] = a[i + 1];
                        a[i + 1] = temp;
                        swapped = true;
                    }
                }

                // if nothing moved, then array is sorted.
                if (swapped == false)
                    break;

                // otherwise, reset the swapped flag so that it
                // can be used in the next stage
                swapped = false;

                // move the end point back by one, because
                // item at the end is in its rightful spot
                end = end - 1;

                // from top to bottom, doing the
                // same comparison as in the previous stage
                for (int i = end - 1; i >= start; i--) {
                    totalComps++;
                    if (a[i] > a[i + 1]) {
                        swaps++;
                        render();
                        int temp = a[i];
                        a[i] = a[i + 1];
                        a[i + 1] = temp;
                        swapped = true;
                    }
                }

                // increase the starting point, because
                // the last stage would have moved the next
                // smallest number to its rightful spot.
                start = start + 1;
            }
        }
    }

    public void radixSort(int[] arr) {
        if(!isSorted(nums)) {
            Queue<Integer>[] buckets = new Queue[10];
            for (int i = 0; i < 10; i++)
                buckets[i] = new LinkedList<Integer>();

            boolean sorted = false;
            int expo = 1;

            while (!sorted) {
                sorted = true;
                //render();
                for (int item : arr) {
                    render();
                    int bucket = (item / expo) % 10;
                    if (bucket > 0) sorted = false;
                    totalComps++;
                    buckets[bucket].add(item);
                }

                expo *= 10;
                int index = 0;

                for (Queue<Integer> bucket : buckets) {
                    while (!bucket.isEmpty()) {
                        render();
                        swaps++;
                        arr[index++] = bucket.remove();
                    }
                }

            }

            assert isSorted(arr);
        }
    }

    public void bogoSort(int[] arr) {
        while(!isSorted(arr)) {
            randomizeArray(arr);
            render();
        }
    }

    private static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++)
            if (arr[i - 1] > arr[i])
                return false;

        return true;
    }

    private static void play(SourceDataLine line, Note note, int ms) {
        ms = Math.min(ms, Note.SECONDS * 1000);
        int length = Note.SAMPLE_RATE * ms / 1000;
        int count = line.write(note.data(), 0, length);
    }

    enum Note {

        REST, A4, A4$, B4, C4, C4$, D4, D4$, E4, F4, F4$, G4, G4$, A5;
        public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
        public static final int SECONDS = 2;
        private byte[] sin = new byte[SECONDS * SAMPLE_RATE];

        Note() {
            int n = this.ordinal();
            if (n > 0) {
                double exp = ((double) n - 1) / 12d;
                double f = 440d * Math.pow(2d, exp);
                for (int i = 0; i < sin.length; i++) {
                    double period = (double) SAMPLE_RATE / f;
                    double angle = 2.0 * Math.PI * i / period;
                    sin[i] = (byte) (Math.sin(angle) * 127f);
                }
            }
        }

        public byte[] data() {
            return sin;
        }
    }
}

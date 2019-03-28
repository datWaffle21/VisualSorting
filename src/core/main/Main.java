package core.main;

import core.util.Constants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Main extends Canvas implements Runnable {

    private Random r = new Random();
    private Thread thread;

    private int i = 0;
    private boolean go = false;

    private boolean running = false;
    public int[] nums = new int[1000];


    private void renderBars(Graphics g) {
        g.setColor(Color.WHITE);
        for (int i = 0; i < nums.length; i++) {
            g.drawRect(i, (Constants.HEIGHT - (nums[i] / 2)) - 40, Constants.WIDTH / nums.length, nums[i] / 2);
        }
    }

    public Main() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                go = !go;
            }
        });

        for (int i = 0; i < nums.length; i++) {
            int temp = r.nextInt(1000);
            Check:
            for (int j = 0; j < nums.length; j++) {
                if (nums[j] == temp) {
                    r.nextInt(1000);
                    continue Check;
                }
            }
            nums[i] = temp;
        }
        new Window(Constants.WIDTH, Constants.HEIGHT, "Visual Sorter", this);
    }

    private void selection() {
        int smallest = nums[i];
        int smallestIndex = i;
        for (int j = i + 1; j < nums.length; j++) {
            // gets the smallest int
            if (nums[j] < smallest) {
                smallest = nums[j];
                smallestIndex = j;
            }
        }

        int temp = nums[i];
        nums[i] = nums[smallestIndex];
        nums[smallestIndex] = temp;
        i++;
    }

    private void bubble() {
        int count = 0;
        for (int i = 0; i < nums.length - 1 - count; i++) {
            int a = nums[i];
            int b = nums[i + 1];
            if (a > b) {
                int temp = a;
                a = b;
                b = temp;
                nums[i] = a;
                nums[i + 1] = b;
            }
        }
        count++;
    }

    public void tick() {
        // This will be the sorting method.
        if (go) {
            //bubble();
            //selection();
            //quickSort(nums, 0, nums.length - 1);
            mergeSort(nums, 0 ,nums.length -1);
            sleep(10);
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

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {}
    }

    void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            if(go) {
                int pi = partition(arr, low, high);

                // partition and after partition
                // Recursively sort elements before
                quickSort(arr, low, pi - 1);
                quickSort(arr, pi + 1, high);
            }
        }
    }

    int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = (low - 1); // index of smaller element
        for (int j = low; j < high; j++) {
            // If current element is smaller than or
            // equal to pivot
            if (arr[j] <= pivot) {
                i++;

                // swap arr[i] and arr[j]
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                render();
                sleep(6);
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        render();

        sleep(6);


        return i + 1;

    }

    void merge(int arr[], int l, int m, int r)
    {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        int L[] = new int [n1];
        int R[] = new int [n2];

        /*Copy data to temp arrays*/
        for (int i=0; i<n1; ++i)
            L[i] = arr[l + i];
        for (int j=0; j<n2; ++j)
            R[j] = arr[m + 1+ j];


        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarry array
        int k = l;
        while (i < n1 && j < n2)
        {
            if (L[i] <= R[j])
            {
                arr[k] = L[i];
                i++;
            }
            else
            {
                arr[k] = R[j];
                j++;
            }
            k++;
            sleep(1);
            render();
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2)
        {
            arr[k] = R[j];
            j++;
            k++;
        }
        render();
    }

    void mergeSort(int arr[], int l, int r)
    {
        if (l < r)
        {
            // Find the middle point
            int m = (l+r)/2;

            // Sort first and second halves
            mergeSort(arr, l, m);
            mergeSort(arr , m+1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }
}

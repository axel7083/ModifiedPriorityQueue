import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Main {

    // Driver code
    public static void main(String args[]) throws IOException {
        //int count = 100000000;
        //int kBest = 10000;

        // Creating empty priority queue
        //int[] data = generateRandomData(count);

        //System.out.println("data:");
        //System.out.println(Arrays.toString(data));

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("ModifiedPriorityQueueAlgorithm");
        XSSFSheet sheet2 = workbook.createSheet("TreeSetAlgorithm");

        int c = 20;
        //Row headerRow = sheet.createRow(0);
        //Cell headerCell = headerRow.createCell(0);
        //headerCell.setCellValue("Course Name");

        //for(int c = 18; c < 20 ; c ++) {

            Row row1 = sheet1.createRow(c);
            Row row2 = sheet2.createRow(c);
            Cell A0_1 = row1.createCell(0);
            Cell A0_2 = row2.createCell(0);
            A0_1.setCellValue("Count = " + c);
            A0_2.setCellValue("Count = " + c);

            int count =  5000000*c;
            int[] data = generateRandomData(count);
            System.out.println("=======\nRandom generated");

            for(int k = 1 ; k < 1000; k++) {
                System.out.println("k = "  + k);
                Cell val1 = row1.createCell(k );
                Cell val2 = row2.createCell(k );

                int kBest = 500*k;


                long start, end, timeInMs;

                start = System.currentTimeMillis();
                ModifiedPriorityQueueAlgorithm(data, kBest);
                end = System.currentTimeMillis();
                timeInMs = end - start;

                val1.setCellValue(timeInMs);

                start = System.currentTimeMillis();
                TreeSetAlgorithm(data, kBest);
                end = System.currentTimeMillis();
                timeInMs = end - start;

                val2.setCellValue(timeInMs);

                //val.setCellValue(k);

            }
        //}

        FileOutputStream outputStream = new FileOutputStream("./maxRandom500.xlsx");
        workbook.write(outputStream);
        workbook.close();


        /*int count = 1000000000;
        int kBest = 10000;
        int[] data = generateRandomData(count);
        System.out.println("=======\nRandom generated");

        long start, best, end, timeInMs;

        start = System.currentTimeMillis();
        best = ModifiedPriorityQueueAlgorithm(data, kBest);
        end = System.currentTimeMillis();
        timeInMs = end - start;

        //System.out.print("k: " + k + " s: " + s + " |=> " + timeInMs + " ");
        System.out.println("[ModifiedPriorityQueueAlgorithm] Solving kBest (" + kBest + ") unique on " + count + " samples in " + timeInMs + " ms. (best value: " + best + ")");

        start = System.currentTimeMillis();
        best = TreeSetAlgorithm(data, kBest);
        end = System.currentTimeMillis();
        timeInMs = end - start;

        //System.out.println(" | " + timeInMs);
        System.out.println("[TreeSetAlgorithm] Solving kBest (" + kBest + ") unique on " + count + " samples in " + timeInMs + " ms. (best value: " + best + ")");

*/

    }

    public static int ModifiedPriorityQueueAlgorithm(int[] data, int kBest) {
        ModifiedPriorityQueue<Integer> mPQueue = new ModifiedPriorityQueue<Integer>(kBest);
        for(int val : data) {
            if(mPQueue.size() < kBest) {
                mPQueue.add(val);
            }
            else
            {
                int first = mPQueue.peek();
                if(val > first)
                    mPQueue.add(val);
            }
        }
        return mPQueue.peek();
    }

    public static int TreeSetAlgorithm(int[] data, int kBest) {
        TreeSet<Integer> results = new TreeSet<>(Comparator.comparingInt(o -> o));
        for(int val : data) {
            if(results.size() < kBest)
                results.add(val);
            else
            {
                int first = results.first();
                if(val > first && results.add(val))
                        results.remove(first);
            }
        }
        return results.first();
    }

    public static int PriorityQueueAlgorithm(int[] data, int kBest) {
        PriorityQueue<Integer> pQueue = new PriorityQueue<>(Comparator.comparingInt(o -> o));
        for(int val : data) {
            if(pQueue.size() < kBest)
                pQueue.add(val);
            else
            {
                if(!pQueue.contains(val)) {
                    int first = pQueue.peek();
                    if(val > first)
                        pQueue.add(val);
                }
            }
        }
        return pQueue.peek();
    }





    public static int[] generateRandomData(int count) {
        int[] array = new int[count];
        for(int i = 0 ; i < count ; i++) {
            array[i] = random(0, 600000);
        }
        return array;
    }

    public static int random(int min , int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

}

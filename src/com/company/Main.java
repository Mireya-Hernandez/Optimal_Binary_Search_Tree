package com.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.IIOException;


public class Main {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in); //Read user input
        System.out.println("Enter Number of Keys:"); // Prints out  instruction to user
        int numKeys = scanner.nextInt(); // get integer inputs
        System.out.println("Probability of keys and dummy key to calculate: "); // printout of user
        //scanner.close();
        int[] keys = generateKeys(numKeys); //Array that will hold the keys

        double[] prob = randomProbabilty((numKeys * 2) + 1); // probability of the sucesses and fails

        //write input (keys) into a file
        PrintWriter writeKeys = new PrintWriter(new File("10000keyFile.txt"));
        for (int num = 0; num < keys.length; num++) {

            writeKeys.println(keys[num]);
            //System.out.println(keys[num]);
        }
        writeKeys.close();

        //writing the random probability into a file
        PrintWriter writeProbs = new PrintWriter(new File("20001ProbFile.txt"));
        for (int num = 0; num < prob.length; num++) {

            writeProbs.printf("%n%.4f", prob[num]);
            //System.out.printf("%n%.4f",prob[num]);
        }
        writeProbs.close();

        //Dividing the total probability which sum to 1 into sucesses and failures
        // Storing P and q into there own array.
        double[] searchHit = Arrays.copyOfRange(prob, 0, (prob.length) / 2);
        System.out.println('\n' + "Probability of Hits  (Success): ");
        for (int num = 0; num < searchHit.length; num++) {
            System.out.printf("%n%.4f", searchHit[num]);
        }

        double[] searchFail = Arrays.copyOfRange(prob, (prob.length / 2), prob.length);
        System.out.println('\n' + "\nProbability of Fails : ");
        for (int num = 0; num < searchFail.length; num++) {
            System.out.printf("%n%.4f", searchFail[num]);
        }
        //Gettimg the exepected cost and root
        //Unable to format the numbers into matrixs so it is unclear
        double[][] expectedCost = new double[numKeys + 2][numKeys + 1];

        double[][] root = optminalBinaryTree(searchHit, searchFail, numKeys, expectedCost);

        //Storing the expected cost into the eMatrix file
        PrintWriter writeExpectedCost = new PrintWriter(new File ("10000KeyExpectedMatrix.txt"));
        for (int i = 0; i < expectedCost.length; i++) {
            for (int j = 0; j <expectedCost[i].length; j++){
                writeExpectedCost.print( expectedCost[i][j]);
            }

        }
        writeExpectedCost.close();

        //Storing the root matrixs into the rMatrix file
        PrintWriter writeRoot = new PrintWriter(new File ("10000KeyRootMatrix.txt"));
        for (int i = 0; i < root.length; i++) {
            for (int j = 0; j < root[i].length; j++) {
                writeRoot.print( root[i][j]);
            }

        }
        writeRoot.close();

        System.out.println("\n The cost of the OBST is " + expectedCost[1][numKeys]);
    }

    //Method to generate the random keys and soting the keys in Ascending order
    public static int[] generateKeys(int numKeys) {
        Random randomNum = new Random();
        int[] inputQuanity = new int[numKeys];

        for (int num = 0; num < inputQuanity.length; num++) {
            inputQuanity[num] = randomNum.nextInt(85) + 15;
        }
        Arrays.sort(inputQuanity);
        return inputQuanity;
    }

    //Method to obtain the random probability for the sucessess(Hits) and failure searches
    public static double[] randomProbabilty(int numKeys) {
        Random random = new Random();

        //storing the total sum of the probablities needed
        double initialSum = 0;
        double[] randomProb = new double[numKeys];

        for (int num = 0; num < randomProb.length; num++) {
            randomProb[num] = random.nextDouble();
            initialSum += randomProb[num];
        }

        //dividing all the randomly obtain probability by the total initialsum calculated above
        //Ensures the sum of the probability sums up to 1
        for (int num = 0; num < randomProb.length; num++) {
            randomProb[num] = (randomProb[num] / initialSum);
        }
        return randomProb;
    }

    //Algrorithm for the optimal binary search tree (Dynamic Programing)
    public static double[][] optminalBinaryTree(double[] searchHit, double[] searchFail,
        int numkeys, double[][] expectedCost) throws IIOException, FileNotFoundException {

        //Start time to measure the time it take the algo. to complete
        Instant start = Instant.now();
        double[][] weight = new double[numkeys + 2][numkeys + 1];
        double[][] root = new double[numkeys + 1][numkeys + 1];



        for (int i = 0; i <= numkeys; i++) {
            expectedCost[i + 1][i] = searchFail[i];
        }

        for (int i = 0; i <= numkeys; i++) {
            weight[i + 1][i] = searchFail[i];
        }

        for (int k = 1; k <= numkeys; k++) {
            for (int i = 1; i <= numkeys - k + 1; i++) {
                int j = i + k - 1;
                expectedCost[i][j] = Integer.MAX_VALUE;
                for (int r = i; r <= j; r++) {
                    double valueMin =
                        expectedCost[i][r - 1] + expectedCost[r + 1][j] + weight[i][j];
                    if (valueMin < expectedCost[i][j]) {
                        expectedCost[i][j] = valueMin;
                        root[i][j] = r;
                    }
                }
            }
        }

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();

        System.out.println(
            "\nElapse time in Miliseconds " + timeElapsed + "s to run the given algrorithm with "
                + numkeys + " keys");

        return root;


    }
}








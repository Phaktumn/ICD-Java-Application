package com.company;

import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Main {

    private static String Url;
    private static int Iterations;

    private static URLConnection currentConnection;

    private static long last = -1;
    private static long iaa = 0, tra = 0, c = 0;
    private static boolean start = false;
    private static int n = 0;
    private static synchronized int next() {
        return n++;
    }

    public static void main(String[] args) throws IOException {
        Url = args[0];
        Iterations = Integer.parseInt (args[1]);

        start = true;

        for (int i = 0; i < Iterations; i++) {

            long before = System.nanoTime();
            Request (Url);
            long after = System.nanoTime();

            regista (before, after);
        }
        Pair <Double, Double> calculation = Calculate ();
        Print (calculation.getKey (), calculation.getValue ());
    }

    private static void Request(String url) throws IOException {
        URL myURL = new URL(url);
        currentConnection = myURL.openConnection ();
    }

    private static double ConvertToSeconds(long value) {
        return value * 0.000000001;
    }

    private static synchronized void regista(long antes, long depois) {
        long tr = depois-antes;

        long anterior = last;
        last = depois;

        if (anterior < 0 || !start)
            return;

        long ia = depois - anterior;

        iaa += ia;
        tra += tr;
        c++;
    }

    public static synchronized Pair<Double,Double> Calculate(){
        double trm = (tra/1e9d)/c;
        double debit = 1/((iaa/1e9d)/c);
        return new Pair <> (trm, debit);
    }

    public static synchronized void Print(double trm, double debito) {
        System.out.println("debito = "+ debito + " tps, tr = "+trm+" s");

    }
}

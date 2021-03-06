package com.company;

import javafx.util.Pair;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Main extends Thread {

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


    public static synchronized void Start() {
        start = true;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Url = args[0];
        Iterations = Integer.parseInt (args[1]);
        Start ();
        for (int i = 0; i < Iterations; i++) {
            new Main().start();
        }

        Thread.sleep(10000);

        Pair <Double, Double> calculation = Calculate ();
        Print (calculation.getKey (), calculation.getValue ());

        System.exit(0);

    }

    public void run() {
        while(true) {

            long before = System.nanoTime();
            try {
                URL website = new URL("http://192.168.40.145");
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream("information"+n+".file");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace ();
            }
            long after = System.nanoTime();
            regista (before, after);
            next();
        }
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

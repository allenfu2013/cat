package com.dianping.cat;

import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;

import java.io.IOException;
import java.util.Scanner;

/**
 * Cat Sample
 */
public class CatSample {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(
                    "choose to test: \n" +
                            "1: error\n" +
                            "2: transaction\n" +
                            "3: event\n" +
                            "4: metric\n" +
                            "q: exit\n" +
                            "please enter: "
            );

            int choose = Integer.valueOf(sc.next());

            switch (choose) {
                case 1:
                    error();
                    break;
                case 2:
                    transaction();
                    break;
                case 3:
                    event();
                    break;
                case 4:
                    metric();
                    break;
                case 5:
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }

    public static void error() {
        LoggerUtil.error(CatSample.class, "cat error sample", new RuntimeException("cat error sample"));
    }

    public static void transaction() {
        Transaction t = Cat.newTransaction("YourType", "YourName");
        try {
            // your business here
            Thread.sleep(500);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
    }

    public static void event() {
        Cat.logEvent("Type1", "Name1");
        Cat.logEvent("Type2", "Name2", Event.SUCCESS, "a=1&b=2");
    }

    public static void metric() {
        Cat.logMetricForCount("PayAmount", 10);
    }
}

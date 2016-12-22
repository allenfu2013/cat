package com.dianping.cat;

import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;

import java.io.IOException;

/**
 * Cat Sample
 */
public class CatSample {

    public static void main(String[] args) throws IOException {
        LoggerUtil.info(CatSample.class, "Cat sample is to user cat error, transaction, event");

        error();
        transaction();
        event();

        /*LoggerUtil.info(CatSample.class, "Choose and press Enter to test: ('q' to exit)");

        String choose = System.console().readLine();

        while (choose != "q") {

        }*/

        LoggerUtil.info(CatSample.class, "press any key to quit: ");

        System.in.read();


        LoggerUtil.info(CatSample.class, "Bye!");
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
}

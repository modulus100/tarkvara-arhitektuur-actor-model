package tarkvara.arhitektuur;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class AkkaBank {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("bank-system");
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        final ActorRef printerActor = system.actorOf(Printer.props(), "printerActor");
        final ActorRef bankActor = system.actorOf(BankActor.props(0, printerActor), "depositorActor");

        for (int i = 0; i < 10; i++) {
            int index = i;

            executor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " async deposit nr " + index + " adding 10 to bank account");
                bankActor.tell(new BankActor.Depositor(10), ActorRef.noSender());
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            bankActor.tell(new BankActor.BalancePrinter(), ActorRef.noSender());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            system.terminate();
        }
    }
}

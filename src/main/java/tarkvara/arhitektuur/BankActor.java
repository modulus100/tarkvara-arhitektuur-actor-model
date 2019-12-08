package tarkvara.arhitektuur;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import tarkvara.arhitektuur.Printer.DepositMessage;

public class BankActor extends AbstractActor {

    static public Props props(int startBalance, ActorRef printerActor) {
        return Props.create(BankActor.class, () -> new BankActor(startBalance, printerActor));
    }

    static public class Depositor {
        public final int deposit;

        public Depositor(int deposit) {
            this.deposit = deposit;
        }
    }

    static public class BalancePrinter {
        public BalancePrinter() {
        }
    }

    private boolean firstDeposit = true;
    private final int startBalance;
    private final ActorRef printerActor;
    private int balance;

    public BankActor(int startBalance, ActorRef printerActor) {
        this.startBalance = startBalance;
        this.printerActor = printerActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Depositor.class, wtg -> {
                    if (firstDeposit) {
                        this.balance = startBalance + wtg.deposit;
                        firstDeposit = false;
                    } else {
                        this.balance = this.balance + wtg.deposit;
                    }
                })
                .match(BalancePrinter.class, x -> {
                    printerActor.tell(new DepositMessage(Integer.toString(this.balance)), getSelf());
                })
                .build();
    }
}
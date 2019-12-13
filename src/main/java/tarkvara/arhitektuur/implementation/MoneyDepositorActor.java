package tarkvara.arhitektuur.implementation;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class MoneyDepositorActor extends AbstractActor {

    public double amount;
    public final ActorRef bankToDeposit;

    public MoneyDepositorActor(double amount, ActorRef bankToDeposit) {
        this.amount = amount;
        this.bankToDeposit = bankToDeposit;
    }

    static public Props props(double amount, ActorRef bankToDeposit) {
        return Props.create(MoneyDepositorActor.class, () -> new MoneyDepositorActor(amount, bankToDeposit));
    }

    static public class Deposit {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Deposit.class, x -> {
            bankToDeposit.tell(new BankActor.DepositReceiver(this.amount), getSelf());
        })
                .build();
    }




}

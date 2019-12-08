package tarkvara.arhitektuur.implementation;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class MoneyDepositorActor extends AbstractActor {

    public double amount;
    public final ActorRef bankToDepose;

    public MoneyDepositorActor(double amount, ActorRef bankToDepose) {
        this.amount = amount;
        this.bankToDepose = bankToDepose;
    }

    static public Props props(double amount, ActorRef bankToDepose) {
        return Props.create(MoneyDepositorActor.class, () -> new MoneyDepositorActor(amount, bankToDepose));
    }

    static public class Depose{
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Depose.class, x -> {
            bankToDepose.tell(new BankActor.MoneyDepositMaker(this.amount), getSelf());
        })
                .build();
    }




}

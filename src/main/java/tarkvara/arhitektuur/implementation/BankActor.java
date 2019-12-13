package tarkvara.arhitektuur.implementation;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class BankActor  extends AbstractActor {

    public double moneyOnAccount = 0;


    static public Props props() {
        return Props.create(BankActor.class, BankActor::new);
    }

    public double getMoneyOnAccount() {
        return moneyOnAccount;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DepositReceiver.class, changeAmount -> {
                    this.moneyOnAccount += changeAmount.moneyAmount;
                    System.out.println("Money arrived: " + changeAmount.moneyAmount +". Now:" +this.moneyOnAccount);
                })
                .build();
    }
    static public class DepositReceiver {

        public final double moneyAmount;

        public DepositReceiver(double moneyAmount) {
            this.moneyAmount = moneyAmount;
        }
    }

}

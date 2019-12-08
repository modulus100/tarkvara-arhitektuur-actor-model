package tarkvara.arhitektuur.implementation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.util.ArrayList;
import java.util.List;

public class MoneyDeposeRunner extends Thread {



    //private final static int NUM_OF_DEPOSITS = 50;

    public ActorSystem systemToRunIn;
    public ActorRef bankToDepositMoney;
    public int numofDeposites;


    public MoneyDeposeRunner(ActorSystem systemToRunIn, ActorRef bankToDepositMoney, int numberOfDeposites){
        this.systemToRunIn = systemToRunIn;
        this.bankToDepositMoney = bankToDepositMoney;
        this.numofDeposites = numberOfDeposites;
    }

    @Override
    public void run(){

        List<ActorRef> deposers = new ArrayList<>();

        for(int i = 0; i < numofDeposites; i++){
            final ActorRef tempActor =  systemToRunIn.actorOf(MoneyDepositorActor.props((i+1)*5 ,bankToDepositMoney));
            deposers.add(tempActor);
        }

        for(ActorRef actorRef: deposers){
            actorRef.tell(new MoneyDepositorActor.Depose(), ActorRef.noSender());
        }

    }


}

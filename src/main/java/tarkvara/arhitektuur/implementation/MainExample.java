package tarkvara.arhitektuur.implementation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;


public class MainExample {
    public static void main(String[] args) throws InterruptedException {

        final ActorSystem system = ActorSystem.create("system");

        final ActorRef bankActor =
                system.actorOf(BankActor.props(), "BankActor");

        final ActorRef actor1 =  system.actorOf(MoneyDepositorActor.props(10 ,bankActor));
        final ActorRef actor2 =  system.actorOf(MoneyDepositorActor.props(20 ,bankActor));
        final ActorRef actor3 =  system.actorOf(MoneyDepositorActor.props(30 ,bankActor));

        actor1.tell(new MoneyDepositorActor.Deposit(), ActorRef.noSender());
        actor2.tell(new MoneyDepositorActor.Deposit(), ActorRef.noSender());
        actor3.tell(new MoneyDepositorActor.Deposit(), ActorRef.noSender());
       // bankActor.tell("11111", ActorRef.noSender());
    }
}

package tarkvara.arhitektuur;

import static org.junit.Assert.assertEquals;

import akka.event.Logging;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import tarkvara.arhitektuur.implementation.BankActor;
import tarkvara.arhitektuur.implementation.MoneyDepositorActor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AlternativeSolutionTest {
    static ActorSystem system;
    private final static Logger logger = Logger.getLogger(AlternativeSolutionTest.class.getName());

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testSingleDeposit() {
        double expectedBalanced = 10;

        final TestKit testProbe = new TestKit(system);
        final ActorRef actor = system.actorOf(MoneyDepositorActor.props(10, testProbe.getRef()));
        actor.tell(new MoneyDepositorActor.Deposit(), ActorRef.noSender());

        BankActor.DepositReceiver testBank = testProbe.expectMsgClass(BankActor.DepositReceiver.class);
        assertEquals(expectedBalanced, testBank.moneyAmount,0);

        system.terminate();
    }



}

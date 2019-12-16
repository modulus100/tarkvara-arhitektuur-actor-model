package tarkvara.arhitektuur;

import static org.junit.Assert.assertEquals;

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

public class AlternativeSolutionTest {
    static ActorSystem system;

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
    public void testActorAsyncDeposit() {
        int expectedBalanced = 1000;

        final TestKit testProbe = new TestKit(system);
        final ActorRef bankActor = system.actorOf(BankActor.props());
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                final ActorRef actor = system.actorOf(MoneyDepositorActor.props(10, bankActor));
                actor.tell(new MoneyDepositorActor.Deposit(), ActorRef.noSender());
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
            BankActor testActor = testProbe.expectMsgClass(BankActor.class);
            assertEquals(expectedBalanced, testActor.moneyOnAccount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            system.terminate();
        }
    }


}

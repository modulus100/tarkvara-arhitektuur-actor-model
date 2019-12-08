package tarkvara.arhitektuur;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AkkaBankTest {
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
    public void testGreeterActorSendingOfGreeting() {
        int startBalance = 0;
        int depositDelta = 10;
        String expectedBalanced = "1000";

        final TestKit testProbe = new TestKit(system);
        final ActorRef bankActor = system.actorOf(BankActor.props(startBalance, testProbe.getRef()));
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                bankActor.tell(new BankActor.Depositor(depositDelta), ActorRef.noSender());
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
            bankActor.tell(new BankActor.BalancePrinter(), ActorRef.noSender());

            Printer.DepositMessage testActor = testProbe.expectMsgClass(Printer.DepositMessage.class);
            assertEquals(expectedBalanced, testActor.message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            system.terminate();
        }
    }
}

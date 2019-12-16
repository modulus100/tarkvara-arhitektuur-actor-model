package tarkvara.arhitektuur;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import tarkvara.arhitektuur.implementation.BankActor;
import tarkvara.arhitektuur.implementation.MoneyDepositorActor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AlternativeSolutionTest {

    private final static Logger logger = Logger.getLogger(AlternativeSolutionTest.class.getName());


    @Test
    public void testSingleDeposit() {
        double expectedBalanced = 10;
        ActorSystem system = ActorSystem.create();
        final TestKit testProbe = new TestKit(system);
        final ActorRef actor = system.actorOf(MoneyDepositorActor.props(10, testProbe.getRef()));
        actor.tell(new MoneyDepositorActor.Deposit(), ActorRef.noSender());

        BankActor.DepositReceiver testBank = testProbe.expectMsgClass(BankActor.DepositReceiver.class);
        assertEquals(expectedBalanced, testBank.moneyAmount, 0);

        system.terminate();
        TestKit.shutdownActorSystem(system);
    }

    @Test
    public void testMultipleDeposit() {

        final double NUM_OF_TRANSACTION = 3;
        ActorSystem system = ActorSystem.create();

        final TestKit testProbe = new TestKit(system);
        final double EXPECTED_BALANCE = 60;
        final List<Double> moneyToDeposit = Arrays.asList(10d, 20d, 30d);

        for (int i = 0; i < NUM_OF_TRANSACTION; i++) {
            final ActorRef actor = system.actorOf(MoneyDepositorActor.props(moneyToDeposit.get(i), testProbe.getRef()));
            actor.tell(new MoneyDepositorActor.Deposit(), ActorRef.noSender());
        }
        double sumOfDeposits = 0;
        BankActor.DepositReceiver testDepositReceiver;

        for (int i = 0; i < NUM_OF_TRANSACTION; i++) {

            testDepositReceiver = testProbe.expectMsgClass(BankActor.DepositReceiver.class);
            double testMoney = testDepositReceiver.moneyAmount;
            assertThat(moneyToDeposit, hasItem(testMoney));
            sumOfDeposits += testMoney;
        }
        assertEquals(EXPECTED_BALANCE, sumOfDeposits, 0);


        system.terminate();
    }

    @Test
    public void testMultipleDepositWithDifferentThreads() {
        ActorSystem system = ActorSystem.create();

        final TestKit testProbe = new TestKit(system);
        final double EXPECTED_BALANCE = 10000;
        final int TOTAL_NUMBER_OF_OPERATIONS = 1000;
        final int TOTAL_NUMBER_OF_OPERATIONS_PER_THREAD = 100;
        final int NUMBER_OF_THREADS = 10;
        final ActorRef actor = system.actorOf(MoneyDepositorActor.props(10, testProbe.getRef()));
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < TOTAL_NUMBER_OF_OPERATIONS_PER_THREAD; j++) {
                    actor.tell(new MoneyDepositorActor.Deposit(), ActorRef.noSender());
                }
            });
        }
        executor.shutdown();
        BankActor.DepositReceiver testDepositReceiver;
        double sumOfMoneyDeposed = 0;
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
            for (int i = 0; i < TOTAL_NUMBER_OF_OPERATIONS; i++) {
                testDepositReceiver = testProbe.expectMsgClass(BankActor.DepositReceiver.class);
                sumOfMoneyDeposed += testDepositReceiver.moneyAmount;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            system.terminate();
        }
        assertEquals(sumOfMoneyDeposed, EXPECTED_BALANCE, 0);
        system.terminate();
    }


    //TODO: add tests of negative answers

}

package tarkvara.arhitektuur;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Printer extends AbstractActor {
    static public Props props() {
        return Props.create(Printer.class, Printer::new);
    }

    static public class DepositMessage {
        public final String message;

        public DepositMessage(String message) {
            this.message = message;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public Printer() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DepositMessage.class, depositMessage -> {
                    log.info(depositMessage.message);
                })
                .build();
    }
}

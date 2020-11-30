package practical5;

import java.util.List;
import edu.builders.ACLMessageBuilder;
import edu.models.ServiceLocator;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
public class SynchronisationBehaviour extends FSMBehaviour {
    private final int _maxDays;
    private int _responsesReceived = 0;
    private int _day = 0;
    private List<AID> _simulationAgents;
    private static final String LOAD_AGENTS_STATE = "LOAD_AGENTS_STATE";
    private static final String AWAIT_RESPONSE_STATE = "AWAIT_RESPONSE_STATE";
    private static final String TERM_SYNC_STATE = "TERM_SYNC_STATE";
    private static final int DAYS_LEFT = -1;
    private static final int DAYS_COMPLETE = 1;
    public SynchronisationBehaviour(final Agent a, final int maxDays) {
        super(a);
        _maxDays = maxDays;
    }
    @Override
    public void onStart() {
        registerFirstState(new OneShotBehaviour() {
            @Override
            public void action() {
               # Ignore this, I created a service locator extension to cut down on the amount of code needed to get agents from the yellow pages, this basically uses the same form of syntax as Action<T, TResult> you find in C# along with a couple of wrapping builders.
                _simulationAgents = ServiceLocator.searchAID(myAgent,
                        (t) -> t.addServices((s) -> s.setType("buyer").toServiceDescription())
                                .toTemplate(),
                        (t2) -> t2.addServices((s) -> s.setType("seller").toServiceDescription())
                                .toTemplate());
                ACLMessageBuilder messageBuilder =
                        new ACLMessageBuilder(new ACLMessage(ACLMessage.INFORM))
                                .setContent("new day");
                for (AID aid : _simulationAgents) {
                    messageBuilder.addReceiver(aid);
                }
                myAgent.send(messageBuilder.build());
                _day++;
            }
        }, LOAD_AGENTS_STATE);
        registerState(new Behaviour() {
            @Override
            public void action() {
                ACLMessage response = myAgent.receive(MessageTemplate.MatchContent("done"));
                if (response != null) {
                    _responsesReceived++;
                } else {
                    block();
                }
            }
            @Override
            public boolean done() {
                return _responsesReceived >= _simulationAgents.size();
            }
            @Override
            public int onEnd() {
                System.out.println("End of day " + _day);
                if (_day == _maxDays) {
                    ACLMessageBuilder builder =
                            new ACLMessageBuilder(new ACLMessage(ACLMessage.INFORM))
                                    .setContent("terminate");
                    for (AID aid : _simulationAgents) {
                        builder.addReceiver(aid);
                    }
                    myAgent.send(builder.build());
                    myAgent.doDelete();
                }
                return _day == _maxDays ? DAYS_COMPLETE : DAYS_LEFT;
            }
        }, AWAIT_RESPONSE_STATE);
        registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("Terminate Synchronisation...");
            }
        }, TERM_SYNC_STATE);
        registerDefaultTransition(LOAD_AGENTS_STATE, AWAIT_RESPONSE_STATE);
        registerTransition(AWAIT_RESPONSE_STATE, LOAD_AGENTS_STATE, DAYS_LEFT);
        registerTransition(AWAIT_RESPONSE_STATE, TERM_SYNC_STATE, DAYS_COMPLETE);
    }
}
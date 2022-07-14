package id.holigo.services.holigouserservice.config;

import id.holigo.services.common.model.EmailStatusEnum;
import id.holigo.services.holigouserservice.actions.EmailStatusAction;
import id.holigo.services.holigouserservice.events.EmailStatusEvent;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@EnableStateMachineFactory(name = "emailStatusSMF")
@Configuration
public class EmailStatusSMConfig extends StateMachineConfigurerAdapter<EmailStatusEnum, EmailStatusEvent> {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void configure(StateMachineStateConfigurer<EmailStatusEnum, EmailStatusEvent> states) throws Exception {
        states.withStates().initial(EmailStatusEnum.WAITING_CONFIRMATION)
                .states(EnumSet.allOf(EmailStatusEnum.class))
                .end(EmailStatusEnum.CONFIRMED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EmailStatusEnum, EmailStatusEvent> transitions) throws Exception {
        transitions.withExternal().source(EmailStatusEnum.WAITING_CONFIRMATION).target(EmailStatusEnum.CONFIRMED)
                .event(EmailStatusEvent.SUCCESSFUL_CONFIRMATION)
                .and().withExternal().source(EmailStatusEnum.WAITING_CONFIRMATION).target(EmailStatusEnum.WAITING_CONFIRMATION)
                .event(EmailStatusEvent.EXPIRATION_CONFIRMATION).action(new EmailStatusAction(userRepository).generateNewVerificationCode());
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EmailStatusEnum, EmailStatusEvent> config) throws Exception {
        StateMachineListenerAdapter<EmailStatusEnum, EmailStatusEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<EmailStatusEnum, EmailStatusEvent> from, State<EmailStatusEnum, EmailStatusEvent> to) {
                log.info(String.format("stateChanged(from: %s, %s", from.getId(), to.getId()));
            }
        };
        config.withConfiguration().listener(adapter);
    }
}

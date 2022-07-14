package id.holigo.services.holigouserservice.interceptors;

import id.holigo.services.common.model.EmailStatusEnum;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.events.EmailStatusEvent;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.services.EmailStatusServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class EmailStatusInterceptor extends StateMachineInterceptorAdapter<EmailStatusEnum, EmailStatusEvent> {

    private final UserRepository userRepository;

    @Override
    public void preStateChange(State<EmailStatusEnum, EmailStatusEvent> state,
                               Message<EmailStatusEvent> message,
                               Transition<EmailStatusEnum, EmailStatusEvent> transition,
                               StateMachine<EmailStatusEnum, EmailStatusEvent> stateMachine,
                               StateMachine<EmailStatusEnum, EmailStatusEvent> rootStateMachine) {
        Optional.ofNullable(message).flatMap(msg -> Optional.of(
                Long.valueOf(Objects.requireNonNull(msg.getHeaders().get(EmailStatusServiceImpl.EMAIL_VERIFICATION_HEADER)).toString())
        )).ifPresent(id -> {
            log.info("interceptor is running... with id -> {}", id);
            log.info("last state -> {}", state.getId());
            User user = userRepository.getById(id);
            user.setEmailStatus(state.getId());
            userRepository.save(user);

        });
    }
}

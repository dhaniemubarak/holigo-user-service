package id.holigo.services.holigouserservice.services;

import id.holigo.services.common.model.EmailStatusEnum;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.events.EmailStatusEvent;
import id.holigo.services.holigouserservice.interceptors.EmailStatusInterceptor;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailStatusServiceImpl implements EmailStatusService {


    private final StateMachineFactory<EmailStatusEnum, EmailStatusEvent> emailStatusStateMachineFactory;

    private UserRepository userRepository;

    private final EmailStatusInterceptor emailStatusInterceptor;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static final String EMAIL_VERIFICATION_HEADER = "email_verification_id";

    @Override
    public StateMachine<EmailStatusEnum, EmailStatusEvent> successConfirmation(Long id) {
        StateMachine<EmailStatusEnum, EmailStatusEvent> sm = build(id);
        sendEvent(id, sm, EmailStatusEvent.SUCCESSFUL_CONFIRMATION);
        return sm;
    }

    @Override
    public StateMachine<EmailStatusEnum, EmailStatusEvent> expiryConfirmation(Long id) {
        StateMachine<EmailStatusEnum, EmailStatusEvent> sm = build(id);
        sendEvent(id, sm, EmailStatusEvent.EXPIRATION_CONFIRMATION);
        return sm;
    }

    private void sendEvent(Long id, StateMachine<EmailStatusEnum, EmailStatusEvent> sm, EmailStatusEvent event) {
        Message<EmailStatusEvent> message = MessageBuilder.withPayload(event).setHeader(EMAIL_VERIFICATION_HEADER, id).build();
        sm.sendEvent(message);
    }

    private StateMachine<EmailStatusEnum, EmailStatusEvent> build(Long id) {
        User user = userRepository.getById(id);
        log.info("user status -  {}", user.getEmailStatus());


        StateMachine<EmailStatusEnum, EmailStatusEvent> sm = emailStatusStateMachineFactory
                .getStateMachine(user.getId().toString());
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(emailStatusInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(user.getEmailStatus(), null, null, null));
        });
        sm.start();
        return sm;
    }
}

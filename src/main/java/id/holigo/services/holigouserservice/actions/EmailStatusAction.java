package id.holigo.services.holigouserservice.actions;

import id.holigo.services.common.model.EmailStatusEnum;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.events.EmailStatusEvent;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.services.EmailStatusServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.statemachine.action.Action;

@Slf4j
@RequiredArgsConstructor
public class EmailStatusAction {

    private final UserRepository userRepository;

    public Action<EmailStatusEnum, EmailStatusEvent> generateNewVerificationCode() {
        log.info("Action generate new verification code");
        return stateContext -> {
            User user = userRepository.getById(Long.valueOf(stateContext.getMessageHeader(EmailStatusServiceImpl.EMAIL_VERIFICATION_HEADER).toString()));
            user.setVerificationCode(RandomStringUtils.randomAlphabetic(64));
            userRepository.save(user);
        };
    }
}

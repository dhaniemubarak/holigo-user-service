package id.holigo.services.holigouserservice.services.otp;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.config.JmsConfig;
import id.holigo.services.common.events.UserValidationOtpEvent;
// import id.holigo.services.holigouserservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserOtpService {

    // private final UserRepository userRepository;
    private final JmsTemplate jmsTemplate;

    public void checkForOtpIsValid(UserDto userDto) {
        log.debug("checkForOtpIsValid is running .....");
        log.info("INFO : checkForOtpIsValid is running .....");
        jmsTemplate.convertAndSend(JmsConfig.OTP_REGISTER_VALIDATION_QUEUE, new UserValidationOtpEvent(userDto));
    }

}

package id.holigo.services.holigouserservice.listeners;

import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import id.holigo.services.common.model.UserAuthenticationDto;
import id.holigo.services.holigouserservice.config.JmsConfig;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OauthUserListener {

    private final JmsTemplate jmsTemplate;

    private final UserRepository userRepository;

    @JmsListener(destination = JmsConfig.OAUTH_USER_DATA_QUEUE)
    public void listen(@Payload UserAuthenticationDto userAuthenticationDto, @Headers MessageHeaders headers,
            Message message) throws JmsException, JMSException {
        Optional<User> fetchUser = userRepository.findByPhoneNumber(userAuthenticationDto.getPhoneNumber());
        if (fetchUser.isPresent()) {
            User user = fetchUser.get();
            userAuthenticationDto.setId(user.getId());
            userAuthenticationDto.setOneTimePassword(user.getOneTimePassword());
            userAuthenticationDto.setAccountNonExpired(user.getAccountNonExpired());
            userAuthenticationDto.setEnabled(user.getEnabled());
            userAuthenticationDto.setAccountNonLocked(user.getAccountNonLocked());
        }
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), userAuthenticationDto);
    }
}

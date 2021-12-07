package id.holigo.services.holigouserservice.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import id.holigo.services.common.events.UserAuthenticationEvent;
import id.holigo.services.common.model.UserAuthenticationDto;
import id.holigo.services.holigouserservice.config.JmsConfig;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OauthUserListener {

    @Autowired
    private final JmsTemplate jmsTemplate;

    @Autowired
    private final UserRepository userRepository;

    @JmsListener(destination = JmsConfig.OAUTH_USER_DATA_QUEUE)
    public void listen(@Payload UserAuthenticationDto userAuthenticationDto, @Headers MessageHeaders headers,
            Message message) throws JmsException, JMSException {
        Optional<User> fetchUser = userRepository.findByPhoneNumber(userAuthenticationDto.getPhoneNumber());
        if (fetchUser.isPresent()) {
            User user = fetchUser.get();
            Collection<String> authorities = new ArrayList<>();
            user.getAuthorities().forEach(authority -> {
                authorities.add(authority.getRole());
            });
            userAuthenticationDto.setId(user.getId());
            userAuthenticationDto.setOneTimePassword(user.getOneTimePassword());
            userAuthenticationDto.setAccountNonExpired(user.getAccountNonExpired());
            userAuthenticationDto.setEnabled(user.getEnabled());
            userAuthenticationDto.setAccountNonLocked(user.getAccountNonLocked());
            userAuthenticationDto.setAuthorities(authorities);
        }
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), userAuthenticationDto);
    }

    @JmsListener(destination = JmsConfig.SET_USER_OTP_QUEUE)
    public void listenForSetOTP(UserAuthenticationEvent userAuthenticationEvent) {
        UserAuthenticationDto userAuthenticationDto = userAuthenticationEvent.getUserAuthenticationDto();

        Optional<User> fetchUser = userRepository.findById(userAuthenticationDto.getId());
        if (fetchUser.isPresent()) {
            User user = fetchUser.get();
            user.setOneTimePassword(userAuthenticationDto.getOneTimePassword());
            userRepository.save(user);
        }
    }
}

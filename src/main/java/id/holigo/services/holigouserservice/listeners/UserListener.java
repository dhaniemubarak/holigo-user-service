package id.holigo.services.holigouserservice.listeners;

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

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.config.JmsConfig;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserListener {

    @Autowired
    private final JmsTemplate jmsTemplate;

    @Autowired
    private final UserRepository userRepository;

    @JmsListener(destination = JmsConfig.GET_USER_DATA_QUEUE)
    public void listen(@Payload UserDto userDto, @Headers MessageHeaders headers, Message message)
            throws JmsException, JMSException {
        Optional<User> fetchUser = userRepository.findByPhoneNumber(userDto.getPhoneNumber());
        if (fetchUser.isPresent()) {
            User user = fetchUser.get();
            userDto.setId(user.getId());
        }
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), userDto);
    }
}

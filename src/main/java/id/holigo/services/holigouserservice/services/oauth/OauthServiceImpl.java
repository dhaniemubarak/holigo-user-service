package id.holigo.services.holigouserservice.services.oauth;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import id.holigo.services.common.model.OauthAccessTokenDto;
import id.holigo.services.common.model.UserAuthenticationDto;
import id.holigo.services.holigouserservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OauthServiceImpl implements OauthService {

    private final JmsTemplate jmsTemplate;

    private final ObjectMapper objectMapper;

    @Override
    public OauthAccessTokenDto createAccessToken(UserAuthenticationDto userAuthenticationDto)
            throws JsonMappingException, JsonProcessingException, JMSException {
        log.info("create access token is running ....");
        Message received = jmsTemplate.sendAndReceive(JmsConfig.CREATE_ACCESS_TOKEN_QUEUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message message = null;
                try {
                    message = session.createTextMessage(objectMapper.writeValueAsString(userAuthenticationDto));
                    message.setStringProperty("_type", "id.holigo.services.common.model.UserAuthenticationDto");
                } catch (JsonProcessingException e) {
                    throw new JMSException(e.getMessage());
                }
                return message;
            }
        });
        OauthAccessTokenDto oauthDto = objectMapper.readValue(received.getBody(String.class),
                OauthAccessTokenDto.class);
        return oauthDto;
    }

}

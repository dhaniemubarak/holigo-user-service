package id.holigo.services.holigouserservice.services.otp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import id.holigo.services.common.model.OtpDto;
import id.holigo.services.common.model.OtpStatusEnum;
import id.holigo.services.holigouserservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final JmsTemplate jmsTemplate;

    private final ObjectMapper objectMapper;

    public boolean isRegisterIdValid(Long registerId, String phoneNumber)
            throws JsonProcessingException, JmsException, JMSException {
        boolean isValid = false;
        OtpDto otpDto = OtpDto.builder().id(registerId).phoneNumber(phoneNumber).build();
        Message received = jmsTemplate.sendAndReceive(JmsConfig.OTP_REGISTER_VALIDATION_QUEUE, session -> {
            Message message = null;
            try {
                message = session.createTextMessage(objectMapper.writeValueAsString(otpDto));
            } catch (JsonProcessingException e) {
                throw new JMSException(e.getMessage());
            }
            message.setStringProperty("_type", "id.holigo.services.common.model.OtpDto");

            return message;
        });
        assert received != null;
        OtpDto result = objectMapper.readValue(received.getBody(String.class), OtpDto.class);

        if (result.getStatus() == OtpStatusEnum.CONFIRMED) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    public OtpDto getOtpForResetPin(OtpDto otpDto)
            throws JsonProcessingException, JMSException {
        Message received = jmsTemplate.sendAndReceive(JmsConfig.OTP_RESET_PIN_VALIDATION_QUEUE, session -> {
            Message message = null;
            try {
                message = session.createTextMessage(objectMapper.writeValueAsString(otpDto));
            } catch (JsonProcessingException e) {
                throw new JMSException(e.getMessage());
            }
            message.setStringProperty("_type", "id.holigo.services.common.model.OtpDto");

            return message;
        });
        assert received != null;
        return objectMapper.readValue(received.getBody(String.class), OtpDto.class);
    }

    @Override
    public void updateOtpStatus(OtpDto otpDto) {
        jmsTemplate.convertAndSend(JmsConfig.OTP_LOGIN_VALIDATION_QUEUE, otpDto);
    }
}

package id.holigo.services.holigouserservice.services.otp;

import javax.jms.JMSException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import id.holigo.services.common.model.OtpDto;

public interface OtpService {

    boolean isRegisterIdValid(Long registerId, String phoneNumber)
            throws JsonProcessingException, JMSException;

    OtpDto getOtpForResetPin(OtpDto otpDto) throws JsonProcessingException, JMSException;

    void updateOtpStatus(OtpDto otpDto);

}

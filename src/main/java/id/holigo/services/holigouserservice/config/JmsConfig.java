package id.holigo.services.holigouserservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {
    public static final String OTP_REGISTER_VALIDATION_QUEUE = "request-otp-register-validation";
    public static final String OTP_LOGIN_VALIDATION_QUEUE = "request-otp-login-validation";
    public static final String OAUTH_USER_DATA_QUEUE = "oauth-user-data";
    public static final String OAUTH_RESET_OTP_QUEUE = "oauth-reset-otp-queue";
    public static final String GET_USER_DATA_BY_PHONE_NUMBER_QUEUE = "get-user-data-by-phone-number-queue";
    public static final String GET_USER_DATA_BY_ID_QUEUE = "get-user-data-by-id-queue";
    public static final String SET_USER_OTP_QUEUE = "set-user-otp-queue";
    public static final String CREATE_ACCESS_TOKEN_QUEUE = "create-access-token-queue";
    public static final String OTP_RESET_PIN_VALIDATION_QUEUE = "request-otp-reset-pin-validation";
    public static final String UPDATE_USER_GROUP_IN_USER_QUEUE = "update-user-group-in-user";
    public static final String CREATE_POINT_BY_USER_ID_QUEUE = "create-point-by-user-id-queue";
    public static final String CREATE_USER_GROUP = "create-user-group";

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}

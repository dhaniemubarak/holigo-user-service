package id.holigo.services.holigouserservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {
    public static final String OTP_REGISTER_VALIDATION_QUEUE = "request-otp-register-validation";
    public static final String OAUTH_USER_DATA_QUEUE = "oauth-user-data";
    public static final String GET_USER_DATA_QUEUE = "get-user-data-queue";
    public static final String SET_USER_OTP_QUEUE = "set-user-otp-queue";
    public static final String CREATE_ACCESS_TOKEN_QUEUE = "create-access-token-queue";

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}

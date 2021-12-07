package id.holigo.services.holigouserservice.services.oauth;

import javax.jms.JMSException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import id.holigo.services.common.model.OauthAccessTokenDto;
import id.holigo.services.common.model.UserAuthenticationDto;

public interface OauthService {

    OauthAccessTokenDto createAccessToken(UserAuthenticationDto userAuthenticationDto)
            throws JsonMappingException, JsonProcessingException, JMSException;
}

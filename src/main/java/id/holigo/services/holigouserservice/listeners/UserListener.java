package id.holigo.services.holigouserservice.listeners;

import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;

import id.holigo.services.common.model.UserDeviceDto;
import id.holigo.services.holigouserservice.domain.UserDevice;
import id.holigo.services.holigouserservice.repositories.UserDeviceRepository;
import id.holigo.services.holigouserservice.web.mappers.UserDeviceMapper;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import id.holigo.services.common.events.UserGroupEvent;
import id.holigo.services.common.model.UpdateUserGroupDto;
import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.config.JmsConfig;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.web.mappers.UserMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserListener {

    private final JmsTemplate jmsTemplate;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserDeviceMapper userDeviceMapper;

    private final UserDeviceRepository userDeviceRepository;

    @Transactional
    @JmsListener(destination = JmsConfig.GET_USER_DATA_BY_PHONE_NUMBER_QUEUE)
    public void listenUserByPhoneNumber(@Payload UserDto userDto, @Headers MessageHeaders headers, Message message)
            throws JmsException, JMSException {
        Optional<User> fetchUser = userRepository.findByPhoneNumber(userDto.getPhoneNumber());
        if (fetchUser.isPresent()) {
            User user = fetchUser.get();
            userDto = userMapper.userToUserDto(user);
        }
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), userDto);
    }

    @Transactional
    @JmsListener(destination = JmsConfig.GET_USER_DATA_BY_ID_QUEUE)
    public void listenUserById(@Payload UserDto userDto, @Headers MessageHeaders headers, Message message)
            throws JmsException, JMSException {
        Optional<User> fetchUser = userRepository.findById(userDto.getId());
        if (fetchUser.isPresent()) {
            User user = fetchUser.get();
            userDto = userMapper.userToUserDto(user);
        }
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), userDto);
    }

    @JmsListener(destination = JmsConfig.UPDATE_USER_GROUP_IN_USER_QUEUE)
    public void listenForUpdateUserGroup(UserGroupEvent userGroupEvent) {
        UpdateUserGroupDto updateUserGroupDto = userGroupEvent.getUpdateUserGroupDto();
        User user = userRepository.getById(updateUserGroupDto.getUserId());
        user.setUserGroup(updateUserGroupDto.getUserGroup());
        userRepository.save(user);
    }

    @JmsListener(destination = JmsConfig.CREATE_USER_DEVICE)
    public void listenForCreateDeviceUser(@Payload UserDeviceDto userDeviceDto) {
        UserDevice userDevice = userDeviceMapper.userDeviceDtoToUserDevice(userDeviceDto);
        User user = userRepository.getById(userDeviceDto.getUserId());
        userDevice.setUser(user);
        userDeviceRepository.save(userDevice);
        user.setMobileToken(userDeviceDto.getMobileToken());
        userRepository.save(user);
    }
}

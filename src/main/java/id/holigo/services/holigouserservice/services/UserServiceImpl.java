package id.holigo.services.holigouserservice.services;

import org.springframework.stereotype.Service;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.web.exceptions.NotFoundException;
import id.holigo.services.holigouserservice.web.mappers.UserMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto findById(Long id) {
        return userMapper.userToUserDto(userRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @Override
    public UserDto save(UserDto userDto) {
        return userMapper.userToUserDto(userRepository.save(userMapper.userDtoToUser(userDto)));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User resultUser = userRepository.findById(id).orElseThrow(NotFoundException::new);
        resultUser.setName(userDto.getName());
        resultUser.setEmail(userDto.getEmail());
        resultUser.setPhoneNumber(userDto.getPhoneNumber());
        resultUser.setType(userDto.getType());
        return userMapper.userToUserDto(userRepository.save(resultUser));
    }

}

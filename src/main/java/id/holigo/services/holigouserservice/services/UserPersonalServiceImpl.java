package id.holigo.services.holigouserservice.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserPersonal;
import id.holigo.services.holigouserservice.repositories.UserPersonalRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.web.exceptions.NotFoundException;
import id.holigo.services.holigouserservice.web.mappers.UserPersonalMapper;
import id.holigo.services.holigouserservice.web.model.UserPersonalDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserPersonalServiceImpl implements UserPersonalService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserPersonalMapper userPersonalMapper;

    @Autowired
    private final UserPersonalRepository userPersonalRepository;

    @Override
    public UserPersonalDto getUserPersonalByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User fetchUser = user.get();
        UserPersonal userPersonal = fetchUser.getUserPersonal();
        if (userPersonal == null) {
            return null;
        }
        return userPersonalMapper.userPersonalToUserPersonalDto(userPersonal);
    }

    @Transactional
    @Override
    public UserPersonalDto createUserPersonalByUserId(Long userId, UserPersonalDto userPersonalDto) throws Exception {
        UserPersonal userPersonalSaved;
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User fetchUser = user.get();
        UserPersonal userPersonal = userPersonalMapper.userPersonalDtoToUserPersonal(userPersonalDto);
        userPersonal.setUser(fetchUser);
        userPersonalSaved = userPersonalRepository.save(userPersonal);
        if (userPersonalSaved.getId() == null) {
            throw new Exception("Opps, something went wrong. We're working on it. Please try again leter.");
        }
        try {
            fetchUser.setUserPersonal(userPersonalSaved);
            userRepository.save(fetchUser);
        } catch (Exception e) {
            throw new Exception("Opps, something went wrong. We're working on it. Please try again leter.");
        }
        return userPersonalMapper.userPersonalToUserPersonalDto(userPersonalSaved);
    }

    @Override
    public UserPersonalDto updateUserPersonal(Long personalId, UserPersonalDto userPersonalDto) {
        Optional<UserPersonal> userPersonal = userPersonalRepository.findById(personalId);
        if (userPersonal.isEmpty()) {
            throw new NotFoundException("Personal data not found");
        }
        UserPersonal fetchUserPersonal = userPersonal.get();
        UserPersonal updateUserPersonal = userPersonalMapper.userPersonalDtoToUserPersonal(userPersonalDto);
        updateUserPersonal.setId(fetchUserPersonal.getId());
        UserPersonal userPersonalUpdated = userPersonalRepository.save(updateUserPersonal);
        return userPersonalMapper.userPersonalToUserPersonalDto(userPersonalUpdated);
    }

}

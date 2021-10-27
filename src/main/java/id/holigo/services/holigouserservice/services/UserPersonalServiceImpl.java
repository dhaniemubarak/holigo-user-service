package id.holigo.services.holigouserservice.services;

import org.springframework.stereotype.Service; 

import id.holigo.services.holigouserservice.web.model.UserPersonalDto;

@Service
public class UserPersonalServiceImpl implements UserPersonalService{

    @Override
    public UserPersonalDto getUserPersonalByUserId(Long userId) {
        
        return null;
    }

    @Override
    public UserPersonalDto createUserPersonal(Long userId, UserPersonalDto userPersonalDto) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserPersonalDto updateUserPersonal(Long personalId, UserPersonalDto userPersonalDto) {
        // TODO Auto-generated method stub
        return null;
    }
    
}

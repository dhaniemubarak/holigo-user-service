package id.holigo.services.holigouserservice.services.guest;

import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.web.model.GuestRegisterDto;

public interface GuestService {

    User createGuest(GuestRegisterDto guestRegisterDto);

    void updateGuest(User user);
}

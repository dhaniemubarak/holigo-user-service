package id.holigo.services.holigouserservice.services.holiclub;

import id.holigo.services.common.events.CreateHoliclubEvent;
import id.holigo.services.common.model.UserClubDto;
import id.holigo.services.holigouserservice.config.JmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class HoliclubServiceImpl implements HoliclubService {

    private JmsTemplate jmsTemplate;

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void createUserClub(UserClubDto userClubDto) {
        jmsTemplate.convertAndSend(JmsConfig.CREATE_USER_GROUP, new CreateHoliclubEvent(userClubDto));
    }
}

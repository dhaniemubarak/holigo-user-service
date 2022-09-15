package id.holigo.services.holigouserservice.services.point;

import id.holigo.services.common.model.PointDto;
import id.holigo.services.holigouserservice.config.JmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class PointServiceImpl implements PointService {

    private JmsTemplate jmsTemplate;

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void createPoint(Long userId) {
        jmsTemplate.convertAndSend(JmsConfig.CREATE_USER_POINT, PointDto.builder().userId(userId).build());
    }
}

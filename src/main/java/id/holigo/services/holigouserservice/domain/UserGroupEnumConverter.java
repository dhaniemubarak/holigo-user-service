package id.holigo.services.holigouserservice.domain;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserGroupEnumConverter implements AttributeConverter<UserGroupEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserGroupEnum attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getCode();
    }

    @Override
    public UserGroupEnum convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(UserGroupEnum.values()).filter(c -> c.getCode().equals(dbData)).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}

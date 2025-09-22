package br.com.financialchatbot.backend.infrastructure.persistence.mappers;

import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.infrastructure.persistence.models.UserDataModel;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDataModel toDataModel(User user) {
        return new UserDataModel(user.chatId(), user.firstName());
    }

    public User toDomainEntity(UserDataModel dataModel) {
        return new User(dataModel.getId(), dataModel.getChatId(), dataModel.getFirstName());
    }
}
package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.UserGateway;
import br.com.financialchatbot.backend.infrastructure.persistence.mappers.UserMapper;
import br.com.financialchatbot.backend.infrastructure.persistence.repositories.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDatabaseGateway implements UserGateway {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    public UserDatabaseGateway(UserJpaRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        var dataModel = userMapper.toDataModel(user);
        var savedDataModel = userRepository.save(dataModel);
        return userMapper.toDomainEntity(savedDataModel);
    }

    @Override
    public Optional<User> findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId)
                .map(userMapper::toDomainEntity);
    }
}
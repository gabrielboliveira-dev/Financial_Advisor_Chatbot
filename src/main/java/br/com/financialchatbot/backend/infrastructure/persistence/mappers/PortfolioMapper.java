package br.com.financialchatbot.backend.infrastructure.persistence.mappers;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import br.com.financialchatbot.backend.infrastructure.persistence.models.PortfolioDataModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PortfolioMapper {

    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    public PortfolioMapper(UserMapper userMapper, ObjectMapper objectMapper) {
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
    }

    public PortfolioDataModel toDataModel(Portfolio portfolio) {
        try {
            String assetsAsJson = objectMapper.writeValueAsString(portfolio.getAssets());
            var ownerDataModel = userMapper.toDataModel(portfolio.getOwner());
            ownerDataModel.setId(portfolio.getOwner().id());

            var dataModel = new PortfolioDataModel(ownerDataModel, assetsAsJson);
            dataModel.setId(portfolio.getId());
            return dataModel;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting portfolio assets to JSON", e);
        }
    }

    public Portfolio toDomainEntity(PortfolioDataModel dataModel) {
        try {
            List<PortfolioAsset> assets = objectMapper.readValue(dataModel.getAssetsJson(), new TypeReference<>() {});
            var ownerEntity = userMapper.toDomainEntity(dataModel.getOwner());
            return new Portfolio(dataModel.getId(), ownerEntity, assets);
        } catch (JsonProcessingException e) {
            var ownerEntity = userMapper.toDomainEntity(dataModel.getOwner());
            return new Portfolio(dataModel.getId(), ownerEntity, Collections.emptyList());
        }
    }
}
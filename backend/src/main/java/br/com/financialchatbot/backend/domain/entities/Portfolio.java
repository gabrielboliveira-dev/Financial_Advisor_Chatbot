package br.com.financialchatbot.backend.domain.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Portfolio {

    private final Long id;
    private final User owner;
    private final List<PortfolioAsset> assets;

    public Portfolio(Long id, User owner, List<PortfolioAsset> assets) {
        this.id = id;
        this.owner = owner;
        this.assets = new ArrayList<>(assets != null ? assets : List.of());
    }

    public void removeAsset(String tickerToRemove) {
        this.assets.removeIf(asset -> asset.ticker().equalsIgnoreCase(tickerToRemove));
    }

    public void addAsset(PortfolioAsset assetToAdd) {
        Optional<PortfolioAsset> existingAssetOpt = this.assets.stream()
                .filter(a -> a.ticker().equalsIgnoreCase(assetToAdd.ticker()))
                .findFirst();

        if (existingAssetOpt.isPresent()) {
            PortfolioAsset existingAsset = existingAssetOpt.get();
            this.assets.remove(existingAsset);

            int newQuantity = existingAsset.quantity() + assetToAdd.quantity();

            BigDecimal existingTotalValue = existingAsset.averagePrice().multiply(BigDecimal.valueOf(existingAsset.quantity()));
            BigDecimal newTotalValue = assetToAdd.averagePrice().multiply(BigDecimal.valueOf(assetToAdd.quantity()));
            BigDecimal totalValue = existingTotalValue.add(newTotalValue);
            BigDecimal newAveragePrice = totalValue.divide(BigDecimal.valueOf(newQuantity), 4, RoundingMode.HALF_UP);

            PortfolioAsset updatedAsset = new PortfolioAsset(existingAsset.ticker(), newQuantity, newAveragePrice);
            this.assets.add(updatedAsset);
        } else {
            this.assets.add(assetToAdd);
        }
    }

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public List<PortfolioAsset> getAssets() {
        return List.copyOf(assets);
    }
}
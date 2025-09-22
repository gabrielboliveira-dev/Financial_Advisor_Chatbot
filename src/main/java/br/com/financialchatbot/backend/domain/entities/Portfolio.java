package br.com.financialchatbot.backend.domain.entities;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {

    private final Long id;
    private final User owner;
    private final List<PortfolioAsset> assets;

    public Portfolio(Long id, User owner, List<PortfolioAsset> assets) {
        this.id = id;
        this.owner = owner;
        this.assets = new ArrayList<>(assets != null ? assets : List.of());
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
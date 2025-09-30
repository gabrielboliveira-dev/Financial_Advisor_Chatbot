package br.com.financialchatbot.backend.infrastructure.persistence.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolios")
public class PortfolioDataModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserDataModel owner;

    @Column(columnDefinition = "TEXT")
    private String assetsJson;

    public PortfolioDataModel() {
    }

    public PortfolioDataModel(UserDataModel owner, String assetsJson) {
        this.owner = owner;
        this.assetsJson = assetsJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDataModel getOwner() {
        return owner;
    }

    public void setOwner(UserDataModel owner) {
        this.owner = owner;
    }

    public String getAssetsJson() {
        return assetsJson;
    }

    public void setAssetsJson(String assetsJson) {
        this.assetsJson = assetsJson;
    }
}
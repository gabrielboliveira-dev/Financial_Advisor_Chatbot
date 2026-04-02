package br.com.financialchatbot.backend.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

    private Portfolio portfolio;
    private User dummyUser;

    @BeforeEach
    void setUp() {
        dummyUser = new User(1L, 123456L, "Test User", null);
        portfolio = new Portfolio(1L, dummyUser, new ArrayList<>());
    }

    @Test
    void addAsset_WhenAssetIsNew_ShouldAddToList() {
        // Arrange
        PortfolioAsset newAsset = new PortfolioAsset("PETR4", 100, new BigDecimal("35.00"));

        // Act
        portfolio.addAsset(newAsset);

        // Assert
        assertEquals(1, portfolio.getAssets().size());
        assertEquals("PETR4", portfolio.getAssets().get(0).ticker());
        assertEquals(100, portfolio.getAssets().get(0).quantity());
        assertEquals(new BigDecimal("35.00"), portfolio.getAssets().get(0).averagePrice());
    }

    @Test
    void addAsset_WhenAssetAlreadyExists_ShouldUpdateQuantityAndAveragePrice() {
        // Arrange
        portfolio.addAsset(new PortfolioAsset("PETR4", 100, new BigDecimal("30.00")));
        PortfolioAsset additionalAsset = new PortfolioAsset("PETR4", 100, new BigDecimal("40.00"));

        // Act
        portfolio.addAsset(additionalAsset);

        // Assert
        assertEquals(1, portfolio.getAssets().size());
        PortfolioAsset updatedAsset = portfolio.getAssets().get(0);
        assertEquals("PETR4", updatedAsset.ticker());
        assertEquals(200, updatedAsset.quantity());
        
        // (100*30 + 100*40) / 200 = 35.00
        assertEquals(new BigDecimal("35.0000"), updatedAsset.averagePrice());
    }

    @Test
    void removeAsset_WhenAssetExists_ShouldRemoveFromList() {
        // Arrange
        portfolio.addAsset(new PortfolioAsset("PETR4", 100, new BigDecimal("30.00")));
        portfolio.addAsset(new PortfolioAsset("ITUB4", 50, new BigDecimal("25.00")));

        // Act
        portfolio.removeAsset("PETR4");

        // Assert
        assertEquals(1, portfolio.getAssets().size());
        assertEquals("ITUB4", portfolio.getAssets().get(0).ticker());
    }

    @Test
    void removeAsset_WhenAssetDoesNotExist_ShouldDoNothing() {
        // Arrange
        portfolio.addAsset(new PortfolioAsset("ITUB4", 50, new BigDecimal("25.00")));

        // Act
        portfolio.removeAsset("PETR4");

        // Assert
        assertEquals(1, portfolio.getAssets().size());
        assertEquals("ITUB4", portfolio.getAssets().get(0).ticker());
    }
}

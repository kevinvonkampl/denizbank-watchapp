package watchapp.dto;

public record CryptoDTO(String id, String symbol, String name, double currentPrice, double priceChangePercentage24h) {}
package com.back.global.app.mcp.dto;

public record CategoryStatsDto(
        Long categoryId,
        String categoryName,
        long tradeCount,
        int totalFee
) {
}

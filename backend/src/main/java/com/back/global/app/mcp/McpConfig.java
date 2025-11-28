package com.back.global.app.mcp;

import com.back.global.app.mcp.tool.StatisticTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider tool(StatisticTools statisticTools) {
        return MethodToolCallbackProvider.builder()
                                         .toolObjects(statisticTools)
                                         .build();
    }
}

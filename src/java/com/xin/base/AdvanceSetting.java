package com.xin.base;

import lombok.AllArgsConstructor;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
@AllArgsConstructor
public enum AdvanceSetting {
    INSTANT_INVOKE_AGENT("instant-invoke-agent.jar", "快速调试"),
    DEV_ROUTE_AGENT("dev-route-agent.jar", "本地路由(rocketmq和dubbo 本地路由到本地)");
    private String path;

    public String getPath() {
        return path;
    }

    private String desc;

    public String getDesc() {
        return desc;
    }

}

package com.xin.vo;

import lombok.Data;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
@Data
public class DbDetailInfoVo {
    private String url;
    private String host;
    private int port;
    private String username;
    private String password;
    private String dbName;
}

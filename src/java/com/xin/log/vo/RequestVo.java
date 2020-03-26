package com.xin.log.vo;

import lombok.Data;

import java.util.List;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
@Data
public class RequestVo {
    private String type;
    private String name;
    private long timestamp;
    private String logs;
    private List<String> sqls;

}

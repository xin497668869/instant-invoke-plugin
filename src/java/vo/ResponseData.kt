package vo

/**
 *
 * @author linxixin@cvte.com
 */
data class ResponseData(var dbDetailInfoVo: DbDetailInfoVo?
                        , var sqls: List<String>?  )


data class DbDetailInfoVo(var url: String?
                          , var host: String?
                          , var port: Int?
                          , var username: String?
                          , var password: String?
                          , var dbName: String?  )
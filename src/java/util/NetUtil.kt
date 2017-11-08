package util

import java.net.ServerSocket

/**
 *
 * @author linxixin@cvte.com
 */

object NetUtils {

    val availablePort: Int
        get() {
            ServerSocket().use {
                it.bind(null)
                return it.localPort
            }
        }


}
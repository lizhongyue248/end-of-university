package wiki.zyue.eou.integration

import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.stereotype.Component
import redis.embedded.RedisServer
import java.io.IOException
import java.net.DatagramSocket
import java.net.ServerSocket
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


/**
 * Start an embedded Redis server.
 *
 * 2022/1/5 15:24:59
 * @author echo
 */
@Component
internal class EmbeddedRedis(
  private val redisProperties: RedisProperties
) {
  private val logger = LogFactory.getLog(this::class.java)
  private val redisServer: RedisServer = RedisServer(redisProperties.port)

  @PostConstruct
  fun postConstruct() {
    logger.info("Redis Test Server Start.")
    if (!isPortAvailable(redisProperties.port)) {
      redisServer.start()
    }
  }

  @PreDestroy
  fun preDestroy() {
    if (redisServer.isActive) {
      logger.info("Redis Test Server Stop.")
      redisServer.stop()
    }
  }

  fun isPortAvailable(port: Int): Boolean {
    try {
      ServerSocket(port).use { DatagramSocket(port).use { return true } }
    } catch (e: IOException) {
      return false
    }
  }
}
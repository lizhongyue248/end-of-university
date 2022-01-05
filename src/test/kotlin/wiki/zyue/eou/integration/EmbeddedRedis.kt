package wiki.zyue.eou.integration

import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.stereotype.Component
import redis.embedded.RedisServer
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
  redisProperties: RedisProperties
) {
  private val logger = LogFactory.getLog(this::class.java)
  private val redisServer: RedisServer = RedisServer(redisProperties.port)

  @PostConstruct
  fun postConstruct() {
    logger.info("Redis Test Server Start.")
    redisServer.start()
  }

  @PreDestroy
  fun preDestroy() {
    if (redisServer.isActive) {
      logger.info("Redis Test Server Stop.")
      redisServer.stop()
    }
  }

}
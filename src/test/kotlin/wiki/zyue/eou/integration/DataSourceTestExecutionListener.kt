package wiki.zyue.eou.integration

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.commons.logging.LogFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.test.context.TestContext
import org.springframework.test.context.support.AbstractTestExecutionListener
import org.springframework.util.ResourceUtils
import org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName
import wiki.zyue.eou.base.BaseRepository
import java.io.File


/**
 * 2022/6/23 21:25:21
 * @author echo
 */
class DataSourceTestExecutionListener : AbstractTestExecutionListener() {
  companion object {
    private const val FILE_PREFIX = ".json"
    private const val FILE_ROOT_DIR = "mock/"
  }

  private val mongoContainer: MongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:latest"))
    .withExposedPorts(27017)

  private val redisContainer = GenericContainer(DockerImageName.parse("redis:latest"))
    .withExposedPorts(6379)

  private val log = LogFactory.getLog(DataSourceTestExecutionListener::class.java)


  override fun beforeTestClass(testContext: TestContext) {
    mongoContainer.start()
    redisContainer.start()
    System.getProperties().setProperty("spring.data.mongodb.uri", mongoContainer.getReplicaSetUrl("EOU_TEST"))
    System.getProperties().setProperty("spring.redis.port", "${redisContainer.firstMappedPort}")
    System.getProperties().setProperty("spring.redis.host", redisContainer.host)
  }

  override fun beforeTestMethod(testContext: TestContext) {
    val testClass = testContext.testClass
    val classInit = testClass.getAnnotation(MongoDatabaseInit::class.java) ?: return
    var filename = FILE_ROOT_DIR + classInit.value + testClass.simpleName
    if (!filename.endsWith(FILE_PREFIX)) {
      filename += FILE_PREFIX
    }
    filename = CLASSPATH_URL_PREFIX + filename.replace("/", File.separator).replace("\\", File.separator)
    log.info("[Init] Load test method mongo data file $filename.")
    val file = ResourceUtils.getFile(filename)
    val objectMapper = jacksonObjectMapper()
    val initData = objectMapper.readValue<Array<MongoDataConfig>>(file)
    val result = initData.find { config -> testContext.testMethod.name.startsWith(config.method) } ?: return
    val reactiveMongoTemplate = testContext.applicationContext.getBean(ReactiveMongoTemplate::class.java)
    result.data.forEach { entity ->
      val document = Class.forName(entity.collection)
      val data = entity.data.map { datum ->
        objectMapper.readValue(datum.toString(), document)
      }
      reactiveMongoTemplate.insert(data, document.simpleName.lowercase()).blockLast()
      log.info("[Init] Insert data $data success.")
    }
  }

  override fun afterTestMethod(testContext: TestContext) {
    val repositoryNames = testContext.applicationContext.getBeanNamesForAnnotation(Repository::class.java)
    repositoryNames.forEach { repositoryName ->
      val repository = testContext.applicationContext.getBean(repositoryName)
      if (repository is BaseRepository<*>) {
        repository.deleteAll().block()
        log.info("Mongo $repositoryName delete all data.")
      }
      if (repository is CrudRepository<*, *>) {
        repository.deleteAll()
        log.info("Redis $repositoryName delete all data.")
      }
    }


  }

  override fun afterTestClass(testContext: TestContext) {
    if (mongoContainer.isRunning) {
      mongoContainer.stop()
    }
    if (redisContainer.isRunning) {
      redisContainer.stop()
    }
  }

  override fun getOrder(): Int {
    return 100
  }
}


class KeepAsJsonDeserializer : JsonDeserializer<ArrayNode>() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ArrayNode {
    if (!p.isExpectedStartArrayToken) {
      return ctxt.handleUnexpectedToken(ArrayNode::class.java, p) as ArrayNode
    }
    val nextToken = p.nextToken()
    val nodeFactory = ctxt.nodeFactory
    val arrayNode = nodeFactory.arrayNode()
    while (nextToken != null) {
      arrayNode.add(nodeFactory.textNode(p.text))
    }
    return arrayNode
  }

}

data class MongoData(
  val collection: String,
  val data: ArrayNode
) {
}

data class MongoDataConfig(
  val method: String,
  val data: Array<MongoData>
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is MongoDataConfig) return false

    if (method != other.method) return false
    if (!data.contentEquals(other.data)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = method.hashCode()
    result = 31 * result + data.contentHashCode()
    return result
  }
}
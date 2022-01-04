package wiki.zyue.eou

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import wiki.zyue.eou.base.impl.ExpandRepositoryFactoryBean

@SpringBootApplication
@EnableReactiveMongoRepositories(repositoryFactoryBeanClass = ExpandRepositoryFactoryBean::class)
class EouApplication

fun main(args: Array<String>) {
  runApplication<EouApplication>(*args)
}

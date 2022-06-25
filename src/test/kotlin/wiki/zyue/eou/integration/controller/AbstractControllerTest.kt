package wiki.zyue.eou.integration.controller

import org.junit.jupiter.api.BeforeEach
import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * 2022/1/17 16:13:08
 * @author echo
 */
abstract class AbstractControllerTest {

  protected lateinit var rest: WebTestClient

  @BeforeEach
  internal fun init(
    context: ApplicationContext, restDocumentation: RestDocumentationContextProvider
  ) {
    rest = WebTestClient.bindToApplicationContext(context).configureClient()
      .filter(
        WebTestClientRestDocumentation.documentationConfiguration(restDocumentation)
          .operationPreprocessors()
          .withRequestDefaults(Preprocessors.prettyPrint())
          .withResponseDefaults(Preprocessors.prettyPrint())
      ).build()
  }
}
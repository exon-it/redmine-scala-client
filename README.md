# Redmine REST API Client for Scala

* Core and client modules are built for Scala 2.11 and 2.12.
* Play-WS module is built for Play Framework 2.5/Scala 2.11.
* Play-WS-Standalone module is built for Scala 2.11 and 2.12.
* Uses typelevel:cats to provide monadic DSL for web request/response.
* Uses SLF4J API for logging.
* Based on [TaskAdapter Redmine Java API](https://github.com/taskadapter/redmine-java-api)

## Usage

First, you need to create a `WebClient`, which is used
to process HTTP requests to Redmine REST API.

Implementations for multiple versions of Play Framework HTTP client
are provided (or you can write your own):

1. Play Framework 2.5 (`"by.exonit.redmine.client" %% "client-play25-ws" % version`)
2. Play Framework 2.6 (`"by.exonit.redmine.client" %% "client-play26-ws" % version`)
3. Play-WS Standalone (`"by.exonit.redmine.client" %% "client-play-ws-standalone" % version`)

Let's use Play WS standalone client:

```scala
# Create Play WS client
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.StandaloneAhcWSClient

implicit val system = ActorSystem()
implicit val materializer = ActorMaterializer()

val client = StandaloneAhcWSClient()

# Create a WebClient
import by.exonit.redmine.client.playws.standalone.PlayWSStandaloneWebClient

val webClient = new PlayWSStandaloneWebClient(client)
```

Now you need to create a `RedmineManager`, which is used to access
different API modules. `RedmineManagerFactory` class allows to create
an instance of `RedmineManager` using provided API authenticaton details.

```scala
import by.exonit.redmine.client.managers.impl.RedmineManagerFactory

val manager = RedmineManagerFactory(webClient).createWithApiKey("https://www.redmine.org", "yourPreciousApiKey")
```

Now you can use other 'managers' to interact with API.
Web requests are using [Monix](https://monix.io/) Task API for asynchronous interactions.

For example, let's get issue by its ID:
```scala
val issueManager = manager.issueManager

val issueTask = issueManager.getIssue(IssueId(5432))

# Run the Task and print the result
issueTask.foreachL(println(_)).runAsync
# > Issue [id=5432, subject=SK lang translation - sync to r3627]
```

When finished using the API, use `WebClient.close()` method
to properly close underlying HTTP client:

```scala
webClient.close()
```

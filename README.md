Spracebook is a native Scala SDK that provides asynchronous access to the [Facebook Graph API][1]. It is built heavily on [spray-client][2] for async non-blocking HTTP I/O and [spray-json][3] for parsing JSON responses into Scala case classes.

### sbt

```
"com.pongr" %% "spracebook" % "0.1.0-SNAPSHOT"
```

### Usage

The Facebook Graph API is represented as a trait; each action that can be performed on a resource is represented as a function in this trait. Each function returns a `Future` of the response from the Graph API, parsed into a convenient case class.

``` scala
//setup
implicit val system = ActorSystem()
val ioBridge = IOExtension(system).ioBridge()
val httpClient = system.actorOf(Props(new HttpClient(ioBridge)))
val facebookApiConduit = system.actorOf(
  props = Props(new HttpConduit(httpClient, "graph.facebook.com", 443, sslEnabled = true)),
  name = "facebook-api-conduit"
)
val facebook = new SprayClientFacebookGraphApi(facebookApiConduit)

//examples
val token: String = ???
val user: Future[User] = facebook.getUser(token)
val friends: Future[Seq[User]] = facebook.getFriends(token)
val event: Future[CreatedComment] = facebook.createComment(photoId, "That is totally rad!", token)
```

[1]: https://developers.facebook.com/docs/reference/api/
[2]: http://spray.io/documentation/1.0-M8.1/spray-client/
[3]: https://github.com/spray/spray-json

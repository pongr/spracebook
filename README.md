Spracebook is a native Scala SDK that provides asynchronous access to the [Facebook Graph API][1]. It is built heavily on [spray-client][2] for async non-blocking HTTP I/O and [spray-json][3] for parsing JSON responses into Scala case classes.

While Spracebook is in production use at [Pongr][9], it currently uses many proof-of-concept ideas and will most likely undergo heavy changes in the future. Thus, it's currently only at a 0.1 state. We would love to hear any feedback you have on the approach Spracebook takes to provide an async native Scala SDK experience for a REST web service. We will also be releasing similar libraries for Instagram, Twitter, etc in the future based on these ideas.

### sbt

Spracebook releases are in the central Maven repository. Snapshots are in [https://oss.sonatype.org/content/repositories/snapshots/][4].

```
"com.pongr" %% "spracebook" % "0.1.0-SNAPSHOT"
```

### Usage

The Facebook Graph API is represented as a trait; each action that can be performed on a resource is represented as a function in this trait. Each function returns a `Future` of the response from the Graph API, parsed into a convenient case class.

``` scala
//setup
implicit val system = ActorSystem()
implicit val timeout = Timeout(10 seconds)
implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

val facebook = Future[SprayClientFacebookGraphApi] = for {
  Http.HostConnectorInfo(connector, _) <- IO(Http) ? Http.HostConnectorSetup("graph.facebook.com", 443, true)
} yield {
  new SprayClientFacebookGraphApi(connector)
}

//examples
val token: String = ???
val user: Future[User] = facebook.flatMap(_.getUser(token))
val friends: Future[Seq[User]] = facebook.flatMap(_.getFriends(token))
val event: Future[CreatedComment] = facebook.flatMap(_.createComment(photoId, "That is totally rad!", token))
```

### License

Spracebook is released under the [Apache 2 License][5].

### Credits

* [spray-client][2] for async non-blocking HTTP I/O
* [spray-json][3] for JSON parsing

### Authors

* [Zach Cox][6]
* [Byamba Tumurkhuu][7]
* [Bayarmunkh Davaadorj][8]

[1]: https://developers.facebook.com/docs/reference/api/
[2]: http://spray.io/documentation/1.0-M8.1/spray-client/
[3]: https://github.com/spray/spray-json
[4]: https://oss.sonatype.org/content/repositories/snapshots/
[5]: http://www.apache.org/licenses/LICENSE-2.0.txt
[6]: https://github.com/zcox
[7]: https://github.com/pcetsogtoo
[8]: https://github.com/bayarmunkh
[9]: http://pongr.com

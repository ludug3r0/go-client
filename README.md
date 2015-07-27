[![Build Status](https://snap-ci.com/ludug3r0/go-client/branch/master/build_image)](https://snap-ci.com/ludug3r0/go-client/branch/master)
# go-client

A Clojure & ClojureScript client for playing the Game of Go

## Development Mode

```
lein clean
lein figwheel
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Runnings tests
```
lein test
```

### Runnig auto tests
```
lein cljsbuild auto auto-test
```

## Production Build

```
lein clean
lein cljsbuild once min
lein uberjar
```

### Running
```
java -cp target/go-client-0.2.0-SNAPSHOT-standalone.jar clojure.main -m go-server.core
```

## License

Copyright © 2014-2015 Rafael Oliveira

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

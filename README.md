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

## Production Build

```
lein clean
lein cljsbuild once min
```

## License

Copyright © 2014-2015 Rafael Oliveira

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

#!/bin/bash -e
lein prodbuild
scp target/immutables-0.1.0-SNAPSHOT-standalone.jar clojurecup@immutables.clojurecup.com:immutables.jar

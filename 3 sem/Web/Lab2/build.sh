#!/bin/sh
# Сборка server.jar (библиотека fastcgi кладётся внутрь jar)
rm -rf build && mkdir build
javac --release 17 -encoding UTF-8 -cp lib/fastcgi-lib.jar -d build src/Main.java
cd build && jar xf ../lib/fastcgi-lib.jar com && cd ..
jar cfe server.jar Main -C build .
rm -rf build

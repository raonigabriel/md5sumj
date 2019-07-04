
# md5sumj
A simple example to demonstrate how to use develop a java based version of the md5sum tool.

## Features:
- Stream enabled "multi-file" parallel execution 
- External 3d party library ([commons-codec]([https://commons.apache.org/proper/commons-codec/])) to perform MD5 calculations
- External 3d party library ([piccocli](https://picocli.info/)) for almost identical cli interface as the original "md5sum" tool

- Version information is generated from git metadata using ([git-commit-id-plugin]([https://github.com/git-commit-id/maven-git-commit-id-plugin])) to a git.properties resource file
- Resource is embedded into native
- Docker [multi-stage build](https://docs.docker.com/develop/develop-images/multistage-build/)


## You have Docker installed? Easy! Just do as follows:
```
$ git clone https://github.com/raonigabriel/md5sumj.git
$ cd md5sumj
$ docker build . -t md5sumj
...
$ docker images
...
REPOSITORY          TAG         IMAGE ID        CREATED         SIZE
md5sumj             latest      26026b086996    28 seconds ago  23.3MB
...
$ docker run --rm -it md5sumj
```
---

## To generate metadata for the native-image process, I force the tool to run alongside with a special agent:
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image -jar ./target/md5sumj.jar --help

java -agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image -jar ./target/md5sumj.jar --version

See [this article](https://medium.com/graalvm/introducing-the-tracing-agent-simplifying-graalvm-native-image-configuration-c3b56c486271) for more help.

## To compress executable with upx (further reduces size, but increases loading time)
$ apt-get install upx-ucl
$ upx --ultra-brute md5sumj


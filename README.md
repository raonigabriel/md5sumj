
# md5sumj
A simple example to demonstrate how to use develop a java based version of the md5sum tool and compile it to a native app that **does not require a Java runtime installed at all!!**

This means: faster loading times, faster execution and reduced memory consumption, powered by the excelent [GraalVM](https://www.graalvm.org/) native-image tool, which features an AOT Compiler that translates our JAR into a native linux binary.

This tool serves another purpose: to demonstrate how to build a professional console (CLI) app that handles command-line parameters, commands and arguments. Have you ever run **"mvn --version"**? Or maybe **"mvn -o -s ../../my-custom-settings.xml clean install"**? Those tools can handle **A LOT** of custom parameters, options and arguments. They usually generate help when you use **--help**. They usually print version info when you use **--version**. What if you could build such rich console (CLI) apps? Well we can!! And there is a wonderfull library in Java to help us do just that: **picocli**. 

I'd like to mention: although GraalVM **does work** with Windows and Mac, it is not my top priority to get that working for now and that means: md5sumj native binary generation works only for Linux. Feel free to submit pull requests.

And if you like this project, please click the **Star** button above! It helps me a lot!


## Features:
- Java 8 "Stream" enabled (multi-file parallel execution)
- stdin reading (pipe support)
- External 3d party library ([commons-codec](https://commons.apache.org/proper/commons-codec/)) to perform MD5 calculations
- External 3d party library ([piccocli](https://picocli.info/)) for almost identical cli interface as the original "md5sum" tool
- Version information is generated from git metadata using ([git-commit-id-plugin]([https://github.com/git-commit-id/maven-git-commit-id-plugin])) to a **git.properties** resource file
- Resource (**git.properties**) is embedded into native binary
- "Fat" JAR generated using [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/)
- Docker [multi-stage build](https://docs.docker.com/develop/develop-images/multistage-build/)


## Clone and build with Maven (any JDK >= 1.8 will suffice):
```
$ git clone https://github.com/raonigabriel/md5sumj.git
$ cd md5sumj
$ mvn clean package
$ ls -la ./target
```
---

## Then we generate the native binary using with native-image (GraalVM is required):
```
$ native-image -H:+ReportUnsupportedElementsAtRuntime --no-server --static -jar ./target/md5sumj.jar
$ ls -la
```
---
## If you have GraalVM >= 20.2.0 java11 version, with musl-gcc you can [statically link to muslc](https://www.graalvm.org/release-notes/20_2/#native-image):
```
$ native-image -H:+ReportUnsupportedElementsAtRuntime --no-server --static --libc=musl -jar ./target/md5sumj.jar
$ ls -la
```
Hint: my [raonigabriel/graalvm-playground:java11](https://github.com/raonigabriel/graalvm-playground) docker image has everything ready for that.

---
## Optional steps: this further reduces the executable size from ~12MB down to ~3MB.
### UPX is an executable compressor which unpacks the file before each execution thus this increases loading time a bit.
```
$ strip md5sumj
$ sudo apt-get install upx-ucl
$ upx --ultra-brute md5sumj
$ ls -la
```
---

## You have Docker installed? Much easier! Just do as follows:
```
$ git clone https://github.com/raonigabriel/md5sumj.git
$ cd md5sumj
$ docker build . -t md5sumj
...
$ docker images
...
REPOSITORY          TAG         IMAGE ID        CREATED         SIZE
md5sumj             latest      26026b086996    28 seconds ago  3.46MB
...
$ docker run --rm -it md5sumj --version
md5sumj 1.2.0 build c0a076c
...
```
---
## Since the generated image is executable, you can create a nice alias like:
```
alias md5sumj="docker run --rm --user $(id -u):$(id -g) --workdir $(pwd) -it -v $(pwd):$(pwd) md5sumj $@"
```
---
## Some details:

### Docker base image
We are using [scratch](https://hub.docker.com/_/scratch) as the base image since the executable is now being **statically** linked with muslc, meaning [we don't need shared libs!](https://codeburst.io/docker-from-scratch-2a84552470c8)

---

### To generate metadata for the native-image process, we force the "uber-jar" to be executed at least once on a GraalVM-enabled JDK alongside with a special agent:
```
$ java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image/com.github.raonigabriel/md5sumj -jar ./target/md5sumj.jar --help
$ java -agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image/com.github.raonigabriel/md5sumj -jar ./target/md5sumj.jar --version
```

See [this article](https://medium.com/graalvm/introducing-the-tracing-agent-simplifying-graalvm-native-image-configuration-c3b56c486271) for more help on this.

## TO-DO:
- Apply ProGuard, to optimize / obfuscate the generated JAR and maybe reduce the native binary size and loading times.
- Implement all the missing features (--binary, --check, and so forth)
- Windows native binary support
- Mac native binary support


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
- External 3d party library ([nanojson](https://github.com/mmastrac/nanojson)) to  read and parse embedded **git.properties**.
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

## Optional steps: this further reduces the executable size from ~12MB down to 3MB.
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
md5sumj             latest      26026b086996    28 seconds ago  8.56MB
...
$ docker run --rm -it md5sumj sh
root@8bbf16bce0ee:/# md5sumj --version
```
---

## Some details:

### Docker base image
We are back using [alpine](https://hub.docker.com/_/alpine) as the base image since the executable is now being statically linked, meaning we don't need shared libs.

---

### To generate metadata for the native-image process, we force the "uber-jar" to be executed at least once on a GraalVM-enabled JDK alongside with a special agent:
```
$ java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image -jar ./target/md5sumj.jar --help
$ java -agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image -jar ./target/md5sumj.jar --version
```

See [this article](https://medium.com/graalvm/introducing-the-tracing-agent-simplifying-graalvm-native-image-configuration-c3b56c486271) for more help on this.

## TO-DO:
- Apply ProGuard, to optimize / obfuscate the generated JAR and maybe reduce the native binary size and loading times.
- Use **--initialize-at-build-time** option. This will probably allow us to optimize our **--version** command and avoid the requirement of having **nanojson**.
- Generate a debian package (*.deb)
- Implement all the missing features (--binary, --check, and so forth)
- Windows native binary support
- Mac native binary support

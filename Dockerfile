FROM raonigabriel/graalvm-playground as builder
WORKDIR /md5sumj

# First, just copy the pom.xml and go-offline. It will generate a cache layer with all maven deps. 
COPY pom.xml pom.xml
RUN mvn dependency:go-offline

# After that, we copy the actual project file and build it using offline mode.
COPY . .
RUN mvn -o clean package && \
    native-image --no-server --static -H:Name=md5sumj -H:NumberOfThreads=8 \
    -H:IncludeResources=git.properties -jar ./target/md5sumj.jar && \
    strip md5sumj && chmod +x md5sumj && ls -la

# We generate a two-layer image, with just our binary. No Alpine, no bash, nothing else!
FROM debian:stretch-slim
COPY --from=builder /md5sumj/md5sumj /md5sumj
CMD ["./md5sumj --help"]
FROM raonigabriel/graalvm-playground as builder
WORKDIR /md5sumj

# First, just copy the pom.xml and go-offline. It will generate a cache layer with all maven deps. 
COPY pom.xml pom.xml
RUN mvn dependency:go-offline

# After that, we copy the actual project file and build it using offline mode.
COPY . .
RUN mvn -o clean package && \
    native-image --no-server --static -jar ./target/md5sumj.jar && \
    strip md5sumj && upx --ultra-brute md5sumj && ls -la

# We generate a two-layer image, with the binary generated from the "builder" stage
FROM alpine:latest
COPY --from=builder /md5sumj/md5sumj /bin/md5sumj
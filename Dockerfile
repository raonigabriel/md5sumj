FROM raonigabriel/graalvm-playground as builder
WORKDIR /md5sumj

# First, just copy the pom.xml and go-offline. It will generate a cache layer with all maven deps. 
COPY pom.xml pom.xml
RUN mvn dependency:go-offline

# After that, we copy the actual project file and build it using offline mode.
COPY . .
RUN mvn -o clean package && \
    native-image --no-server -jar ./target/md5sumj.jar && \
    strip md5sumj && ls -la

# We generate a four-layer image, with the binary generated on the "builder" stage
FROM frolvlad/alpine-glibc
COPY --from=builder /md5sumj/md5sumj /bin/md5sumj
ENTRYPOINT ["/bin/md5sumj"]
CMD ["--help"]

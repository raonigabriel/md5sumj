FROM raonigabriel/graalvm-playground as builder
WORKDIR /md5sumj

# First, just copy the pom.xml and go-offline. It will generate a cache layer with all maven deps. 
COPY pom.xml pom.xml
RUN mvn dependency:go-offline

# After that, we copy the actual project file and build it using offline mode.
COPY . .
RUN mvn -o clean package && \
    strip ./target/md5sumj

# We generate a two-layer image, with just our binary. No Alpine, no bash, nothing else!
FROM scratch
COPY --from=builder /md5sumj/target/md5sumj /md5sumj
CMD ["./md5sumj --help"]
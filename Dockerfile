FROM raonigabriel/graalvm-playground:java11 as builder

RUN mkdir -p /home/graalvm/md5sumj
WORKDIR /home/graalvm/md5sumj

# First, just copy the pom.xml and go-offline. It will generate a cache layer with all maven deps. 
COPY --chown=graalvm pom.xml pom.xml
RUN mvn dependency:go-offline

# After that, we copy the actual project files and build it using offline mode.
COPY --chown=graalvm src/ src/
COPY --chown=graalvm .git/ .git/

RUN mvn -o clean package

# We generate a small image, with the binary generated from the "builder" stage
FROM scratch
COPY --from=builder /home/graalvm/md5sumj/target/md5sumj /bin/md5sumj
CMD ["--version"]
ENTRYPOINT ["/bin/md5sumj"]
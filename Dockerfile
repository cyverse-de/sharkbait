FROM clojure:alpine

RUN apk add --update git && \
    rm -rf /var/cache/apk

ARG git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.sharkbait.git-ref="$git_commit" \
      org.iplantc.de.sharkbait.version="$version"

COPY . /usr/src/app

WORKDIR /usr/src/app

RUN lein uberjar && \
    cp target/sharkbait-standalone.jar .

RUN ln -s "/usr/bin/java" "/bin/sharkbait"

ENTRYPOINT ["sharkbait", "-jar", "sharkbait-standalone.jar"]
CMD ["--help"]

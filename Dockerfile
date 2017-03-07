FROM clojure:alpine

RUN apk add --update git && \
    rm -rf /var/cache/apk

WORKDIR /usr/src/app

COPY project.clj /usr/src/app/
RUN lein deps

COPY . /usr/src/app

RUN lein uberjar && \
    cp target/sharkbait-standalone.jar .

RUN ln -s "/usr/bin/java" "/bin/sharkbait"

ENTRYPOINT ["sharkbait", "-jar", "sharkbait-standalone.jar"]
CMD ["--help"]

ARG git_commit=unknown
ARG version=unknown
ARG descriptive_version=unknown

LABEL org.cyverse.git-ref="$git_commit"
LABEL org.cyverse.version="$version"
LABEL org.cyverse.descriptive-version="$descriptive_version"

FROM discoenv/grouper:2.5.68

RUN yum makecache fast && \
    yum install -y git && \
    yum clean all && \
    curl -o /usr/bin/lein https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && \
    chmod +x /usr/bin/lein && \
    lein version

WORKDIR /usr/src/app/

COPY . /usr/src/app

RUN lein do clean, deps

RUN lein uberjar && \
    cp target/sharkbait-standalone.jar .

RUN ln -s "/usr/bin/java" "/bin/sharkbait"

ENV CLASSPATH="$GROUPER_HOME/classes"

ENTRYPOINT ["sharkbait", "-jar", "sharkbait-standalone.jar"]
CMD ["--help"]

ARG git_commit=unknown
ARG version=unknown
ARG descriptive_version=unknown

LABEL org.cyverse.git-ref="$git_commit"
LABEL org.cyverse.version="$version"
LABEL org.cyverse.descriptive-version="$descriptive_version"
LABEL org.label-schema.vcs-ref="$git_commit"
LABEL org.label-schema.vcs-url="https://github.com/cyverse-de/sharkbait"
LABEL org.label-schema.version="$descriptive_version"

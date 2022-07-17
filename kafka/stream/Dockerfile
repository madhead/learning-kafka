FROM eclipse-temurin:17

ADD ./build/install/stream/ /opt/stream/

CMD /opt/stream/bin/stream kafka:29092 mediawiki.recentchange mediawiki.recentchange.rate.by_wiki

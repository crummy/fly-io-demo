FROM openjdk:16

ADD target/fly-io-demo-1.0-SNAPSHOT-jar-with-dependencies.jar /app/ping.jar

WORKDIR app

CMD java -jar ping.jar
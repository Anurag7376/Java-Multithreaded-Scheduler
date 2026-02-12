FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY src ./src

RUN cd src && javac *.java && jar --create --file scheduler.jar --main-class Main *.class

EXPOSE 8080

CMD ["sh", "-c", "cd src && java -jar scheduler.jar"]

FROM openjdk:17
ARG JAR_FILE=*.jar
EXPOSE 8080
COPY ${JAR_FILE} pic_validity_check.jar
ENTRYPOINT ["java", "-jar", "pic_validity_check.jar"]
# docker rmi pic_validity_check:latest
# docker build -t pic_validity_check .
# image: pic_validity_check:latest

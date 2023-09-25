# Use uma imagem base do OpenJDK como base
FROM openjdk:19-jdk-alpine

# Exponha a porta em que a sua aplicação Spring Boot está configurada para ouvir (aqui é a porta padrão 8080)
EXPOSE 8080

# Defina um diretório de trabalho dentro do contêiner
ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} app.jar

# Comando para iniciar a aplicação quando o contêiner for executado
ENTRYPOINT ["java","-jar","/app.jar"]

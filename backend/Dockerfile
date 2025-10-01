# Usa imagem base com Java 21 (runtime apenas)
FROM eclipse-temurin:21-jre-jammy

# Define variáveis de ambiente para a aplicação
ENV SERVER_PORT=8084

# Copia o JAR para dentro do container
COPY target/precatorio.jar /opt/precatorio/precatorio.jar

# Define o diretório de trabalho
WORKDIR /opt/precatorio

# Expõe a porta da aplicação
EXPOSE 8084

# Comando de inicialização
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/precatorio/precatorio.jar" ]

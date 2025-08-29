# 将你的后端的Dockerfile复制到这里
FROM --platform=$BUILDPLATFORM maven:3.9.2-eclipse-temurin-17 AS development
WORKDIR /code
COPY pom.xml /code/pom.xml
# 提前下载所有依赖项
RUN mvn dependency:go-offline

FROM development AS dev-envs
RUN sed -i s@/deb.debian.org/@/mirrors.aliyun.com/@g /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y --no-install-recommends git

COPY src /code/src
EXPOSE 8080
CMD ["mvn", "spring-boot:run"]
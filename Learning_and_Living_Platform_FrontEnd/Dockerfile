# 将你的前端的Dockerfile复制到这里
FROM --platform=$BUILDPLATFORM node:20 AS development
WORKDIR /code
COPY package*.json /code/package*.json
# 提前下载所有依赖项
RUN npm init -y
RUN npm cache clean --force
RUN npm install

FROM development AS dev-envs
RUN sed -i s@/deb.debian.org/@/mirrors.aliyun.com/@g /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y --no-install-recommends git

COPY . /code
EXPOSE 80
CMD ["npm", "run", "serve"]
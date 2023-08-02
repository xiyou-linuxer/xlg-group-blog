FROM ubuntu:latest

# 设置工作目录
WORKDIR /xlg-group-blog-server

COPY ./blog .

RUN apt update
RUN apt-get install -y openjdk-11-jdk
RUN apt install -y wget
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt install -y ./google-chrome-stable_current_amd64.deb

ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $PATH:$JAVA_HOME/bin

# 暴露端口
EXPOSE 6789

# 指定 程序运行时的命令
CMD [ "java", "-jar","groupblog-1.0.1-SNAPSHOT.jar","-Dio.netty.tryReflectionSetAccessible=false"]


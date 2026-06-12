FROM maven:3.8.6-openjdk-11

LABEL maintainer="Shubhank Sharma"
LABEL project="Parabank Automation Framework"

RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    curl \
    --no-install-recommends

RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub \
    | apt-key add -

RUN echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
    >> /etc/apt/sources.list.d/google-chrome.list

RUN apt-get update && apt-get install -y \
    google-chrome-stable \
    --no-install-recommends

RUN google-chrome --version

RUN apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY . .

CMD ["mvn","test","-Dsurefire.suiteXmlFiles=testng-negative.xml"]
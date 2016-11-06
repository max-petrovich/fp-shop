FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/shop.jar /shop/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/shop/app.jar"]

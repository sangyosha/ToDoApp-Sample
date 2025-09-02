# =========================
# ビルド用イメージ
# =========================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests -B

# =========================
# 実行用イメージ
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
CMD ["sh", "-c", "java -jar app.jar --server.port=$PORT"]


# Render が割り当てるポートを環境変数から受け取る
ENV PORT=8080

# Spring Boot に Render の PORT を渡す
CMD ["sh", "-c", "java -jar app.jar --server.port=$PORT"]



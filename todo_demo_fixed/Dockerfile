# =========================
# ビルド用イメージ
# =========================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Maven Wrapper をコピー
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# 依存関係を先に取得（キャッシュ効率化）
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# ソースをコピー
COPY src src

# jar をビルド
RUN ./mvnw clean package -DskipTests -B

# =========================
# 実行用イメージ
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# ビルド済み jar をコピー
COPY --from=build /app/target/*.jar app.jar

# Render が割り当てるポートを使う
ENV PORT=8080

# Spring Boot 起動
CMD ["sh", "-c", "java -jar app.jar --server.port=$PORT"]





# =========================
# ビルド用イメージ
# =========================
FROM eclipse-temurin:21-jdk AS build

# 作業ディレクトリを作成
WORKDIR /app

# Maven Wrapperとpom.xmlをコピー
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# 依存関係を先にダウンロード（キャッシュ効率化）
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

# build ステージで作られた jar をコピー
COPY --from=build /app/target/*.jar app.jar

# Render が割り当てるポートを環境変数から受け取る
ENV PORT=8083

# Spring Boot に Render の PORT を渡す
CMD ["sh", "-c", "java -jar app.jar --server.port=$PORT"]

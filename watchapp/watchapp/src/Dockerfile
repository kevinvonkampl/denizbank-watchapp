# --- AŞAMA 1: Build Aşaması ---
# Uygulamayı derlemek için tam bir JDK ve Maven ortamı olan bir imaj kullanıyoruz.
FROM eclipse-temurin:17-jdk-jammy AS builder

# Çalışma dizinini /app olarak ayarlıyoruz.
WORKDIR /app

# Önbellekleme (caching) optimizasyonu için önce sadece bağımlılıkları yöneten dosyaları kopyala.
# Bu sayede, kod değişse bile bağımlılıklar değişmediyse, Maven her seferinde her şeyi indirmez.
COPY pom.xml .
COPY .mvn/ .mvn
COPY mvnw .
COPY mvnw.cmd .

# Maven bağımlılıklarını indir.
RUN ./mvnw dependency:go-offline

# Şimdi tüm proje kaynak kodunu kopyala.
COPY src ./src

# Uygulamayı paketle (testleri atlayarak build sürecini hızlandır).
RUN ./mvnw clean package -DskipTests


# --- AŞAMA 2: Çalıştırma Aşaması ---
# Sadece Java Runtime Environment (JRE) içeren çok daha küçük bir temel imaj kullanıyoruz.
FROM eclipse-temurin:17-jre-jammy

# Çalışma dizinini /app olarak ayarlıyoruz.
WORKDIR /app

# Önceki 'builder' aşamasında oluşturulan .jar dosyasını bu yeni imajın içine kopyala.
COPY --from=builder /app/target/*.jar app.jar

# Uygulamanın çalışacağı portu belirtiyoruz.
EXPOSE 8080

# Konteyner çalıştığında çalıştırılacak olan komut.
ENTRYPOINT ["java", "-jar", "app.jar"]
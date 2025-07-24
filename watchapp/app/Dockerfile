# --- Adım 1: Temel İmaj ---
FROM mingc/android-build-box:latest

# --- Adım 2: Çalışma Dizini Belirleme ---
WORKDIR /project

# --- Adım 3: Proje Dosyalarını Kopyalama ---
# Önce derleme için gerekli olan tüm Gradle yapılandırma dosyalarını kopyala.
COPY gradlew ./
COPY gradle/ ./gradle/
COPY settings.gradle ./
COPY build.gradle ./
COPY gradle.properties ./
COPY local.properties ./

# Sonra uygulamanın kaynak kodunu kopyala.
COPY app/ ./app/

# --- Adım 4: Gradle Wrapper'ı Çalıştırılabilir Yapma ---
RUN chmod +x ./gradlew

# --- Adım 5: Derleme Komutu ---
CMD ["./gradlew", "clean", "assembleDebug"]
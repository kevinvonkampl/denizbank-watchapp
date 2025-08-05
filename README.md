# DenizBank - WatchApp Projesi

> [!WARNING]
> **Önemli Güvenlik Notu**
>
> Bu proje bir bootcamp çalışmasıdır ve hızla geliştirilmiştir. Kod içerisinde görebileceğiniz API anahtarları, token'lar veya diğer kimlik bilgileri (**credentials**) demo amaçlı olup, proje tamamlandıktan sonra **tamamı geçersiz kılınmıştır (deprecated).**
>
> Projeyi kendi ortamınızda çalıştırmak için lütfen `application.yml` (backend) ve diğer ilgili yapılandırma dosyalarındaki bu bilgileri **kendi geçerli API anahtarlarınız ile değiştirin.** Basit bir eğitim projesi olduğu için, üretim ortamı güvenlik standartları (credential'ların güvenli saklanması vb.) bu çalışmada önceliklendirilmemiştir. Kod güvenliğini çok önemsemedik kısacası.

Bu proje, **DenizBank İlerisi Gençlik Bootcamp Programı** kapsamında geliştirilmiş bir Wear OS (akıllı saat) uygulaması ve bu uygulamaya hizmet veren backend API'sini içermektedir. Proje, kullanıcılara temel bankacılık işlemlerini akıllı saatleri üzerinden hızlı ve kolay bir şekilde yapma imkanı sunmayı amaçlamaktadır.

## Proje Yapısı

Bu repository, iki ana bölümden oluşan bir monorepo yapısındadır:

-   **`/watchapp`**: Android Wear OS için geliştirilmiş frontend (istemci) uygulamasıdır. Kullanıcı arayüzü, **XML tabanlı layout'lar** kullanılarak tasarlanmıştır ve mantık katmanı **Kotlin** ile yazılmıştır.
-   **`/watchapp-api/watchapp`**: Uygulamaya veri sağlayan backend (sunucu) servisidir. **Java** ve **Spring Boot** teknolojileri kullanılarak geliştirilmiştir.

## Teknolojiler

### Backend (watchapp-api)
-   **Dil:** Java 17+
-   **Framework:** Spring Boot 3
-   **Veri Erişimi:** Spring Data JPA
-   **Veritabanı:** PostgreSQL (Aiven üzerinde cloud veritabanı)
-   **Harici API'ler:**
    -   Finansal piyasa verileri için **Financial Modeling Prep (FMP)**
    -   Kripto para verileri için **CoinGecko**
    -   Randevu onayı için SMS gönderimi **Twilio**
-   **API Dokümantasyonu:** Swagger UI

### Frontend (watchapp)
-   **Platform:** Android - Wear OS
-   **Dil:** Kotlin
-   **Mimari:** MVVM (Model-View-ViewModel)
-   **Arayüz (UI):** XML tabanlı Layouts
-   **Asenkron İşlemler:** Kotlin Coroutines & StateFlow
-   **Ağ (Networking):** Retrofit & OkHttp
-   **JSON İşleme:** Gson
-   **Görsel Bileşenler:** WearableRecyclerView, CardView, ConstraintLayout

## Özellikler

Uygulama, aşağıdaki temel bankacılık fonksiyonlarını desteklemektedir:

-   **Hesap Bilgileri:** Kullanıcının IBAN ve bakiye bilgilerini görüntülemesi.
-   **Piyasa Verileri:** Anlık Hisse Senedi, Döviz Kuru ve Kripto Para (Bitcoin, Ethereum) verilerini listeleme.
-   **IBAN Yönetimi:** Yeni IBAN kaydetme, mevcut IBAN'ları listeleme ve silme.
-   **Döviz Al/Sat:** Basit bir arayüz üzerinden döviz alım/satım işlemlerini simüle etme.
-   **Randevu Sistemi:** Seçilen ATM veya şube için randevu talebi oluşturma ve bu talebin SMS ile kullanıcıya bildirilmesi.
-   **Destek:** En yakın ATM'leri listeleme ve çağrı merkezi bilgilerine erişim.
-   **Diğer İşlemler:** Fatura ödeme, para çekme talebi ve QR kod ile ödeme gibi işlemlerin simülasyonu.

## Kurulum ve Çalıştırma

### Backend
1.  `watchapp-api/watchapp` dizinine gidin.
2.  `application.yml` dosyasındaki veritabanı, FMP, CoinGecko ve Twilio API anahtarlarını kendi bilgilerinizle güncelleyin.
3.  Projeyi bir IDE (IntelliJ IDEA, Eclipse vb.) üzerinden çalıştırın. İsterseniz docker ile de çalıştırabilirsiniz. Sunucu varsayılan olarak `localhost:8080` portunda ayağa kalkacaktır.

### Frontend
1.  `watchapp` dizinini Android Studio ile açın.
2.  `presentation/data/remote/RetrofitClient.kt` dosyasındaki `BASE_URL`'i, backend'i çalıştırdığınız bilgisayarın yerel IP adresine göre güncelleyin (örn: `http://10.0.2.2:8080/`).
3.  Uygulamayı bir Wear OS emülatörü veya fiziksel bir akıllı saat üzerinde çalıştırın.

---
### Ekip ve Katkıda Bulunanlar

Bu proje, DenizBank İlerisi Gençlik Bootcamp'i kapsamında, her biri kendi alanında değerli katkılar sunan dinamik bir ekip tarafından hayata geçirilmiştir.

*   **PM & System Architect (API & Services)**
    *   **Arhan Konuk** - Projenin genel teknik vizyonunu ve yönetimini üstlenmiştir. Backend (Java, Spring Boot) ve frontend (Kotlin, Android) arasındaki tüm API katmanlarını tasarlamış, geliştirmiş ve sistemlerin uçtan uca entegrasyonunu sağlamıştır.

*   **UI/UX & Kotlin Developer**
    *   **Salih Karakuş** - Uygulamanın hem kullanıcı arayüzü (UI/UX) tasarım süreçlerine aktif olarak katkıda bulunmuş, hem de bu tasarımları Kotlin ve XML kullanarak çalışan, fonksiyonel bir saate dönüştürmüştür.

*   **UI/UX Designer**
    *   **Raziyenur Yanık** - Uygulamanın Figma üzerindeki görsel kimliğini ve temel kullanıcı deneyimi akışlarını tasarlamıştır. Sezgisel ve modern bir arayüz yaratarak projenin estetik vizyonunu belirlemiştir.

*   **Business Analyst**
    *   **Selin İnan** - Proje gereksinimlerinin analiz edilmesi, iş akışlarının tanımlanması ve teknik ekibin ihtiyaç duyduğu fonksiyonel şartnamelerin oluşturulmasında kilit rol oynamıştır.

*   **Product and Marketing Strategist**
    *   **Nurcan Ercan** - Projenin pazar analizini, hedef kitleye uygun özelliklerin belirlenmesini ve ürünün pazarlama stratejilerini yönetmiştir. Projenin iş hedeflerine ulaşmasını sağlamıştır.

*   **Scrum Master & Team Coordinator**
    *   **Yusuf Ali Mertyürek** - Ekip içi iletişimi ve senkronizasyonu güçlendirerek projenin çevik (agile) bir metodolojiyle ilerlemesini sağlamıştır. Proje boyunca pozitif bir çalışma ortamı yaratmış, takım ruhunu ve motivasyonu en üst düzeyde tutmuştur.

# Malzeme-Teknik-Form Matrisi Uygulaması

Bu uygulama, Excel dosyasındaki malzeme-teknik-form matrisini interaktif bir web arayüzünde görüntüler.

## Gereksinimler

- Java 17 veya üstü
- Maven
- Spring Boot 3.x
- Apache POI (Excel işlemleri için)

## Kurulum

1. Projeyi klonlayın:
   ```bash
   git clone [proje-url]
   cd materialV1
   ```

2. Maven bağımlılıklarını yükleyin:
   ```bash
   mvn clean install
   ```

3. Uygulamayı başlatın:
   ```bash
   mvn spring-boot:run
   ```

## Kullanım

1. Uygulama başladıktan sonra tarayıcınızda `http://localhost:8080` adresine gidin
2. Ana sayfada üç seçenek göreceksiniz:
   - Malzeme ile Başla
   - Teknik ile Başla
   - Form ile Başla
3. Başlangıç seçiminizi yapın
4. Seçtiğiniz kategoriye göre ilgili öğeler listelenecektir
5. Bir öğe seçtiğinizde, diğer kategorilerdeki uyumlu öğeler otomatik olarak filtrelenecektir
6. Her öğenin yanındaki bilgi (ℹ️) butonuna tıklayarak detaylı bilgi alabilirsiniz

## Excel Dosyaları

Uygulama aşağıdaki Excel dosyalarını kullanır:

1. `M-T_F Matrisi.xlsx`: Ana matris dosyası
2. `01 Malzeme listesi.xlsx`: Malzeme detayları
3. `02 Teknik listesi.xlsx`: Teknik detayları
4. `03 Form listesi.xlsx`: Form detayları

Bu dosyalar `src/main/resources` klasöründe bulunmalıdır.

## İkon Dosyaları

İkon dosyaları aşağıdaki klasörlerde bulunmalıdır:

- `src/main/resources/static/icons/Malzeme icon/`
- `src/main/resources/static/icons/Teknikler icon/`
- `src/main/resources/static/icons/Form icon/`

Her ikon dosyası için:
- JPG/jpg veya PNG/png formatında olabilir
- Dosya adı formatı: `[Kod]_[İsim].[uzantı]` (örn: `M01_Seramik.jpg`)
- Form ikonları için klasör yapısı: `[Kod] [İsim]/[Kod].jpg` (örn: `F01 Paralel Ekstrüzyon Doğrusal/F01.jpg`)

## Özellikler

- Malzeme, teknik ve formlar arasındaki ilişkileri görselleştirme
- İnteraktif filtreleme ve seçim
- Detaylı bilgi görüntüleme
- Excel dosyası yükleme desteği
- Responsive tasarım
- Türkçe karakter desteği 
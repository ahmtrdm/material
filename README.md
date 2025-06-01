# Malzeme-Teknik-Form Matrisi Uygulaması

Bu uygulama, Excel dosyasındaki malzeme-teknik-form matrisini interaktif bir web arayüzünde görüntüler.

## Kurulum

### Backend (Python/Flask)

1. Python 3.7 veya üstü sürümün yüklü olduğundan emin olun
2. Backend klasörüne gidin:
   ```bash
   cd backend
   ```
3. Gerekli Python paketlerini yükleyin:
   ```bash
   pip install -r requirements.txt
   ```
4. Flask uygulamasını başlatın:
   ```bash
   python app/app.py
   ```

### Frontend (React)

1. Node.js'in yüklü olduğundan emin olun
2. Frontend klasörüne gidin:
   ```bash
   cd frontend
   ```
3. Gerekli npm paketlerini yükleyin:
   ```bash
   npm install
   ```
4. React uygulamasını başlatın:
   ```bash
   npm start
   ```

## Kullanım

1. Backend ve frontend uygulamalarını ayrı terminal pencerelerinde çalıştırın
2. Tarayıcınızda `http://localhost:3000` adresine gidin
3. Malzeme listesinden bir malzeme seçin
4. Seçilen malzemeye uygun teknikler ve formlar otomatik olarak filtrelenecektir
5. Tekniklerden birini seçerek ilgili formları daha da filtreleyebilirsiniz 
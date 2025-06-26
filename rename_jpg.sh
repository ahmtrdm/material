#!/bin/bash

# static klasörü altındaki tüm JPG uzantılı dosyaları bul ve jpg'ye çevir
find src/main/resources/static -type f -name "*.JPG" | while read file; do
    # Dosya adını al ve uzantıyı küçük harfe çevir
    new_name="${file%.JPG}.jpg"
    # Dosyayı yeniden adlandır
    mv "$file" "$new_name"
    echo "Renamed: $file -> $new_name"
done 
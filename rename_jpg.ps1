# static klasörü altındaki tüm JPG uzantılı dosyaları bul
Get-ChildItem -Path "src/main/resources/static" -Recurse -Filter "*.JPG" | ForEach-Object {
    # Yeni dosya adını oluştur (uzantıyı küçük harfe çevir)
    $newName = $_.FullName -replace '\.JPG$', '.jpg'
    # Dosyayı yeniden adlandır
    Rename-Item -Path $_.FullName -NewName $newName
    Write-Host "Renamed: $($_.FullName) -> $newName"
} 
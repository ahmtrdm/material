import pandas as pd

# Excel dosyasını oku
df = pd.read_excel('01 Malzeme listesi.xlsx')

# İlk 5 satırı göster
print("\nİlk 5 satır:")
print(df.head())

# Sütun isimlerini göster
print("\nSütun isimleri:")
print(df.columns.tolist())

# Veri tiplerini göster
print("\nVeri tipleri:")
print(df.dtypes)

# Null değerleri kontrol et
print("\nNull değerler:")
print(df.isnull().sum())

# Her sütun için unique değerleri göster
print("\nUnique değerler:")
for column in df.columns:
    print(f"\n{column}:")
    print(df[column].unique()) 
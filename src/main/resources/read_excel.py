import pandas as pd
import os

def read_excel_file(filename):
    print(f"\n=== Reading {filename} ===")
    try:
        df = pd.read_excel(filename)

        # Print all column names
        print("\nAll columns:")
        for i, col in enumerate(df.columns):
            print(f"{i}: {col}")

        # Print first 5 rows of each column
        print("\nFirst 5 rows of each column:")
        for col in df.columns:
            print(f"\n{col}:")
            print(df[col].head())

    except Exception as e:
        print(f"Error reading {filename}: {str(e)}")

# Read all three Excel files
read_excel_file('01 Malzeme listesi.xlsx')
read_excel_file('02 Teknik listesi.xlsx')
read_excel_file('03 Form listesi.xlsx') 
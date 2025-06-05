#!/bin/bash

# Malzeme ikonlarını yeniden adlandır
cd src/main/resources/static/icons/Malzeme\ icon/
for file in *_*.{jpg,JPG,png,PNG}; do
    if [ -f "$file" ]; then
        new_name=$(echo "$file" | sed -E 's/([A-Z][0-9]+)_.*\.(jpg|JPG|png|PNG)/\1.\2/')
        mv "$file" "$new_name"
        echo "Renamed: $file -> $new_name"
    fi
done

# Teknik ikonlarını yeniden adlandır
cd ../Teknikler\ icon/
for file in *_*.{jpg,JPG,png,PNG}; do
    if [ -f "$file" ]; then
        new_name=$(echo "$file" | sed -E 's/([A-Z][0-9]+)_.*\.(jpg|JPG|png|PNG)/\1.\2/')
        mv "$file" "$new_name"
        echo "Renamed: $file -> $new_name"
    fi
done

# Form klasörlerini ve ikonlarını yeniden adlandır
cd ../Form\ icon/
for dir in F*; do
    if [ -d "$dir" ]; then
        new_dir=$(echo "$dir" | sed -E 's/(F[0-9]+).*/\1/')
        if [ "$dir" != "$new_dir" ]; then
            mv "$dir" "$new_dir"
            echo "Renamed directory: $dir -> $new_dir"
        fi
    fi
done

# Form ikonlarını yeniden adlandır
for dir in F*; do
    if [ -d "$dir" ]; then
        cd "$dir"
        for file in *.jpg *.JPG *.png *.PNG; do
            if [ -f "$file" ]; then
                new_name=$(echo "$file" | sed -E 's/(F[0-9]+).*\.(jpg|JPG|png|PNG)/\1.\2/')
                if [ "$file" != "$new_name" ]; then
                    mv "$file" "$new_name"
                    echo "Renamed: $dir/$file -> $dir/$new_name"
                fi
            fi
        done
        cd ..
    fi
done 
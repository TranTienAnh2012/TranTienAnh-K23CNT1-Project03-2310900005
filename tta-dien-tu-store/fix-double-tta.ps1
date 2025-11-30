# Fix double Tta prefix issues in refactored files

$files = Get-ChildItem -Path "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java" -Recurse -Filter "*.java"

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    $modified = $false
    
    # Fix double Tta prefix
    if ($content -match "ttaTta") {
        $content = $content -replace "ttaTta", "tta"
        $modified = $true
    }
    
    # Fix TtaTta pattern
    if ($content -match "TtaTta") {
        $content = $content -replace "TtaTta", "Tta"
        $modified = $true
    }
    
    if ($modified) {
        $content | Set-Content $file.FullName -NoNewline
        Write-Host "Fixed: $($file.FullName)"
    }
}

Write-Host "Fix complete!"

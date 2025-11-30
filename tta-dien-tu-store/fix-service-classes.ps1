# Fix class name issues in service files

$files = @(
    "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\admin\service\TtaAdminDonHangService.java",
    "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\admin\service\TtaAdminQuanTriVienService.java",
    "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\admin\service\TtaAdminSanPhamService.java"
)

foreach ($file in $files) {
    if (Test-Path $file) {
        $content = Get-Content $file -Raw
        
        # Fix class name with extra 's' prefix
        $content = $content -replace "public class sTtaAdmin", "public class TtaAdmin"
        
        # Write back
        $content | Set-Content $file -NoNewline
        Write-Host "Fixed: $file"
    }
}

Write-Host "All service files fixed!"

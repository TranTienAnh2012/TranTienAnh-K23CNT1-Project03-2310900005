# Script to refactor controller files by adding Tta prefix to class names and updating imports

$controllers = @(
    @{
        Old = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\admin\controller\AdminSanPhamController.java"
        New = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\admin\controller\TtaAdminSanPhamController.java"
        OldClass = "AdminSanPhamController"
        NewClass = "TtaAdminSanPhamController"
    },
    @{
        Old = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\admin\controller\AdminDonHangController.java"
        New = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\admin\controller\TtaAdminDonHangController.java"
        OldClass = "AdminDonHangController"
        NewClass = "TtaAdminDonHangController"
    },
    @{
        Old = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\admin\controller\AdminQuanTriVienController.java"
        New = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\admin\controller\TtaAdminQuanTriVienController.java"
        OldClass = "AdminQuanTriVienController"
        NewClass = "TtaAdminQuanTriVienController"
    }
)

foreach ($ctrl in $controllers) {
    if (Test-Path $ctrl.Old) {
        $content = Get-Content $ctrl.Old -Raw
        
        # Update class name
        $content = $content -replace "public class $($ctrl.OldClass)", "public class $($ctrl.NewClass)"
        
        # Update service imports and references
        $content = $content -replace "AdminSanPhamService", "TtaAdminSanPhamService"
        $content = $content -replace "AdminDonHangService", "TtaAdminDonHangService"
        $content = $content -replace "AdminQuanTriVienService", "TtaAdminQuanTriVienService"
        $content = $content -replace "AdminDanhMucService", "TtaAdminDanhMucService"
        
        # Update entity imports
        $content = $content -replace "import com\.tta\.dientu\.store\.entity\.SanPham;", "import com.tta.dientu.store.entity.TtaSanPham;"
        $content = $content -replace "import com\.tta\.dientu\.store\.entity\.DonHang;", "import com.tta.dientu.store.entity.TtaDonHang;"
        $content = $content -replace "import com\.tta\.dientu\.store\.entity\.DanhMuc;", "import com.tta.dientu.store.entity.TtaDanhMuc;"
        $content = $content -replace "import com\.tta\.dientu\.store\.entity\.QuanTriVien;", "import com.tta.dientu.store.entity.TtaQuanTriVien;"
        $content = $content -replace "import com\.tta\.dientu\.store\.entity\.ChiTietDonHang;", "import com.tta.dientu.store.entity.TtaChiTietDonHang;"
        
        # Update type references in code
        $content = $content -replace "\bSanPham\b", "TtaSanPham"
        $content = $content -replace "\bDonHang\b", "TtaDonHang"
        $content = $content -replace "\bDanhMuc\b", "TtaDanhMuc"
        $content = $content -replace "\bQuanTriVien\b", "TtaQuanTriVien"
        $content = $content -replace "\bChiTietDonHang\b", "TtaChiTietDonHang"
        
        # Write to new file
        $content | Set-Content $ctrl.New -NoNewline
        Write-Host "Created: $($ctrl.New)"
    }
}

Write-Host "Refactoring complete!"

# Script to refactor user controller files

$controllers = @(
    @{
        Old = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\user\controller\AccountController.java"
        New = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\user\controller\TtaAccountController.java"
        OldClass = "AccountController"
        NewClass = "TtaAccountController"
    },
    @{
        Old = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\user\controller\CartController.java"
        New = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\user\controller\TtaCartController.java"
        OldClass = "CartController"
        NewClass = "TtaCartController"
    },
    @{
        Old = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\user\controller\LoginRedirectController.java"
        New = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\user\controller\TtaLoginRedirectController.java"
        OldClass = "LoginRedirectController"
        NewClass = "TtaLoginRedirectController"
    },
    @{
        Old = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\user\controller\UserHomeController.java"
        New = "d:\TranTienAnh-K23CNT1-Project03-2310900005\tta-dien-tu-store\src\main\java\com\tta\dientu\store\areas\user\controller\TtaUserHomeController.java"
        OldClass = "UserHomeController"
        NewClass = "TtaUserHomeController"
    }
)

foreach ($ctrl in $controllers) {
    if (Test-Path $ctrl.Old) {
        $content = Get-Content $ctrl.Old -Raw
        
        # Update class name
        $content = $content -replace "public class $($ctrl.OldClass)", "public class $($ctrl.NewClass)"
        
        # Update service imports and references
        $content = $content -replace "\bCartService\b", "TtaCartService"
        $content = $content -replace "CustomUserDetails", "TtaCustomUserDetails"
        
        # Update entity imports
        $content = $content -replace "import com\.tta\.dientu\.store\.entity\.SanPham;", "import com.tta.dientu.store.entity.TtaSanPham;"
        $content = $content -replace "import com\.tta\.dientu\.store\.entity\.DanhMuc;", "import com.tta.dientu.store.entity.TtaDanhMuc;"
        $content = $content -replace "import com\.tta\.dientu\.store\.entity\.QuanTriVien;", "import com.tta.dientu.store.entity.TtaQuanTriVien;"
        
        # Update repository imports
        $content = $content -replace "import com\.tta\.dientu\.store\.repository\.SanPhamRepository;", "import com.tta.dientu.store.repository.TtaSanPhamRepository;"
        $content = $content -replace "import com\.tta\.dientu\.store\.repository\.DanhMucRepository;", "import com.tta.dientu.store.repository.TtaDanhMucRepository;"
        $content = $content -replace "import com\.tta\.dientu\.store\.repository\.QuanTriVienRepository;", "import com.tta.dientu.store.repository.TtaQuanTriVienRepository;"
        
        # Update type references in code
        $content = $content -replace "\bSanPham\b", "TtaSanPham"
        $content = $content -replace "\bDanhMuc\b", "TtaDanhMuc"
        $content = $content -replace "\bQuanTriVien\b", "TtaQuanTriVien"
        $content = $content -replace "SanPhamRepository", "TtaSanPhamRepository"
        $content = $content -replace "DanhMucRepository", "TtaDanhMucRepository"
        $content = $content -replace "QuanTriVienRepository", "TtaQuanTriVienRepository"
        
        # Write to new file
        $content | Set-Content $ctrl.New -NoNewline
        Write-Host "Created: $($ctrl.New)"
    }
}

Write-Host "User controllers refactoring complete!"

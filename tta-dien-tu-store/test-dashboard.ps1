# Script để test tính năng "Xem thêm" trên dashboard
# Mở browser và chụp screenshot

# Mở trang dashboard
Start-Process "chrome.exe" "http://localhost:8080/user/dashboard"

Write-Host "✅ Đã mở trang dashboard trong Chrome"
Write-Host "📍 URL: http://localhost:8080/user/dashboard"
Write-Host ""
Write-Host "Vui lòng kiểm tra:"
Write-Host "1. Có 12 sản phẩm hiển thị (2 hàng x 6 cột)"
Write-Host "2. Có nút 'Xem thêm sản phẩm' ở dưới"
Write-Host "3. Click nút để xem tất cả sản phẩm"
Write-Host "4. Nút sẽ biến mất sau khi click"

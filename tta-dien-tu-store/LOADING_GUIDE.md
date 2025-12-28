# Hướng Dẫn Sử Dụng Loading Overlay

## 📋 Tổng Quan

Loading overlay đã được tích hợp vào toàn bộ user area của TTA Store. Nó sẽ tự động hiển thị khi:
- Trang web đang tải
- Người dùng click vào link nội bộ
- Form được submit

## 🎨 Thiết Kế

Loading overlay có:
- **Spinner đa màu**: 4 vòng tròn xoay với màu gradient (vàng → cam → đỏ)
- **Backdrop blur**: Nền mờ với hiệu ứng blur
- **Animation mượt mà**: Fade in/out với transition
- **Text động**: Chữ "Đang tải..." với hiệu ứng pulse

## 🔧 Sử Dụng Thủ Công

### Hiển thị loading
```javascript
showLoading(); // Hiển thị với text mặc định "Đang tải..."
showLoading('Đang xử lý thanh toán...'); // Hiển thị với text tùy chỉnh
```

### Ẩn loading
```javascript
hideLoading();
```

### Ví dụ với AJAX request
```javascript
// Hiển thị loading trước khi gửi request
showLoading('Đang tải dữ liệu...');

fetch('/api/data')
    .then(response => response.json())
    .then(data => {
        // Xử lý dữ liệu
        console.log(data);
    })
    .finally(() => {
        // Ẩn loading sau khi hoàn thành
        hideLoading();
    });
```

## 🚫 Vô Hiệu Hóa Loading Tự Động

### Cho link cụ thể
Thêm attribute `data-no-loading` vào thẻ `<a>`:
```html
<a href="/some-page" data-no-loading>Link không hiển thị loading</a>
```

### Cho form cụ thể
Thêm attribute `data-no-loading` vào thẻ `<form>`:
```html
<form action="/submit" method="post" data-no-loading>
    <!-- Form content -->
</form>
```

## 🎯 Các Trường Hợp Sử Dụng

### 1. Khi nhận voucher
```javascript
document.querySelector('.btn-save').addEventListener('click', function() {
    showLoading('Đang nhận voucher...');
    // Submit form
});
```

### 2. Khi thanh toán
```javascript
document.querySelector('#checkout-form').addEventListener('submit', function() {
    showLoading('Đang xử lý thanh toán...');
});
```

### 3. Khi tải sản phẩm
```javascript
function loadProducts() {
    showLoading('Đang tải sản phẩm...');
    
    fetch('/api/products')
        .then(response => response.json())
        .then(products => {
            renderProducts(products);
        })
        .finally(() => {
            hideLoading();
        });
}
```

## 🎨 Tùy Chỉnh

### Thay đổi màu spinner
Chỉnh sửa trong CSS:
```css
.tta-spinner-ring:nth-child(1) {
    border-top-color: #your-color;
}
```

### Thay đổi thời gian animation
```css
.tta-spinner-ring {
    animation: tta-spin 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
    /* Thay đổi 1.2s thành giá trị khác */
}
```

### Thay đổi nền overlay
```css
.tta-loading-overlay {
    background: rgba(255, 255, 255, 0.95); /* Nền trắng 95% opacity */
    backdrop-filter: blur(5px); /* Blur 5px */
}
```

## 📱 Responsive

Loading overlay tự động responsive và hoạt động tốt trên mọi thiết bị:
- Desktop
- Tablet
- Mobile

## ⚡ Performance

- **Z-index**: 99999 (cao nhất để luôn hiển thị trên cùng)
- **GPU Acceleration**: Sử dụng transform và opacity cho animation mượt
- **Lightweight**: Không sử dụng thư viện bên ngoài
- **Auto cleanup**: Tự động ẩn khi trang load xong

## 🐛 Troubleshooting

### Loading không ẩn
```javascript
// Force ẩn loading
hideLoading();
```

### Loading hiển thị quá lâu
Kiểm tra xem có lỗi JavaScript nào đang block không:
```javascript
// Thêm timeout để tự động ẩn sau 10 giây
setTimeout(() => {
    hideLoading();
}, 10000);
```

## 📝 Notes

- Loading tự động ẩn sau khi trang load xong (300ms delay)
- Không hiển thị cho link external hoặc link có `target="_blank"`
- Không hiển thị cho anchor links (href="#...")
- Tự động restore khi dùng nút back/forward của browser

# Hướng dẫn gọi Fragment vào Layout

## Cấu trúc Fragment

```
templates/
└── layout/
    ├── admin-layout.html          # Layout admin (gọi fragments)
    ├── user-layout.html            # Layout user (gọi fragments)
    └── fragments/
        ├── admin-nav.html          # Navigation admin
        ├── user-nav.html           # Navigation user
        ├── user-footer.html        # Footer user
        └── admin-footer.html       # Footer admin (optional)
```

## Cách gọi Fragment

### 1. Admin Layout - Gọi Navigation

**File:** `layout/admin-layout.html`

```html
<aside class="admin-sidebar">
    <div class="sidebar-header">...</div>
    
    <!-- GỌI NAVIGATION FRAGMENT -->
    <th:block th:replace="~{layout/fragments/admin-nav :: admin-sidebar-nav}"></th:block>
</aside>
```

**Giải thích:**
- `th:replace` = Thay thế toàn bộ thẻ hiện tại bằng fragment
- `~{layout/fragments/admin-nav}` = Đường dẫn đến file fragment
- `:: admin-sidebar-nav` = Tên fragment cần lấy

### 2. User Layout - Gọi Navigation

**File:** `layout/user-layout.html`

```html
<nav class="navbar">
    <div class="container">
        <a class="navbar-brand">...</a>
        <button>...</button>
        
        <!-- GỌI NAVIGATION FRAGMENT -->
        <th:block th:replace="~{layout/fragments/user-nav :: user-header-nav}"></th:block>
    </div>
</nav>
```

### 3. User Layout - Gọi Footer

**File:** `layout/user-layout.html`

```html
<main>...</main>

<!-- GỌI FOOTER FRAGMENT -->
<th:block th:replace="~{layout/fragments/user-footer :: user-footer}"></th:block>
```

## Cú pháp gọi Fragment

### Cú pháp cơ bản:
```html
<th:block th:replace="~{đường-dẫn-file :: tên-fragment}"></th:block>
```

### Ví dụ cụ thể:

#### ✅ ĐÚNG:
```html
<!-- Gọi navigation admin -->
<th:block th:replace="~{layout/fragments/admin-nav :: admin-sidebar-nav}"></th:block>

<!-- Gọi navigation user -->
<th:block th:replace="~{layout/fragments/user-nav :: user-header-nav}"></th:block>

<!-- Gọi footer user -->
<th:block th:replace="~{layout/fragments/user-footer :: user-footer}"></th:block>
```

#### ❌ SAI:
```html
<!-- SAI: Thiếu ~{} -->
<th:block th:replace="layout/fragments/admin-nav :: admin-sidebar-nav"></th:block>

<!-- SAI: Thiếu :: -->
<th:block th:replace="~{layout/fragments/admin-nav}"></th:block>

<!-- SAI: Sai đường dẫn -->
<th:block th:replace="~{fragments/admin-nav :: admin-sidebar-nav}"></th:block>
```

## Các Fragment hiện có

### 1. Admin Navigation
- **File:** `layout/fragments/admin-nav.html`
- **Fragment name:** `admin-sidebar-nav`
- **Sử dụng trong:** `admin-layout.html` (dòng 26)
- **Chứa:** Menu sidebar (Dashboard, Sản phẩm, Danh mục, Đơn hàng, Hồ sơ, Đăng xuất)

### 2. User Navigation
- **File:** `layout/fragments/user-nav.html`
- **Fragment name:** `user-header-nav`
- **Sử dụng trong:** `user-layout.html` (dòng 30)
- **Chứa:** Menu header (Trang chủ, Sản phẩm, Giỏ hàng, Dropdown User)

### 3. User Footer
- **File:** `layout/fragments/user-footer.html`
- **Fragment name:** `user-footer`
- **Sử dụng trong:** `user-layout.html` (dòng 56)
- **Chứa:** Footer với thông tin liên hệ, social media, copyright

### 4. Admin Footer (Optional)
- **File:** `layout/fragments/admin-footer.html`
- **Fragment name:** `admin-footer`
- **Sử dụng trong:** Chưa được gọi (có thể thêm vào admin-layout nếu cần)

## Cách thêm Fragment mới

### Bước 1: Tạo file fragment
**File:** `layout/fragments/my-fragment.html`
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div th:fragment="my-fragment-name">
        <!-- Nội dung fragment -->
        <p>Hello from fragment!</p>
    </div>
</body>
</html>
```

### Bước 2: Gọi fragment trong layout
**File:** `layout/admin-layout.html` hoặc `layout/user-layout.html`
```html
<th:block th:replace="~{layout/fragments/my-fragment :: my-fragment-name}"></th:block>
```

## Lưu ý quan trọng

1. **Đường dẫn:** Luôn bắt đầu từ `layout/fragments/`
2. **Tên fragment:** Phải khớp với `th:fragment` trong file fragment
3. **Cú pháp:** Phải có `~{}` và `::`
4. **File fragment:** Phải có cấu trúc HTML đầy đủ (DOCTYPE, html, body)

## Ví dụ hoàn chỉnh

### Admin Layout với tất cả fragments:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div class="admin-wrapper">
        <aside class="admin-sidebar">
            <!-- Gọi navigation -->
            <th:block th:replace="~{layout/fragments/admin-nav :: admin-sidebar-nav}"></th:block>
        </aside>
        
        <div class="admin-main">
            <!-- Content -->
            <th:block th:fragment="content">...</th:block>
        </div>
    </div>
    
    <!-- Có thể thêm footer nếu cần -->
    <th:block th:replace="~{layout/fragments/admin-footer :: admin-footer}"></th:block>
</body>
</html>
```

### User Layout với tất cả fragments:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div class="user-wrapper">
        <header>
            <nav class="navbar">
                <!-- Gọi navigation -->
                <th:block th:replace="~{layout/fragments/user-nav :: user-header-nav}"></th:block>
            </nav>
        </header>
        
        <main>
            <!-- Content -->
            <th:block th:fragment="content">...</th:block>
        </main>
        
        <!-- Gọi footer -->
        <th:block th:replace="~{layout/fragments/user-footer :: user-footer}"></th:block>
    </div>
</body>
</html>
```

## Tóm tắt

✅ **Admin Layout gọi:**
- `admin-nav :: admin-sidebar-nav` (dòng 26)

✅ **User Layout gọi:**
- `user-nav :: user-header-nav` (dòng 30)
- `user-footer :: user-footer` (dòng 56)

🎯 **Lợi ích:**
- Code gọn, dễ bảo trì
- Tách biệt rõ ràng
- Dễ thêm/sửa/xóa menu

Hệ thống sử dụng Bootstrap Grid (tổng 12 cột). Cách tính số sản phẩm = 12 / số cột.

Hiển thị số sản phẩm trên hàng
Giải thích hiện tại:

col-6: Mobile hiển thị 2 sản phẩm/hàng (12/6 = 2).
col-md-4: Tablet hiển thị 3 sản phẩm/hàng (12/4 = 3).
col-lg-2: Desktop hiển thị 6 sản phẩm/hàng (12/2 = 6).
Cách thay đổi: Bạn muốn hiển thị bao nhiêu sản phẩm trên 1 hàng ở Desktop?

4 sản phẩm/hàng: Đổi col-lg-2 thành col-lg-3 (12/3 = 4).
3 sản phẩm/hàng: Đổi col-lg-2 thành col-lg-4.
5 sản phẩm/hàng: Bootstrap chuẩn không hỗ trợ chia 5 (12 không chia hết cho 5), nhưng có thể dùng CSS custom width: 20%.
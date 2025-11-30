# Hướng dẫn sử dụng Layout

## Cấu trúc Layout

### 1. Admin Layout
- **File**: `layout/admin-layout.html`
- **Sử dụng cho**: Tất cả các trang admin (`/admin/**`)
- **Đặc điểm**: 
  - Sidebar cố định bên trái
  - Topbar với thông tin user
  - CSS riêng: `admin.css`

### 2. User Layout
- **File**: `layout/user-layout.html`
- **Sử dụng cho**: Tất cả các trang user (`/user/**`)
- **Đặc điểm**:
  - Header với navigation
  - Footer
  - CSS riêng: `user.css`

## Cách sử dụng

### Cho Admin Views (CRUD Admin)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi"
      th:replace="~{layout/admin-layout}">
<head>
    <title th:text="${pageTitle}">Admin Page - TTA Store</title>
</head>
<body>
    <div th:fragment="content">
        <!-- Nội dung trang admin của bạn ở đây -->
        <h1>Quản lý sản phẩm</h1>
        <!-- Form CRUD, table, etc. -->
    </div>
</body>
</html>
```

**Lưu ý**: 
- View phải nằm trong thư mục `areas/admin/`
- Sử dụng `th:replace="~{layout/admin-layout}"` để extend layout
- Nội dung đặt trong `<div th:fragment="content">`

### Cho User Views (Trang người dùng)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi"
      th:replace="~{layout/user-layout}">
<head>
    <title th:text="${pageTitle}">User Page - TTA Store</title>
</head>
<body>
    <div th:fragment="content">
        <!-- Nội dung trang user của bạn ở đây -->
        <h1>Danh sách sản phẩm</h1>
        <!-- Product list, cart, etc. -->
    </div>
</body>
</html>
```

**Lưu ý**:
- View phải nằm trong thư mục `areas/user/`
- Sử dụng `th:replace="~{layout/user-layout}"` để extend layout
- Nội dung đặt trong `<div th:fragment="content">`

## Cấu trúc thư mục

```
templates/
├── layout/
│   ├── admin-layout.html    # Layout cho admin
│   ├── user-layout.html      # Layout cho user
│   └── base.html             # Layout cơ bản (nếu cần)
├── areas/
│   ├── admin/                # Tất cả views admin
│   │   ├── home/
│   │   │   └── dashboard.html
│   │   ├── san-pham/         # CRUD sản phẩm
│   │   ├── danh-muc/         # CRUD danh mục
│   │   └── don-hang/         # CRUD đơn hàng
│   └── user/                 # Tất cả views user
│       ├── home/
│       │   └── dashboard.html
│       ├── san-pham/          # Xem sản phẩm
│       └── gio-hang/          # Giỏ hàng
├── account/                   # Views đăng nhập, đăng ký
└── error/                     # Views lỗi
```

## Ví dụ Controller

### Admin Controller
```java
@Controller
@RequestMapping("/admin")
public class AdminSanPhamController {
    
    @GetMapping("/san-pham")
    public String listSanPham(Model model) {
        model.addAttribute("pageTitle", "Quản lý sản phẩm");
        model.addAttribute("activePage", "san-pham");
        return "areas/admin/san-pham/list";  // Sử dụng layout admin
    }
}
```

### User Controller
```java
@Controller
@RequestMapping("/user")
public class UserSanPhamController {
    
    @GetMapping("/san-pham")
    public String listSanPham(Model model) {
        model.addAttribute("pageTitle", "Sản phẩm");
        model.addAttribute("activePage", "san-pham");
        return "areas/user/san-pham/list";  // Sử dụng layout user
    }
}
```

## Tách biệt hoàn toàn

- **Admin views** chỉ sử dụng `admin-layout.html` → Không bị lẫn với user
- **User views** chỉ sử dụng `user-layout.html` → Không bị lẫn với admin
- Mỗi layout có CSS riêng (`admin.css` và `user.css`)
- Cấu trúc thư mục tách biệt rõ ràng (`areas/admin/` và `areas/user/`)


# Yêu cầu đầu tiên từ khách hàng

Xin chào, bên tôi đang muốn xây một **ứng dụng quản lý thư viện cho trường đại học**.

Hiện tại thư viện đang quản lý bằng Excel nên rất khó kiểm soát.

Tôi muốn một hệ thống web có các chức năng chính:

### 1. Sinh viên

* đăng nhập bằng mã sinh viên
* tìm kiếm sách
* xem sách còn hay đã được mượn
* mượn sách
* trả sách
* xem lịch sử mượn

---

### 2. Thủ thư

* thêm / sửa / xóa sách
* quản lý sinh viên
* duyệt yêu cầu mượn
* xác nhận trả sách
* xem báo cáo sách quá hạn

---

### 3. Business rule

* mỗi sinh viên chỉ được mượn tối đa **3 cuốn**
* thời hạn mượn là **14 ngày**
* quá hạn thì tính phí **5.000 VND / ngày**
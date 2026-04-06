GET /api/books -> dùng query eg. ?title=Clean code
POST /api/books -> đẩy thông tin sách bằng body data
GET /api/books/{id}
PATCH /api/books/{id}
DELETE /api/books/{id}

GET /api/borrow-records -> dùng query eg. ?user_id={id}
DELETE /api/borrow-records/{id}
POST /api/borrow-records -> dùng data body, tạo lượt mới
PATCH /api/borrow-records/{id}/return

GET /api/users
GET /api/users/{id}

POST /api/auth/login
POST /api/auth/logout
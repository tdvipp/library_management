GET /api/books
GET /api/books/{id}
POST /api/books -> đẩy thông tin sách bằng body data
PATCH /api/books/{id}
DELETE /api/books/{id}

GET /api/borrow-records
GET /api/borrow-records/{id}
POST /api/borrow-records -> dùng data body, tạo lượt mới
PATCH /api/borrow-records/{id}
PATCH /api/borrow-records/{id}/return
DELETE /api/borrow-records/{id}

GET /api/users
GET /api/users/{id}
POST /api/users
PATCH /api/users/{id}
DELETE /api/users/{id}

POST /api/auth/login
POST /api/auth/logout
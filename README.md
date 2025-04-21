
# 📚 OrderSystem API Documentation

This documentation outlines the RESTful API endpoints for the four main microservices: User Service, Menu Service, Shopping Cart Service, and Order Service. Each service is responsible for a key area in the overall system.

---

## 👤 User Service

### Base URL
```
/users
```

### Endpoints

#### 🔐 Register a User
- **POST** `/users/register`
- **Headers**: Optional `Authorization` (required only to register an ADMIN)
- **Body**:
```json
{
  "username": "adminuser",
  "email": "admin@example.com",
  "password": "secure123",
  "role": "ADMIN"
}
```

#### 🔑 Login
- **POST** `/users/login`
- **Body**:
```json
{
  "email": "admin@example.com",
  "password": "secure123"
}
```
- **Response**: Get JWT
```json
{
  "token": "<JWT>"
}
```

#### 🙋 Get Current User Info
- **GET** `/users/me`
- **Headers**: `Authorization: Bearer <JWT>`
- **Response**:
```json
{
  "username": "adminuser",
  "role": "ADMIN"
}
```

---

## 🍽 Menu Service

### Base URL
```
/menu
```

### Endpoints

#### 📃 Get All Menu Items
- **GET** `/menu/items`
- **Response**:
```json
[
  {
    "id": 6,
    "name": "Chicken Curry",
    "description": "Delicious spicy chicken curry",
    "price": 18.99,
    "category": "Main Course",
    "imageUrl": "http://localhost:8080/menu/images/1744569618192.jpg",
    "createTime": "2025-04-13T11:40:18",
    "updateTime": "2025-04-13T11:40:18"
  }
]
```

#### 🔍 Get Menu Item by ID
- **GET** `/menu/items/{id}`
- **Response**: Similar as "Get All Menu Items"

#### ➕ Add Menu Item (Admin Only)
- **POST** `/menu/items`
- **Headers**: `Content-Type: multipart/form-data`, `Authorization: Bearer <JWT>`
- **Form Data**:
  - `menuItem`: JSON string for `MenuItemDTO`
  - `file`: image (.jpg/.png)

#### ✏️ Update Menu Item (Admin Only)
- **PUT** `/menu/items/{id}`
- **Same format** as Add

#### ❌ Delete Menu Item (Admin Only)
- **DELETE** `/menu/items/{id}`

---

## 🛒 Shopping Cart Service

### Base URL
```
/cart
```

### Endpoints

#### ➕ Add Item to Cart
- **POST** `/cart/add`
- **Headers**: `Authorization: Bearer <JWT>`
- **Body**:
```json
{
  "item Id": 101,
  "quantity": 2
}
```

#### 🗑 Remove Item from Cart
- **DELETE** `/cart/remove/{itemID}`
- **Headers**: `Authorization: Bearer <JWT>`

#### 📦 Get Cart Items
- **GET** `/cart`
- **Headers**: `Authorization: Bearer <JWT>`
- **Response:** List<CartItem>

#### ❌ Clear Cart
- **DELETE** `/cart/clear`
- **Headers**: `Authorization: Bearer <JWT>`

---

## 📦 Order Service

### Base URL
```
/orders
```

### Endpoints

#### 📝 Create Order

- Create an order, request must have Authorization, get all items in shopping cart and place an order

- **POST** `/orders/create`
- **Headers**: `Authorization: Bearer <JWT>`

#### 💳 Pay for Order
- **PUT** `/orders/pay/{orderId}`
- **Authorization**: Only the order owner, based on JWT

#### ❌ Cancel Order
- **PUT** `/orders/cancel/{orderId}`
- **Authorization**: Only the order owner, based on JWT

#### 📜 Get User Orders
- **GET** `/orders/my`
- **Headers**: `Authorization: Bearer <JWT>`
- **Response**:
```json
[
  {
    "id": 1001,
    "status": "PAID",
    "createdAt": "2025-04-21T15:00:00Z"
  }
]
```

---

## 🔐 Authentication
- JWT tokens are required for protected endpoints.
- Include in the `Authorization` header as:
```
Authorization: Bearer <token>
```

---

## 📌 Swagger UI
**After service started:**

- Menu service:

  http://localhost:8080/swagger-ui/index.html

- User service: 

  http://localhost:8083/swagger-ui/index.html

- Shopping cart service: 

  http://localhost:8085/swagger-ui/index.html

- Order service:

  http://localhost:8086/swagger-ui/index.html

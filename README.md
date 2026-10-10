# Ecommerce Backend

A backend API for an online shop, built with Java and Spring Boot. It handles user accounts, products with sizes and colors, shopping cart, orders, discount codes and online payment. There is no frontend, you can test it with Postman or curl.

## Features

- **Accounts:** register, login, email verification, forgot and reset password, login with JWT, refresh token, lock account after 5 wrong passwords
- **Roles and permissions:** 4 roles (Customer, Staff, Manager, System Admin). Access rules are saved in the database, so you can change who can call which API without editing code
- **Products:** categories (with parent and child), product variants, stock per variant, price history, images, soft delete
- **Cart:** one cart per user, stock check when adding items
- **Orders:** checkout from cart, discount codes, order status history, cancel pending orders (stock goes back)
- **Payment:** VNPay, Momo and Stripe
- **Monitoring:** health check, Prometheus metrics, Grafana dashboard

## Tech stack

| Part | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4 (Web, Security, Data JPA, Validation) |
| Database | MySQL 8 with Flyway migrations |
| Cache | Redis |
| Login | JWT (jjwt), BCrypt |
| Monitoring | Spring Actuator, Prometheus, Grafana |
| Testing | JUnit 5, Mockito |
| Run | Docker Compose |

## Main APIs

| Group | Endpoints |
|---|---|
| Auth | `POST /api/auth/register`, `login`, `refresh`, `logout`, `forgot-password`, `reset-password`, `GET /api/auth/verify-email`, `GET /api/auth/me` |
| Products | `GET /api/products`, `GET /api/products/{id}`, `POST`, `PUT`, `DELETE` (admin) |
| Variants (SKU) | `GET` and `POST /api/products/{id}/skus`, `PUT` and `DELETE /api/products/skus/{skuId}` |
| Categories | `GET /api/categories`, `GET /api/categories/tree`, `POST`, `PUT`, `DELETE` (admin) |
| Addresses | `GET`, `POST /api/addresses`, `PUT`, `DELETE /api/addresses/{id}` |
| Cart | `GET /api/cart`, `POST /api/cart/items`, `PUT` and `DELETE /api/cart/items/{itemId}` |
| Orders | `POST /api/orders/checkout`, `GET /api/orders`, `GET /api/orders/{id}`, `PUT /api/orders/{id}/cancel`, `PUT /api/orders/{id}/status` (admin) |

## Roles

| Role | What it can do |
|---|---|
| Customer | Default role. Manage own cart, addresses and orders |
| Staff | Gets permissions from an admin |
| Manager | Manage products, categories and orders |
| System Admin | Only gives the Admin role to other people |

## What is not done yet

- API to manage discount codes.
- Image upload.
- Filter products by price range.
- Reviews, notifications and admin APIs.
- Payment have not done yet.

# 🧠 Signature Demo Project

A Java Spring Boot backend demo project integrating:
- JWT authentication
- Signature image upload
- SFTP integration (via Docker)
- Camunda workflow orchestration
- AI-based signature similarity check

---

## 🛠 Technologies
- Java 17 + Spring Boot 3.2.x
- Oracle Database (via Docker)
- Camunda BPM (v8)
- Springdoc OpenAPI 3 (Swagger UI)
- Docker + SFTP Server (atmoz/sftp)
- Postman for API testing

---

## 🚀 How to Run the Project

### 1. Clone the repo
```bash
git clone <your-repo-url>
cd signature-demo
```

### 2. Run Docker (Oracle DB + SFTP)
```bash
docker-compose up --build
```

- Oracle DB → `localhost:1521`, SID: `XEPDB1`, user: `system`, pass: `oracle`
- SFTP Server → `sftp://foo:pass@localhost:2222`, folder: `/upload`

### 3. Run Spring Boot App
Either from your IDE (IntelliJ) or using Maven:
```bash
./mvnw spring-boot:run
```
Swagger available at: [http://localhost:8080/v1/swagger-ui.html](http://localhost:8080/v1/swagger-ui.html)

---

## 📦 Backend Modules

| Endpoint          | Description                                 |
|-------------------|---------------------------------------------|
| `/v1/auth/login`  | Login endpoint returning JWT token          |
| `/v1/signatures`  | Upload, get detail, request approval, approve |
| `/v1/ai`          | Mock AI similarity scoring                  |
| `/actuator/**`    | Spring Boot health monitoring               |

---

## 📁 SFTP Demo

SFTP is mocked using [atmoz/sftp](https://github.com/atmoz/sftp).

- Access: `sftp://foo:pass@localhost:2222`
- Upload directory: `/home/foo/upload` (mapped to `./sftp-data` in project)

---

## 🗃 Database Notes

- Oracle DB runs in Docker container and auto-initializes schema and mock data from `import.sql`
- Spring Boot handles schema generation (`spring.jpa.hibernate.ddl-auto=update`)
- UUIDs are stored as `VARCHAR2(36)`

---

## 🧪 Test Credentials

| Role   | Username | Password |
|--------|----------|----------|
| Admin  | admin    | 123456   |
| User   | user     | 123456   |

---

## 📬 Contact

Maintainer: Huỳnh Tiên  
Email: tien.huynhlt.tn@gmail.com  
LinkedIn: [https://www.linkedin.com/in/tienhuynh-tn](https://www.linkedin.com/in/tienhuynh-tn)

---
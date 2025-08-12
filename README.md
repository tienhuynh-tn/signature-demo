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

### Runbook

```bash
docker compose up -d oracle-db

docker exec -it oracle-db bash -lc "sqlplus -L -S system/oracle123@127.0.0.1:1521/XE <<'SQL'
WHENEVER SQLERROR EXIT 1

CREATE USER APPLICATION_DEMO IDENTIFIED BY \"App#12345a\"
  DEFAULT TABLESPACE USERS
  TEMPORARY TABLESPACE TEMP
  QUOTA UNLIMITED ON USERS;

GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW,
      CREATE PROCEDURE, CREATE TRIGGER, CREATE TYPE TO APPLICATION_DEMO;

ALTER USER APPLICATION_DEMO ACCOUNT UNLOCK;

EXIT
SQL"

docker compose up -d sftp backend

docker exec -it oracle-db bash

sqlplus APPLICATION_DEMO/\"App#12345a\"@127.0.0.1:1521/XE

# Insert data line-by-line from data.sql
# (Run inside sqlplus as APPLICATION_DEMO)
/src/main/resources/data.sql
```

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
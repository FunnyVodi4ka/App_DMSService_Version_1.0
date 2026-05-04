# DMS Insurance Service — Version 1.0

## О приложении

Данный проект представляет собой микросервис для оформления полисов добровольного медицинского страхования (ДМС). Сервис реализует полный цикл работы со страховыми заявками: от создания и хранения данных застрахованных лиц до интеграции с внешними страховыми провайдерами. В основе архитектуры лежит событийная модель с использованием Kafka и CDC через Debezium, что обеспечивает надёжную и масштабируемую обработку страховых событий.

## Доступный функционал

На данный момент (Версия 1.0) реализованы следующие возможности:

- Оформление и управление страховыми полисами ДМС
- Хранение и обработка данных застрахованных лиц
- Интеграция с внешними страховыми провайдерами
- Событийная шина на базе Apache Kafka (KRaft-режим)
- CDC (Change Data Capture) через Debezium Kafka Connect
- Регистрация сервиса в Eureka Service Discovery
- Визуализация сообщений через Kafka UI

## Технологический стек

- **Java** — основной язык разработки
- **Spring Boot** — фреймворк для построения микросервиса
- **PostgreSQL 16** — хранение данных (с включённым logical replication)
- **Apache Kafka 3.7** (KRaft) — шина событий
- **Debezium 2.7** — Change Data Capture из PostgreSQL в Kafka
- **Eureka Server** — Service Discovery
- **Docker / Docker Compose** — контейнеризация и оркестрация

## Инструкция по запуску

### Запуск через Docker Compose

```bash
# 1. Клонировать репозиторий
git clone https://github.com/FunnyVodi4ka/App_DMSService_Version_1.0.git
cd App_DMSService_Version_1.0

# 2. Запустить инфраструктуру (БД, Kafka, Debezium, Eureka)
docker-compose up -d

# 3. Зарегистрировать Postgres-коннектор в Debezium
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d @register-postgres-connector.json
```

### Полезные адреса после запуска

| Сервис | Адрес |
|---|---|
| Swagger UI (API документация) | `http://localhost:8080/swagger-ui.html` |
| Eureka Dashboard | `http://localhost:8761` |
| Kafka UI | `http://localhost:8081` |
| Kafka Connect REST API | `http://localhost:8083` |

### Конфигурация окружения

Основные переменные задаются в файле `.env`:

| Переменная | Описание | По умолчанию |
|---|---|---|
| `DB_HOST` | Хост базы данных | `localhost` |
| `DB_PORT` | Порт базы данных | `5434` |
| `DB_NAME` | Название БД | `dms_db` |

## Разработчики

- Разработчик — Александр

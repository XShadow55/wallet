# Кошелек (Wallet Service) - README

Микросервис для управления балансом кошелька с поддержкой конкурентных операций до 1000 RPS.

## Оглавление

1. Особенности 

2. Технологический стек

3. Запуск приложения

4. API Endpoints

5. Архитектура решения

6. Конкурентная обработка

7. Тестирование

8. Настройки

9. Миграции БД

10. Разработчику

## Особенности

- ✅ Поддержка операций DEPOSIT и WITHDRAW

- ✅ Гарантированная обработка до 1000 RPS на один кошелек

- ✅ Оптимистичная блокировка через versioning

- ✅ Retry-механизм для конкурентных операций

- ✅ Полная валидация запросов

- ✅ Глобальная обработка ошибок

- ✅ Health-check через Actuator

- ✅ Запуск в Docker-окружении

## Технологический стек
- Язык: Java 17

- Фреймворк: Spring Boot 3

- База данных: PostgreSQL

- Миграции: Liquibase

- Сборка: Maven

- Контейнеризация: Docker, Docker Compose

- Тестирование: JUnit 5, Mockito

## Запуск приложения
### Требования
- Docker

- Docker Compose

### Запуск
```bash
# Клонировать репозиторий
git clone https://github.com/yourusername/wallet-service.git
cd wallet-service

# Запустить систему
docker-compose up --build
```
После запуска:

- Приложение доступно на порту 8070

- PostgreSQL доступна на порту 5432

- Health-check: http://localhost:8070/actuator/health

### Проверка работы
```bash
# Пополнение счета
curl -X POST http://localhost:8070/api/v1/wallet \
  -H "Content-Type: application/json" \
  -d '{
    "walletId": "c8a3d9b7-2f5a-4e8d-b8c1-2d7e9c0a1f6b",
    "operationType": "DEPOSIT",
    "amount": 100.00
  }'

# Проверка баланса
curl http://localhost:8070/api/v1/wallets/c8a3d9b7-2f5a-4e8d-b8c1-2d7e9c0a1f6b
```
## API Endpoints
### Обновление баланса
`POST /api/v1/wallet`

### Тело запроса (JSON):

```json
{
  "walletId": "c8a3d9b7-2f5a-4e8d-b8c1-2d7e9c0a1f6b",
  "operationType": "DEPOSIT",
  "amount": 100.00
}
```
### Возможные ответы:

- 200 OK: "Операция выполнена"

- 400 Bad Request: "Недостаточно средств"

- 404 Not Found: "Кошелек не найден"

- 400 Bad Request: "Вывод средств не удался, повторите позже"

### Получение баланса
`GET /api/v1/wallets/{walletId}`

Пример ответа:

```text
100.00
```
Коды состояния:

- 200 OK: текущий баланс

- 404 Not Found: "Кошелек не найден"

## Архитектура решения
### Компоненты
- WalletController: REST API для операций с кошельком

- WalletService: Бизнес-логика операций DEPOSIT/WITHDRAW

- WalletRepository: JPA-репозиторий для работы с БД

- GlobalExceptionHandler: Централизованная обработка ошибок

- LiquibaseConfig: Управление миграциями БД

## Конкурентная обработка
Для гарантированной обработки 1000 RPS реализованы:

### 1. Оптимистичная блокировка:

- Каждая запись в wallets имеет поле version

- При обновлении баланса проверяется соответствие версии

### 2. Pessimistic Locking:

- Используется `LockModeType.PESSIMISTIC_WRITE` при чтении

### 3. Retry-механизм:

- 3 попытки выполнения операции при конфликте версий

### 4. Атомарные UPDATE:

- Баланс изменяется одним SQL-запросом

```java
// Пример атомарного обновления
@Modifying
@Query("UPDATE Wallet w SET w.balance = w.balance - :amount, w.version = w.version + 1 " +
       "WHERE w.id = :id AND w.balance >= :amount AND w.version = :version")
int withdraw(UUID id, BigDecimal amount, Long version);
```
## Тестирование
Тестовое покрытие включает:

### Сервисный слой (WalletServiceTest)
- Успешное пополнение/снятие

- Ошибка при недостатке средств

- Обработка конкурентного доступа

- Кошелек не найден

### Контроллер (WalletControllerTest)
- Успешные операции

- Невалидный JSON

- Кошелек не найден

- Проверка баланса

### Обработка ошибок (GlobalExceptionHandlerTest)
- Обработка невалидных запросов

- Форматирование ошибок

#### Запуск тестов:

```bash
mvn test
```
## Настройки
Основные параметры можно изменить через переменные окружения:
```
# Настройки БД
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/wallet
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=12345678

# Пул соединений
SPRING_DATASOURCE_HIKARI_MAXIMUM-POOL-SIZE=15

# Liquibase
SPRING_LIQUIBASE_ENABLED=true
SPRING_LIQUIBASE_CHANGE_LOG=classpath:/db/changelog-master.yaml

# Память JVM
JAVA_OPTS=-XX:MaxRAMPercentage=75
```


## Миграции БД
Миграции управляются через Liquibase:

- `src/main/resources/db/changelog-master.yaml`

- `src/main/resources/db/changelog/v1.0.0-initial-schema.yaml`

### Структура таблицы:

```sql
CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    version BIGINT NOT NULL DEFAULT 0
);
```
## Разработчику
### Сборка
```bash
mvn clean package
```
### Запуск без Docker
```bash
# Настройте БД в application.properties
mvn spring-boot:run
```
Автор: Константин

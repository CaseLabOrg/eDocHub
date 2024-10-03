# File Storage Service

Это приложение предоставляет REST API для хранения и управления файлами. Сервис разработан с использованием Spring Boot и PostgreSQL и может быть развернут с использованием Docker и Docker Compose.

## Требования:
- Docker
- Docker Compose

## Запуск проекта:
Чтобы запустить проект, выполните следующие шаги:

1. Перейдите в корневую директорию проекта и выполните команду для сборки Docker-образа:

```bash
docker build -t filestorage-app .
```

2. После успешной сборки образа запустите контейнеры с помощью `docker-compose`:

```bash
docker-compose up —build
```

3. После того как контейнеры будут запущены, сервис будет доступен по следующему адресу:

[http://localhost:8080/hello](http://localhost:8080/hello)

4. Чтобы открыть Swagger UI, используйте следующий URL:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Swagger UI предоставляет возможность взаимодействовать с REST API и просматривать всю доступную документацию.

## Остановка проекта:
Чтобы остановить все запущенные контейнеры, используйте команду:

```bash
docker-compose down
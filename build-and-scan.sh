#!/bin/bash
# build-and-scan.sh

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Запуск процесса сборки и сканирования ===${NC}"

# 1. Сборка проекта
echo -e "${YELLOW}Шаг 1: Сборка проекта с Maven...${NC}"
mvn clean package

if [ $? -ne 0 ]; then
    echo -e "${RED}Ошибка сборки проекта!${NC}"
    exit 1
fi

# 2. Запуск SonarQube
echo -e "${YELLOW}Шаг 2: Запуск SonarQube...${NC}"
docker-compose up -d sonarqube

# Ждем запуска SonarQube
echo -e "${YELLOW}Ожидание запуска SonarQube (40 секунд)...${NC}"
for i in {1..40}; do
    echo -n "."
    sleep 1
done
echo ""

# 3. Сканирование SonarQube
echo -e "${YELLOW}Шаг 3: Запуск SonarQube сканирования...${NC}"

# Используем Maven плагин
mvn sonar:sonar \
  -Dsonar.projectKey=crud-application \
  -Dsonar.projectName="CRUD Application" \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=admin \
  -Dsonar.password=admin

if [ $? -ne 0 ]; then
    echo -e "${RED}Ошибка SonarQube сканирования!${NC}"
    echo -e "${YELLOW}Попробуйте вручную:${NC}"
    echo -e "1. Откройте http://localhost:9000"
    echo -e "2. Логин: admin, Пароль: admin"
    echo -e "3. Перейдите вручную в раздел проектов"
fi

# 4. Сканирование Trivy
echo -e "${YELLOW}Шаг 4: Сборка Docker образа...${NC}"
docker build -t crud-application:latest .

echo -e "${YELLOW}Шаг 5: Сканирование безопасности с Trivy...${NC}"

# Проверяем установлен ли Trivy
if ! command -v trivy &> /dev/null; then
    echo -e "${RED}Trivy не установлен локально! Используем Docker версию...${NC}"

    # Создаем директорию для отчетов
    mkdir -p reports

    # Запускаем Trivy через Docker
    docker run --rm \
      -v /var/run/docker.sock:/var/run/docker.sock \
      -v $PWD/reports:/reports \
      aquasec/trivy:latest \
      image --format template --template "@contrib/html.tpl" \
      -o /reports/trivy-report.html crud-application:latest

    # Консольный вывод
    docker run --rm \
      -v /var/run/docker.sock:/var/run/docker.sock \
      aquasec/trivy:latest \
      image crud-application:latest
else
    # Локальный Trivy
    mkdir -p reports

    # Генерация HTML отчета Trivy
    trivy image --format template --template "@contrib/html.tpl" \
      -o reports/trivy-report.html crud-application:latest

    # Консольный отчет
    echo -e "${YELLOW}Консольный отчет Trivy:${NC}"
    trivy image --severity HIGH,CRITICAL crud-application:latest

    echo -e "${GREEN}HTML отчет сохранен в reports/trivy-report.html${NC}"
fi

# 5. Запуск приложения
echo -e "${YELLOW}Шаг 6: Запуск CRUD приложения...${NC}"
docker-compose up -d crud-app

echo -e "${GREEN}=== Процесс завершен! ===${NC}"
echo -e "${YELLOW}Доступ к сервисам:${NC}"
echo -e "CRUD приложение:  http://localhost:8080"
echo -e "SonarQube:        http://localhost:9000 (admin/admin)"
echo -e "Отчет Trivy:      ./reports/trivy-report.html"
echo -e "${YELLOW}Логи приложения: docker-compose logs -f crud-app${NC}"
echo -e "${YELLOW}Для остановки:   docker-compose down${NC}"
# Food Delivery Platform

Онлайн-сервіс замовлення та доставки їжі з ресторанів (мікросервісна архітектура, Java 17 + Maven).

## Структура

| Шлях | Призначення |
|------|-------------|
| `services/auth-service` | Реєстрація, автентифікація, ролі (клієнт, ресторан, кур'єр, адміністратор) |
| `services/restaurant-service` | Каталог ресторанів і меню, пошук і фільтри |
| `services/order-service` | Замовлення: розрахунок суми, життєвий цикл статусів |
| `services/payment-service` | Перевірка платежів (сума, контрольна сума картки за Луном) |
| `services/delivery-service` | Призначення найближчого кур'єра |
| `services/notification-service` | Push/SMS/email-сповіщення про зміну статусу |
| `docs/technical-spec.md` | Базове технічне завдання |
| `Jenkinsfile` | CI/CD-конвеєр: збірка Maven, unit-тести JUnit, архівування JAR |

## Локальна збірка

```bash
mvn clean test      # збірка і unit-тести всіх сервісів
mvn package         # JAR-файли в services/*/target/
java -jar services/order-service/target/order-service.jar   # GET http://localhost:8083/health
```

CI trigger check

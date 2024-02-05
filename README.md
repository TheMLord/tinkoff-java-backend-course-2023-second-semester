![Bot](https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester/actions/workflows/bot.yml/badge.svg)
![Scrapper](https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester/actions/workflows/scrapper.yml/badge.svg)

# Link Tracker

ФИО: Березин Михаил Александрович   

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 21` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для работы требуется БД `PostgreSQL`. Присутствует опциональная зависимость на `Kafka`.

# java-shareit

***Учебный проект Яндекс Практикума.***

Backend приложения для совместного использования и аренды вещей. 
Позволяет рассказать о вещах, которыми пользователь готов поделиться, 
найти и арендовать на определенное время нужную вещь,
а также оставить запрос о поиске нужной вещи, если она не нашлась в сервисе.
---
### Стек технологий

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring%20Boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

---

### Архитектура

Валидация входных запросов выделена в отдельный легковесный сервис-шлюз. 
После валидации запрос перенаправляется основному сервису. 
Межсервисное взаимодействие осуществляется через REST с использованием RestTemplate.

---
### Хранение данных

![Схема базы данных](/img/shareit-schema.png)
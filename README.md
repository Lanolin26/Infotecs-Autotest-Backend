# Infotecs Java Back-end приложение  
  
[![Build Status](https://img.shields.io/github/release/pandao/editor.md.svg)](https://github.com/Lanolin26/Infotecs-Autotest-Backend/releases/tag/v1.0)
  
## Инструкция по сборке  

1. Скачайте исходные файлы проекта
2. Распакуйте в удобное место и откройте в терминале
3. В распакованном папке будут две папки 'client' и 'server'
4. Для сборки клиента/сервера:
	- Перейти в папку client/server
	- `make`
	- `make install`

## Инструкция по запуску Клиент  
Запуск клиента производится с помощью команды: 
`java -jar  infotecs_client-1.0.jar [host]`
, где `[host]` адрес сервера для подключения, задается опционально. 
Если он не задан, будет использоваться адресс из файла .properties (по умолчанию `localhost`)

## Инструкция по запуску Сервер
Запуск клиента производится с помощью команды: 
`java -jar  infotecs_server-1.0.jar`
В папке, где произведен запуск, будет создан файл `storage.xml`, с хранящимися сообщениями

# File Sharing Telegram Bot

This Telegram bot simplifies file sharing on Telegram by providing users with a convenient way to
upload and download files while generating persistent download links.

## Description

File Sharing Telegram Bot is a Java-based Telegram bot that allows users to send various types of content,
such as images, photos, and documents, and receive a permanent link to download the uploaded file.
The bot is built using Spring Boot and follows a microservices architecture.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Features](#features)
- [Technologies](#technologies)
- [Contributing](#contributing)
- [Commit Message Convention](#commit-message-convention)
- [License](#license)

## Installation

To install and run the File Sharing Telegram Bot, follow these steps:

1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/andyBzg/file-sharing-telegram-bot.git
   
2. Obtain your Telegram Bot Username and Token by following these instructions: https://core.telegram.org/bots/features#creating-a-new-bot
3. Download Ngrok and register to get Authtoken https://ngrok.com/
4. Run Ngrok and enter these commands:
   ```
   ngrok config add-authtoken [your token]
   ngrok http 8084

5. Copy Forwarding address from Ngrok console
   ```
   https:// ... .ngrok-free.app 

6. Set up your dispatcher/resources/application.properties:
   ```
   bot.name=[bot username]
   bot.token=[bot token]
   bot.uri=[forwarding uri]
   
7. Also copy bot token property to node/resources/application.properties
   
8. Using Terminal run these commands:
   ```
   mvn install
   docker compose up

## Usage

To use the File Sharing Telegram Bot, follow these steps:

1. Find the bot on Telegram using its Username
2. Start a chat with the bot on Telegram.
3. Use the available commands to upload, download, and manage files.

## Features

* File uploading and downloading
* Microservices architecture for scalability
* Integration with Telegram for seamless user interaction

## Technologies

The File Sharing Telegram Bot is developed using the following technologies and libraries:

* Spring Boot
* Telegram API
* Spring Web MVC
* Docker
* PostgreSQL
* RabbitMQ
* Hibernate
* Junit5 and Mockito
* Maven

## Contributing

Contributions to the File Sharing Telegram Bot are welcome! Follow these steps to contribute:

1. Fork the project.
2. Create a branch (git checkout -b feature/new-feature).
3. Commit your changes (git commit -am 'Add new feature').
4. Push the branch (git push origin feature/new-feature).
5. Open a Pull Request.

## Commit Message Convention

We follow a commit message convention to maintain a clear and organized commit history. 
Below is a guide to the meaning of each commit tag:

- **build**: Changes related to the build process or external dependencies.
- **sec**: Security-related changes or vulnerability fixes.
- **ci**: Configuration changes for CI/CD and script management.
- **docs**: Updates or additions to documentation.
- **feat**: Additions of new features.
- **fix**: Bug fixes.
- **perf**: Changes aimed at improving performance.
- **refactor**: Code changes that neither fix errors nor add new features (code restructuring).
- **revert**: Reverting to previous commits.
- **style**: Code style changes (tabs, indentation, punctuation, etc.).
- **test**: Additions or modifications to tests.

Commit messages should be clear, concise, and follow this convention for better collaboration and 
understanding of project changes.

### Example Commit Message

> feat: Add file upload functionality

## License

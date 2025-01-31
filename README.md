# 🌍 News Insights Web Application - VibePress

A reactive web application that lets you explore the latest news articles, 
view detailed article information with related reddit posts and save your favorite articles and sources.

---

## 📚 Outline

- [Features](#features)
    - [General Features](#general-features)
    - [Article Detail Pages](#article-detail-pages)
    - [User Account Page](#user-account-page)
- [Technologies Used](#technologies-used)
    - [Backend](#backend)
    - [Database](#database)
    - [APIs](#apis)
    - [Frontend](#frontend)
- [Application Architecture](#application-architecture)
    - [Overview](#overview)
    - [Key Modules](#key-modules)
    - [Database Structure](#database-structure)
- [How to Run](#how-to-run)
    - [Prerequisites](#prerequisites)
    - [Steps](#steps)
- [Endpoints](#endpoints)
    - [Public Endpoints](#public-endpoints)
- [Testing](#testing)
    - [Unit Tests](#unit-tests)
- [Folder Structure](#folder-structure)
- [Future Enhancements](#future-enhancements)
- [Contributors](#contributors)

---

## Features

### General Features
- View **top headlines** from your country.
- Search for articles using a **custom query**.
- Explore:

### Article Detail Pages
- Article information.
- Related Reddit Posts.
- **Sentiment analysis** visualized with pie charts.

### User Account Page
- View your **saved articles**.
- View your **favorite sources**.

---

## Technologies Used

### Backend
- **Spring Boot** with Kotlin.
- **Spring WebFlux** for reactive, non-blocking programming.
- **R2DBC** for working with the database reactively.
- **Flyway** for database migrations.
- **Docker** for containerized database management.

### Database
- R2DBC database with `ReactiveCrudRepository` for seamless reactive interactions.

### APIs
- **[News API](https://newsapi.org/)**: Fetch top headlines and search for articles.
- **[Reddit API](https://www.reddit.com/dev/api/)**: Retrieve Reddit posts and comments.
- **[Google Natural Language API](https://cloud.google.com/natural-language)**: Perform sentiment analysis for articles and Reddit discussions.

### Frontend
- **Thymeleaf** templates for dynamic HTML rendering.
- **CSS** for styling.

---

## Application Architecture

### Overview
This project is a **reactive Spring Boot application** that integrates multiple APIs and services to deliver a seamless user experience. The backend handles API calls, sentiment analysis, and database interactions, while the frontend renders the content dynamically.

### Key Modules
1. **Controller Layer**: 
   - Handles HTTP requests, routes, and prepares data for views by delegating logic to services.
2. **Service Layer**: 
     - API Services: Manage interactions with external APIs like NewsAPI, RedditAPI, and Google Natural Language API.
     - Database Services: Handle application logic for database entities and interact with repositories.
3. **Repository Layer**: 
   - Communicates with the R2DBC database reactively.

### Database Structure

The database consists of the following tables:

- **ARTICLE**: Stores articles with title, author, content, and sentiment attributes.
- **REDDIT_POST**: Contains Reddit posts with link, title, content, and sentiment attributes.
- **REDDIT_COMMENT**: Contains Reddit Comments with content and sentiment attributes.
- **PROJECT_USER**: Tracks users and their preferred sources.
- **COUNTRY**: Stores country information and associated sources.
- **ARTICLE_REDDIT_POST**: Links articles to Reddit posts.
- **USER_SAVED_ARTICLE**: Saves articles that are bookmarked by users.
- **COUNTRY_ARTICLE**: Links articles to countries.

---

## How to Run

### Prerequisites
- **Java 17** or higher (tested with Java 21).
- **Docker** installed and running.
- **Kotlin setup**.

### Steps
1. Clone the repository:
   ```bash  
   git clone https://github.com/JuliaTrumpp/vipepress.git  
   cd article.analysis  
2. Start the database with Docker:
   ```bash 
   docker-compose up -d
4. Build and run the application:
   ```bash 
   ./gradlew bootRun  
5. Access the application in your browser:
   ```bash 
   http://localhost:8080  
   
## Endpoints

Public Endpoints
- Home Page: /
    - Displays top headlines, option to search articles by query + menu bar options.
- Article Detail Page: /article/{article-id}
    - Displays article details, Reddit posts, and sentiment pie charts.
- Account Account: /account/{name}
    - Displays saved articles and favorite sources.

## Testing 

### Unit Tests

Service layer are covered with unit tests to ensure functionality.

## Folder Structure
      ```bash 
    src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com.stacs.article.analysis/
    │   │       ├── backend/
    │   │       │   ├── apis/
    │   │       │   │   ├── ArticleApiDataClasses.kt
    │   │       │   │   ├── ...
    │   │       │   ├── Article/
    │   │       │   │   ├── Article.kt
    │   │       │   │   ├── ArticleRepository.kt
    │   │       │   │   └── ArticleService.kt
    │   │       │   └── ...
    │   │       │   
    │   │       ├── frontend_controller/
    │   │       │   ├── AccountPageController.kt
    │   │       │   ├── ...
    │   │       ├── libarys/
    │   │       │   └── UsefullFunctionsLibrary.kt
    │   │       └── Application.kt
    │   ├── resources/
    │   │   ├── db.migration/
    │   │   │   └── V1_initial_setup.sql
    │   │   ├── static/
    │   │   │   ├── styles_account.css
    │   │   │   └── ...
    │   │   ├──templates/
    │   │   │   ├── account.html
    │   │   │   └── ...
    │   │   └── application.properties
    └── test/
        └── same structure as main for tests


## Future Enhancements

- Sentiment analysis with Google Language Api
- Graphs 
  - to visualize articles (by category, length, source, number of quotes on reddit(?))
  - the article's and post's sentiment
- Country detail page:
  - information and filter articles by category, language, date or source + sort them
  - average sentiment of articles + in country's subreddit
- Country comparison
  - comparison of Reddit (country's subreddits) and article sentiments (also by category)
  - number of articles (top headlines) + other statistics
- Save source on article page and account page
- Sources detail page:
    - list of last articles, which language/ country
- Possibility to delete articles/ other data base entries


## Contributors

Julia Trumpp

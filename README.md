Here’s a Spring Boot app that allows you to get all your liked songs from Spotify with the click of a button.

<img src="src/main/resources/static/examples/landing-page-screenshot.png" width=50% alt="landing page">

You’ll be redirected to Spotify to log in to your account.

<img src="src/main/resources/static/examples/login-screenshot.png" width=50% alt="spotify login">

Your liked songs will then be displayed for you to print.

<img src="src/main/resources/static/examples/songs-screenshot.png" width=50% alt="songs page">

It makes use of the following frameworks:

* Spring Beans for autowiring service classes and configuration management (IoC)
* OkHttp Client to manage Spotify OAuth 2.0 and token flows
* Google Gson and Jackson for robust parsing of Spotify API responses
* Thymeleaf to display the album objects on an HTML template
* Modern java.time API for ordering songs chronologically by release date
* Mockito and JUnit for secure unit tests of OAuth, Json parsing and Object handling

This app is remains a work in progress as I move to:
* deploy using PKCE authorisation 
* persist users’ songs to a database
* provide optional song recommendations for further music discovery

‼️ Spotify does restrict usage of their API to paid accounts. That means as a free-tier user, you won't be able to export your liked songs. 

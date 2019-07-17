# NewsReader
News Reader App based on "The Complete Android Oreo Developer Course" from Rob Percival.

# Description
The App displays a list of top news from the website https://news.ycombinator.com/ in the main activity. The user can select the desired article to read, which launches the ArticleActivity and displays the content in a webView widget.

# Characteristics
- Main Activity contains ListView to display the title of the available articles
- Makes use of DownloadTask to download the content utilising the Hacker News API https://github.com/HackerNews/API
- Stores the Article ID, Title, and Content in an SQLite database
- Displays the content in a WebView through a secondary activity

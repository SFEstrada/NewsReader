# NewsReader
News Reader App based on "The Complete Android Oreo Developer Course" from Rob Percival.

# Description
The App displays a list of top and new stories from which the user can then choose to read in the app.

# Characteristics
- Main Activity contains ListView to display the title of the available articles
- Makes use of DownloadTask to download the content utilising the Hacker News API https://github.com/HackerNews/API
- Stores the Article ID, Title, and Url in an SQLite database
- Displays the content in a WebView through a secondary activity by loading the url
- Button to update top stories and see new stories

# Credits
- Launcher icon credits: <div>Icons made by <a href="https://www.freepik.com/?__hstc=57440181.2085911cc5ab8003d6546729cf973c2c.1563374635012.1563374635012.1563378547468.2&__hssc=57440181.1.1563378547468&__hsfp=4103235252" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/"                 title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/"                 title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>

# Deal-or-No-Deal in plain HTML
The game Deal or No Deal but it uses no JavaScript and no CSS
### [Play it here](http://deal.thomasnakagawa.com)

## Setup instructions
Openable with IntelliJ IDEA. Open the project and import the dependencies.
Requires Java 1.8

#### Leaderboard instruction
1. Add a directory to the root called private
2. In this directory add a file called dreamloKey.txt
3. Create a leaderboard at [dreamlo.com](http://dreamlo.com) and put the private key in this file

### About
This project was a challenge to see what I could do with just HTML. Web stuff now uses alot JS and CSS, so I wanted to see what it would be like to not use them at all. To do the layouts withoout CSS, I used alot of <center> elements, tables and &nbsp's. To make the main case screen where there are four rows of cases that are offset from each other, I made each row its own table, and put each in a <center> tag. The rows have different numbers of cases in them so by centering them it makes them offset from each other.
For the text, I used different header levels and highlighting and strikethroughs. I made it so the money values remaining and the money values opened are always highlighted, since it's important for the player to be keeping track of them.
I also didn't use cookies and instead made it so that the links on the page contain both a game ID and the action that the link does to the game. When the service gets a request, it changes the game state, then serves a new page with the same game ID in all the links.

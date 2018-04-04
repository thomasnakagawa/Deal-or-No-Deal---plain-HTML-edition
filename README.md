# Deal-or-No-Deal in plain HTML
The game Deal or No Deal without using JavaScript or CSS
### [Play it here](http://deal.thomasnakagawa.com)

## Setup instructions
The program is can be opened with IntelliJ IDEA. After opening the project import the dependencies.
This program requires Java 1.8

#### Leaderboard instruction
1. Add a directory to the private root
2. In this directory add a file called dreamloKey.txt
3. Create a leaderboard at [dreamlo.com](http://dreamlo.com) and put the private key in this file

### About
The goal of this project is to see what is possible in HTML. Web stuff now uses JS and CSS, so it was essential to see what it would be like not to use JS or CSS at all. To do the layouts withoout CSS required the use of <center> elements, tables, and &nbsp's. To make the main case screen, where there are four rows of offset cases, each row has its own table with a <center> tag. Each row has different numbers of cases, so by centering them they offset each other. Different header levels, highlighting, and strikethroughs were used for the text. The money values remaining and the money values opened are always highlighted, since it's important for the player to keep track of them.
Instead of using cookies the links on the page each contain both a game ID and the action that the link does in the game. When the service gets a request it changes the game state, and serves a new page with the same game ID as all the links.

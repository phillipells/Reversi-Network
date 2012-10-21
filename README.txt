******CSCE 315 Team 6******
Phillip Ells
William Guerra
Daniel Tan

compiling:
    javac telnetReversi.java 
		(on server host)


Running:
	java telnetReversi 
		(on server host)
	This is to be run on the server host
	You will be prompted for a port number
	Use a port number between 8000 and 640000
	(9999 works fine)
	To connect to the server with a telnet client, enter the command
		telnet [host address] [port number]
		ex: telnet localhost 9999
			(on client machine)
	If you are unsure what your host address is, try entering "hostname" in a terminal
	
Gameplay:
	First the user (client) will be shown a menu and will be prompted to select a game.
	They are numbered 1-5 and should be enterd as such.
	Next the user will be prompted to select a color; enter 'w' or 'b'
	Next a list of commands will print showing:
		quit
		display_on
		display_off
		undo
		redo
	and their explanations
	The game will now begin and the black player will start its move.
	If it is the AI's turn, it will select a move and then switch back to the users turn
	If it is a human's turn, it will prompt for the input of the desired move or a command.
	Valid move entries should be of the form [column letter][row number]
		ex: 'd3'
	Informative messages about the game will appear on the bottom right-hand side of the board
	The game will continue taking turns until there are no valid moves left for either player
	After the game has finished, the user will be prompted to play again; enter 'y' or 'n'
	very simple stuff

Additional:
	To play against another machine, or to play two difficulties against eachother,
	simply run the game in two different terminals, select opposite colors in each game,
	and manually enter the outputs (moves) from each game into the opposite game.
	an example can be seen in easyVmedium.jpg

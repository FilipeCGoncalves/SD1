package ttt.grpc.client;

import java.util.Scanner;

// Channel package (connection abstraction. Transport layer, etc.)
import io.grpc.ManagedChannel;

// Import messages from the contract package
import ttt.grpc.TttContract;

// Import stub from the contract package
import ttt.grpc.TTTServiceGrpc;

public class Game {
	/* *!* Fijarse que la clase TTT no tiene un constructor, es decir que sus variables
	  son como globales (no deben ser accesadas de forma concurrente). En cambio esta
	  clase tiene un constructor y cada jugador la instanciara. Ambos compartiran la 
	  instancia de TTT pero cada uno tendra su instancia de Game */
	
	final ManagedChannel channel;
	TTTServiceGrpc.TTTServiceBlockingStub stub;
	Scanner keyboardSc;
	int winner = 0;
	int player = 1;

	public Game(ManagedChannel c, TTTServiceGrpc.TTTServiceBlockingStub s) {
		channel = c;
		stub = s;
		keyboardSc = new Scanner(System.in);
	}

	public int readPlay() {
		int play;
		do {
			System.out.printf(
					"\nPlayer %d, please enter the number of the square "
							+ "where you want to place your %c (or 0 to refresh the board): \n",
					player, (player == 1) ? 'X' : 'O');
			play = keyboardSc.nextInt();
		} while (play > 9 || play < 0);
		return play;
	}

	public void playGame() {
		// Declaring request messages defined in TttContract 
		TttContract.playRequest playReq;
		TttContract.currentBoardRequest currentBoardReq = currentBoardRequest.newBuilder().build(); // These last two are empty messages
		TttContract.winnerRequest winnerReq = winnerRequest.newBuilder().build();

		// Declaring response messages
		TttContract.playResponse playAccepted;
		TttContract.currentBoardResponse cb;
		TttContract.winnerResponse winner;
		
		do {
			player = ++player % 2;
			do {
		
				cb = stub.currentBoard(currentBoardReq);
		
				System.out.println(cb);
				play = readPlay();
				playReq = playRequest.newBuilder().setRow(--play / 3)
												  .setCol(play % 3)
												  .setPlayer(player).build();
				if (play != 0) {
					/* *!* Como el tablero se identifica del 1 al 10, se debe restar 
					 * al indice - 1 y dividirlo entre 3 */
					playAccepted = stub.play(playReq);
			
					System.out.printf(playAccepted); // *!* borrar
					if (!playAccepted)
						System.out.println("Invalid play! Try again.");
				} else
					playAccepted = false;
			} while (!playAccepted);

			winner = stub.checkWinner(winnerReq);
		
		} while (winner == -1);
	}

	public void congratulate() {
		if (winner == 2)
			System.out.printf("\nHow boring, it is a draw\n");
		else
			System.out.printf("\nCongratulations, player %d, YOU ARE THE WINNER!\n", winner);
	}

	public static void main(String[] args) {
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", HelloClient.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final String target = host + ":" + port;

		// Creating gRPC channel with an asyncronous stub, since we
		// need to get a response of each message
		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

		// Creating blocking stub. *!* read more about stub types and their relation with threads
		TTTServiceGrpc.TTTServiceBlockingStub stub = TTTServiceGrpc.newBlockingStub(channel);

		Game g = new Game(channel, stub);
		g.playGame();
		g.congratulate();
	}
}
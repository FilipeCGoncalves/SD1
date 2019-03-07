package ttt.grpc.client;

import java.util.Scanner;

// Import messages from the contract package
import ttt.grpc.TttContract;

// Import stub from the contract package
import ttt.grpc.TTTServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TTTClient {

	final ManagedChannel c;
	TTTServiceGrpc.TTTServiceBlockingStub s;
	Scanner keyboardSc;
	int winner = 0;
	int player = 1;


	public TTTClient(ManagedChannel channel, TTTServiceGrpc.TTTServiceBlockingStub stub) {
		c = channel;
		s = stub;
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
		int play;

		// Declaring request messages defined in TttContract 
		TttContract.playRequest playReq;
		// To avoid creating empty messages, use winnerRequest.getDefaultInstance()
		TttContract.currentBoardRequest currentBoardReq = TttContract.currentBoardRequest.newBuilder().build(); // These last two are empty messages
		TttContract.winnerRequest winnerReq = TttContract.winnerRequest.newBuilder().build();
		
		// Variables with the actual values of the stubs
		String currentBoard;
		boolean playAccepted; // playAccepted == playMade (change in on the .proto) *!*

		do {
			player = ++player % 2;
			do {
		
				currentBoard = s.currentBoard(currentBoardReq).getBoard();
		
				System.out.println(currentBoard);
				play = readPlay();
				playReq = TttContract.playRequest.newBuilder().setRow(--play / 3)
												  .setCol(play % 3)
												  .setPlayer(player).build();
				if (play != 0) {
					/* *!* Como el tablero se identifica del 1 al 10, se debe restar 
					 * al indice - 1 y dividirlo entre 3 */
					playAccepted = s.play(playReq).getPlayMade();
			
					if (!playAccepted)
						System.out.println("Invalid play! Try again.");
				} else
					playAccepted = false;
			} while (!playAccepted);

			winner = s.checkWinner(winnerReq).getWinner();
		
		} while (winner == -1);
	}

	public void congratulate() {
		if (winner == 2)
			System.out.printf("\nHow boring, it is a draw\n");
		else
			System.out.printf("\nCongratulations, player %d, YOU ARE THE WINNER!\n", winner);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(TTTClient.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", TTTClient.class.getName());
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
		
		TTTClient ttt = new TTTClient(channel, stub);

		// A Channel should be shutdown before stopping the process.
		channel.shutdownNow();
	}

}

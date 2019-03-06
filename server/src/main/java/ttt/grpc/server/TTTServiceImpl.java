package ttt.grpc.server;

// Import the contract files
// mvn will look for any path that matches
// ttt/grpc/file_name.java 

// Import messages from the contract package
import ttt.grpc.TttContract;

// Import services from the contract package
import ttt.grpc.TTTServiceGrpc;

// Importing StreamObserver (response)
import io.grpc.stub.StreamObserver;

// Import TTT file (just like the local version)
// *!* es necesario? estan en la misma carpeta
import ttt.grpc.server.TTT;

public class TTTServiceImpl extends TTTServiceGrpc.TTTServiceImplBase {
	private TTT ttt = new TTT();

	@Override
	public void currentBoard(TttContract.currentBoardRequest request, StreamObserver<TttContract.currentBoardResponse> responseObserver) {

		// In this case the request message is empty, so we don't use it
		
		// Getting current board
		String board = ttt.currentBoard();

		// Creating Protobuff object with a builder
		TttContract.currentBoardResponse response = TttContract.currentBoardResponse
													.newBuilder().setBoard(board).build();

		// Use responseObserver to send a single response back
		responseObserver.onNext(response);

		// Service completed
		responseObserver.onCompleted();
	}


	@Override
	public void play(TttContract.playRequest request, StreamObserver<TttContract.playResponse> responseObserver) {

		// Getting row, col and player from the request message (see line 17 in TttContract)
		int row = request.getRow();
		int col = request.getCol();
		int player = request.getPlayer();

		// Performing play
		boolean playMade = ttt.play(row, col, player);

		// Creating Protobuff object with a builder
		TttContract.playResponse response = TttContract.playResponse
												.newBuilder().setPlayMade(playMade).build();

		// Use responseObserver to send a single response back
		responseObserver.onNext(response);

		// Service completed
		responseObserver.onCompleted();

	}

	@Override
	public void checkWinner(TttContract.winnerRequest request, StreamObserver<TttContract.winnerResponse> responseObserver) {


		// In this case the request message is empty, so we don't use it

		int winner = ttt.checkWinner();

		// Creating response message setting the field winner
		TttContract.winnerResponse response = TttContract.winnerResponse
														.newBuilder().setWinner(winner).build();

		// Use responseObserver to send a single response back
		responseObserver.onNext(response);

		// Service completed
		responseObserver.onCompleted();
	}
}
package ttt.grpc.server;

// Import the contract files
// mvn will look for any path that matches
// ttt/grpc/file_name.java 

// Import messages
import ttt.grpc.TttContract;

// Import services
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

		// In this case the request message is empty, so we only answer
		String board = ttt.currentBoard();

		// Creating Protobuff object with a builder
		TttContract.currentBoardResponse response = TttContract.currentBoardResponse
													.newBuilder().setBoard(board).build();

		// Use responseObserver to send a single response back
		responseObserver.onNext(response);

		// Service completed
		responseObserver.onCompleted();
	}
}
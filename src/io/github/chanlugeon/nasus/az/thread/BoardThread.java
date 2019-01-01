package io.github.chanlugeon.nasus.az.thread;

import java.io.IOException;

import io.github.chanlugeon.nasus.az.board.Board;

public class BoardThread extends Thread {
	Board board;
	
	public BoardThread(Board board) {
		this.board = board;
	}
	
	@Override
	public void run() {
		do {
			try {
				board.visitInReverseOrder().writeComment();
				System.out.print(board.currentPage());
				Thread.sleep(4000);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (board.currentPage() != 1);
	}
}

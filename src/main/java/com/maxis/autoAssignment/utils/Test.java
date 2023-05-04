package com.maxis.autoAssignment.utils;

import java.util.LinkedList;
import java.util.Queue;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Queue<String> schAssignQueue = new LinkedList<String>();
		for(int i=0;i<100;i++){
			schAssignQueue.add(i+"");
		}
	
		Queue<String> tempQueue = new LinkedList<String>();
		tempQueue.addAll(schAssignQueue);
		
		while(!tempQueue.isEmpty()){
			System.out.println(tempQueue.peek());
			tempQueue.poll();
		}
		
	}

}

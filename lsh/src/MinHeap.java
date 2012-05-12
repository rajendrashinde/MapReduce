package lsh;  

import java.util.Set; 
import java.util.List; 
import java.util.ArrayList; 
import java.util.HashSet;

public class MinHeap {
    private List<Set<Integer>> Heap;
    private int maxsize;
    public int size;
	private Float [] x; 
	private Integer [] sorted_order; 

    public MinHeap(int max, Float[] x, Integer[] sorted_order) {

		this.maxsize = max;
		this.Heap = new ArrayList<Set<Integer>> ();
		this.size = 0 ;
		this.x = x; 	
		this.sorted_order = sorted_order; 
		Set<Integer> init = new HashSet<Integer> ();
		init.add(new Integer(-10000));
		Heap.add(init);
    }

    private int leftchild(int pos) {
		return 2*pos;
    }
    private int rightchild(int pos) {
		return 2*pos + 1;
    }

    private int parent(int pos) {
		return  pos / 2;
    }
    
    private boolean isleaf(int pos) {
		return ((pos > size/2) && (pos <= size));
    }

    private void swap(int pos1, int pos2) {
		Set<Integer> tmp;

		tmp = Heap.get(pos1);
		Heap.set(pos1, Heap.get(pos2));
		Heap.set(pos2, tmp);
    }

    public void insert(Set<Integer> elem) {
		size++;
		Heap.add(elem);
		int current = size;
	
		while (Score(Heap.get(current)) < Score(Heap.get(parent(current)))) {
	    	swap(current, parent(current));
	    	current = parent(current);
		}	
    }

    public void print() {
		int i;
		System.out.print("Heap size: " +  size + " , "); 
		for (i=1; i<=size;i++)
	    	System.out.print(ToString(Heap.get(i)) + ": " + Score(Heap.get(i)) + "; ");
		System.out.println();
    }

	public String ToString(Set<Integer> A){
		String out = ""; 
		for (Integer i : A) 
			out += i + " ";
		out = out.trim();  
		return out; 		
	}
	
    public Set<Integer> removemin() {

		swap(1,size);
		Set<Integer> out = Heap.get(size); 
		Heap.remove(size);
		size--;
		 
		if (size != 0)
	    	pushdown(1);
		return out;
    }

    private void pushdown(int position) {
		int smallestchild;
		while (!isleaf(position)) {
	    	smallestchild = leftchild(position);
	    	if ((smallestchild < size) && (Score(Heap.get(smallestchild)) > Score(Heap.get(smallestchild+1))))
				smallestchild = smallestchild + 1;
	    	if (Score(Heap.get(position)) <= Score(Heap.get(smallestchild))) 
				return;
	    	swap(position,smallestchild);
	    	position = smallestchild;
		}
    }

	private float Score(Set<Integer> S) {
		float out = 0; 
		for (Integer i: S) {
			if ((int)i == -10000)
				return (float)-1.0;
			out += x[sorted_order[i]]; 
		}
		return out; 
	}
}

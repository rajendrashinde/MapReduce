package lsh;
import java.lang.Float; 
import java.util.HashMap;

public class Vector_sparse {
    public final int N;         // length of the vector
    public HashMap<Integer, Float> data;       // array of vector's components
	public double norm;
	 
    // create the zero vector of length N
    public Vector_sparse(int N) {
        this.N = N;
        this.data = new HashMap<Integer, Float> ();
		this.norm = 0.0; 
    }
    
	public Vector_sparse(String point_str, int d) {
		
		String[] junk = point_str.split(" ");
		this.N = d;
		this.norm = 0.0; 
		this.data = new HashMap<Integer, Float> ();
		for (int i = 0; i < junk.length; i++) {
			String[] dir_value = junk[i].split(","); 
			if (dir_value.length == 2){
				Integer dir = Integer.parseInt(dir_value[0]);
				float temp = Float.parseFloat(dir_value[1]); 
				this.data.put(dir, new Float(temp)) ;
				this.norm += temp*temp; 
				}
		//	else
		//	  System.out.println("split empty: "+ junk[i]);
		} 
	}
   
	public int dimension(){
		return this.N;
	}
}

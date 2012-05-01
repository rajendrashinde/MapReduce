package lsh;
import java.lang.Float; 

public class Vector {
    private final int N;         // length of the vector
    private double[] data;       // array of vector's components

    // create the zero vector of length N
    public Vector(int N) {
        this.N = N;
        this.data = new double[N];
    }
    
    public Vector(double[] data) {
        N = data.length;

        // defensive copy so that client can't alter our copy of data[]
        this.data = new double[N];
        for (int i = 0; i < N; i++)
            this.data[i] = data[i];
    }

    
    public Vector(String point_str) {
        String[] junk = point_str.split(" "); 
        N = junk.length;

        // defensive copy so that client can't alter our copy of data[]
        this.data = new double[N];
        for (int i = 0; i < N; i++)
            this.data[i] = new Float(junk[i]);
    }
    
	public Vector(String point_str, String mode, int d) {
		String[] junk = point_str.split(" ");
		this.N = d;
		this.data = new double[this.N];
		for (int i = 0; i < junk.length; i++) {
			String[] dir_value = junk[i].split(","); 
			if (dir_value.length == 2){
				int dir = Integer.parseInt(dir_value[0]);
				this.data[dir] = new Float(dir_value[1]);
				}
		//	else
		//	  System.out.println("split empty: "+ junk[i]);
		} 
	}
     
    public double dot(Vector that) {
        if (this.N != that.N) throw new RuntimeException("Dimensions don't agree");
        double sum = 0.0;
        for (int i = 0; i < N; i++)
            sum = sum + (this.data[i] * that.data[i]);
        return sum;
    }
    
    public double l2norm() {
        return Math.sqrt(this.dot(this));
    }
    
    public Vector plus(Vector that) {
        if (this.N != that.N) throw new RuntimeException("Dimensions don't agree");
        Vector c = new Vector(N);
        for (int i = 0; i < N; i++)
            c.data[i] = this.data[i] + that.data[i];
        return c;
    }

    public Vector minus(Vector that) {
        if (this.N != that.N) throw new RuntimeException("Dimensions don't agree");
        Vector c = new Vector(N);
        for (int i = 0; i < N; i++)
            c.data[i] = this.data[i] - that.data[i];
        return c;
    }
   
    public double distanceTo(Vector that) {
        if (this.N != that.N) throw new RuntimeException("Dimensions don't agree");
        return this.minus(that).l2norm();
    }
    
    public Vector times(double factor) {
        Vector c = new Vector(N);
        for (int i = 0; i < N; i++)
            c.data[i] = factor * data[i];
        return c;
    }
    
    public Vector normalize() {
        if (this.l2norm() == 0.0) throw new RuntimeException("Zero-vector has no direction");
        return this.times(1.0 / this.l2norm());
    }

    public String toString() {
        String s = "(";
        for (int i = 0; i < N; i++) {
            s += data[i];
            if (i < N-1) s+= ", "; 
        }
    return s + ")";
    }

	public int dimension(){
		return this.N;
	}
}

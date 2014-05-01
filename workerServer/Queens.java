    package client;

	import edu.harvard.cs262.ComputeServer.WorkTask;
	import java.io.Serializable;
	import java.lang.StringBuffer;
	import java.lang.String;

/**
 * A job sent to a {@link QueuedServer}, performed
 * on {@link ComputeEngine} servers.
 * It solves Queens puzzle. Typically it's eight queens puzzle 
 * -- placing 8 chess queens on an 8*8 board so that any two queens
 * could not attact each other. The problem here is extanded to be any
 * size.
 */

	public class Queens implements WorkTask,Serializable{
        
		private static final long serialVersionUID = 1;
        
        /** number of solutions */
        public static int num = 0;
        
        /** number of queens (the scale of the problem) */
        public final int QueenNum;
        
        /** keep record of the solutions */
        static int[] columns;
        
        /** buffer all the solutions */
		public StringBuffer output;
        
        
		/**
         * Construct a task to compute queens problem
         */
        public Queens(int Num) {
		  QueenNum = Num;
		 
        }
        
		/**
         * Wrapper of the initial recursive function
         */
		public void execute() {
          Try(0);  // Try to put the first queen
        } 
		
        /**
         * Start the work and return the final results
         */
		public String doWork() {
		
            columns = new int[QueenNum];
            output = new StringBuffer();
            
            execute();
			output.append("\n");  
            output.append("There is totally "+num+" solutions.\n"); 
			return output.toString();
		}
        
        
        /**
         * Compute queens problem recursively
         */
        public void Try(int n){
            
         boolean[] rows = new boolean[QueenNum];  
         for(int i=0;i<n;i++){  
            rows[columns[i]]=true;  
            int d = n-i;  
            if(columns[i]-d >= 0) rows[columns[i]-d]=true;  
            if(columns[i]+d <= QueenNum-1) rows[columns[i]+d]=true;              
         }  
         for(int i=0;i<QueenNum;i++){
           
           if(rows[i]) continue;
           columns[n] = i;  
           if(n<QueenNum-1) {  
             Try(n+1);  
           }else{   
             printBoard(); 
           }           
         }         
        }  
		
        /**
         * Save the solutions
         */
        public void printBoard(){
            System.out.println("a");
		   num++;
           output.append("Solution No."+num+" \n");             
           for(int i=0;i<QueenNum;i++){  
             for(int j=0;j<QueenNum;j++){  
               if(i==columns[j]){  
                 output.append("Q ");  
               }else  
                 output.append(". ");  
             }  
             output.append("\n");  
           }              
        }  

         
    }  
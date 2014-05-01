package workerServer;

import edu.harvard.cs262.ComputeServer.WorkTask;
import java.io.Serializable;
import java.math.BigDecimal;

public class Pi implements WorkTask, Serializable {

    private static final long serialVersionUID = 1;
    public static int num = 0;
    public int QueenNum;
    static int[] columns;
    public StringBuffer output;
    
    public Pi(int Num) {
        QueenNum = Num;
        columns = new int[QueenNum];
        output = new StringBuffer();
    }
    
    public void execute() {
        Try(0);  // Try to put the first queen
    }
    
    public String doWork() {
        execute();
        output.append("\n");
        output.append("There is totally "+num+" solutions.\n");
        return "succeed";
    }
    
    public void Try(int n){
        boolean[] rows = new boolean[QueenNum];
        System.out.println(n);
        System.out.println(QueenNum);
        System.out.println(columns[n]);
        for(int i=0;i<n;i++){
            rows[columns[i]]=true;
            int d = n-i;
            if(columns[i]-d >= 0) rows[columns[i]-d]=true;
            if(columns[i]+d <= QueenNum-1) rows[columns[i]+d]=true;
        }
        for(int i=0;i<QueenNum;i++){
            System.out.println(rows[i]);
            
            if(rows[i]) continue;
            
            columns[n] = i;
            if(n<QueenNum-1) {
                Try(n+1);
            }else{
                printBoard();
            }
        }
    }
    
    public void printBoard(){
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
    }  }

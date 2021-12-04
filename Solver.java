package Sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solver {
	private int GRID_SIZE;
	private int BOX_SIZE;
	private int[][] grid;
	private int numFilled;
	
	//hashmaps to show the possible numbers which could go in each row, col and box
    private final Map<Integer, Set<Integer>> rowOptions = new HashMap<>();
    private final Map<Integer, Set<Integer>> colOptions = new HashMap<>();
    private final Map<Integer, Set<Integer>> boxOptions = new HashMap<>();
    
    // Field that stores the same Sudoku puzzle solved in all possible ways
    public HashSet<Solver> solutions = new HashSet<Solver>();
	
	public Solver(int gridSize, int[][] pGrid) {
		GRID_SIZE=gridSize;
		BOX_SIZE=(int)Math.sqrt(gridSize);
		grid=pGrid;
	}
	
	//fills the col, row, and box hashmaps with their possible options
    private void getOptions() {
        for (int n = 0; n < GRID_SIZE; n++) { //initially let's say that a row/box/col can have any number
            colOptions.put(n, new HashSet<>(Arrays.asList()));
            rowOptions.put(n, new HashSet<>(Arrays.asList()));
            boxOptions.put(n, new HashSet<>(Arrays.asList()));
            //initially say all numbers are possible
            for (int num = 1; num <= GRID_SIZE; num++) {
                colOptions.get(n).add(num);
                rowOptions.get(n).add(num);
                boxOptions.get(n).add(num);
            }
        }
        
        //lok in each cell iof grid
        for (int i = 0; i < GRID_SIZE; i++) {//for all rows
            for (int j = 0; j < GRID_SIZE; j++) { //iterate through all cols
                int num = grid[i][j];
                if (num != 0) {//for every non empty cell
                    colOptions.get(j).remove(num);//remove that option from that col
                    rowOptions.get(i).remove(num);//remove that option from that col
                    //which box is this number in?
                    int boxNum = getBoxNum(i, j);
                    boxOptions.get(boxNum).remove(num);//remove that option from that box
                    //meanwhile update # filled counter
                    this.numFilled++;
                }
            }
        }
    }
    
  //looks at maps for row, col and box options
    //and returns an int[] of all possible #s cell could be
    private ArrayList<Integer> getCellOptions(int row, int col) {
        ArrayList<Integer> options = new ArrayList<>(9);
        for (Integer i : rowOptions.get(row)) {
            if (colOptions.get(col).contains(i) && (boxOptions.get(getBoxNum(row, col)).contains(i))) {
                options.add(i);
            }
        }
        return options;
    }
    
  //USES BACKTRACKING TO SOLVE PUZZLE!
    private boolean recurSolve(boolean allSolutions) {
        while (this.numFilled<(GRID_SIZE*GRID_SIZE)){
            int cell[] = pickCell();
            int row=cell[0];
            int col=cell[1];
            if (this.grid[row][col] == 0) { //if cell is empty
                ArrayList tempOptions = getCellOptions(row, col);
                for (Object num : tempOptions) {
                    //try adding numbers
                    updateGrid(row, col, (int) num);//add to grid and update maps
                    this.numFilled++;
                    if (recurSolve(allSolutions)) {//recursively do the next move
                        return true;//return true if all moves are valid
                    } else {
                        grid[row][col] = 0;//remove that number
                        //the number can once again be an option in that row/col/box
                        colOptions.get(col).add((int) num);
                        rowOptions.get(row).add((int) num);
                        boxOptions.get(getBoxNum(row, col)).add((int) num);
                        this.numFilled--;
                    }
                    
                }return false;//return false if no number works (you need to backtrack)
            }
        }//when you reached here, board is full
        if(!allSolutions) {//if you only need to find one soluion, then you are done
            return true;
        }else {
            //add sol to hashmap
            Solver p = new Solver(GRID_SIZE, new int[GRID_SIZE][GRID_SIZE]);
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    p.grid[row][col] = this.grid[row][col];//deep copy
                }
            }
            solutions.add(p);
            return false;//pretend its false and backtrack
        }
    }
    
    //quick method to add a cell to grid and update maps
    private void updateGrid(int row, int col, int num) {
        grid[row][col] = num;//add that num to grid
        //update row, col and box maps
        colOptions.get(col).remove(num);
        rowOptions.get(row).remove(num);
        boxOptions.get(getBoxNum(row, col)).remove(num);
    }
    
  //return the x and y coordinates of the cell you should do next
    private int[] pickCell(){
        //for each cell
        int minOptions=9;
        int bestRow=0;
        int bestCol=0;
        for (int row=0; row<GRID_SIZE; row++){
            for (int col=0; col<GRID_SIZE; col++){
                if (grid[row][col]==0){//if cell is empty
                    int optionCount=0;
                    //count the number of options the cell has
                    for (Integer i : rowOptions.get(row)) {
                        if (colOptions.get(col).contains(i) && (boxOptions.get(getBoxNum(row, col)).contains(i))) {
                            optionCount++;
                        }
                    }//compare to current best

                    if (optionCount<minOptions){
                        minOptions=optionCount;
                        bestRow=row;
                        bestCol=col;
                    }
                    if (optionCount==1){
                        break;
                    }
                }
            }
        }
        int move[] = new int[]{bestRow, bestCol};
        return move;
    }
    
    int getBoxNum(int height, int width) {
        int h, w;
        h = Math.floorDiv(height, BOX_SIZE);
        w = Math.floorDiv(width, BOX_SIZE);
        return (h * BOX_SIZE) + w;
    }
    
    //possible solutions and store them in the field named solutions. */
   	public int[][] solve(boolean allSolutions) {
	    if (this.numFilled == (GRID_SIZE*GRID_SIZE)){ 
	    	
	    	return this.grid;}//make sure the board isn't filled
	    getOptions();//fill the maps with all options for rows, cols, and boxes
	    recurSolve(allSolutions); //use recursive backtracking method to solve
	    if (allSolutions){//set the grid to the first one obtained
	           for (Solver o:this.solutions){
	               this.grid=o.grid;
	               break;
	           }
         }
	    return this.grid;
   	}
   	
   	public void print() {
        // Compute the number of digits necessary to print out each number in the Sudoku puzzle
        int digits = (int) Math.floor(Math.log(GRID_SIZE) / Math.log(10)) + 1;

        // Create a dashed line to separate the boxes
        int lineLength = (digits + 1) * GRID_SIZE + 2 * BOX_SIZE - 3;
        StringBuffer line = new StringBuffer();
        for( int lineInit = 0; lineInit < lineLength; lineInit++ )
            line.append('-');

        // Go through the grid, printing out its values separated by spaces
        for( int i = 0; i < GRID_SIZE; i++ ) {
            for( int j = 0; j < GRID_SIZE; j++ ) {
                printFixedWidth( String.valueOf( grid[i][j] ), digits );
                // Print the vertical lines between boxes
                if( (j < GRID_SIZE-1) && ((j+1) % BOX_SIZE == 0) )
                    System.out.print( " |" );
                System.out.print( " " );
            }
            System.out.println();

            // Print the horizontal line between boxes
            if( (i < GRID_SIZE-1) && ((i+1) % BOX_SIZE == 0) )
                System.out.println( line.toString() );
        }
    }
   
	private void printFixedWidth( String text, int width ) {
        for( int i = 0; i < width - text.length(); i++ )
            System.out.print( " " );
        System.out.print( text );
    }
  
	

}

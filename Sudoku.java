package Sudoku;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class Sudoku extends Application{
	GridPane grid = new GridPane();
	private final static int GRID_SIZE=9;
	private final int BOX_SIZE=(int)Math.sqrt(GRID_SIZE);
	Map<Integer,TextField> aNumbers=new HashMap<>();
	private LightMode aLightMode = LightMode.DAY;
	
	@Override
	public void start(Stage pStage) throws Exception {
		
		for (int i=0; i<GRID_SIZE; i++) {
			if (i%3==0) {
				Rectangle hLine = new Rectangle();
				hLine.setHeight(0.5);
				grid.add(hLine, i, i);
				
			}
			for (int j=0; j<GRID_SIZE; j++) {
				HBox box = new HBox();
				TextField input = new TextField();
				input.setAlignment(Pos.CENTER);//Align text to center
				
				setTextFieldColour(input, i, j);
				
				input.setPrefWidth(30);//Set width
				box.getChildren().add(input);
				aNumbers.put(getID(i,j),input);
				grid.add(input, i, j);
				
			}
		}
		
		Button solveButton=new Button("Solve");
		Button clearButton = new Button("Clear");
		Button nightOrDay = new Button();
		grid.add(solveButton, GRID_SIZE, 0);
		grid.add(clearButton, GRID_SIZE, 1);
		grid.add(nightOrDay, GRID_SIZE, 9);
		Scene scene = new Scene(grid);
		grid.setStyle("-fx-background-color: WHITE");
		ImageView icon = new ImageView("file:///C:/Users/User/eclipse-workspace/Sudoku/src/Sudoku/nightorday.JPG");
		//icon.setScaleX(0.5);
		//icon.setScaleY(0.5);
		nightOrDay.setGraphic(icon);
		nightOrDay.setScaleX(0.5);
		nightOrDay.setScaleY(0.5);
		pStage.setTitle("Sudoku Cheater");
		pStage.getIcons().add(new Image("file:///C:/Users/User/eclipse-workspace/Sudoku/src/Sudoku/SC.png"));
		pStage.setScene(scene);
		pStage.show();
		
		solveButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				int[][] unsolved = new int[GRID_SIZE][GRID_SIZE];
				
				for (int i=0; i<GRID_SIZE; i++) {
					for (int j=0; j<GRID_SIZE; j++) {
						String num = aNumbers.get(getID(i,j)).getText();
						if (isValidNumber(num)) {
							unsolved[i][j]=Integer.parseInt(num);
						}else {
							unsolved[i][j]=0;
						}
					}
				}
				Solver solver=new Solver(GRID_SIZE,unsolved);
				int[][] solution = solver.solve(false);
				
				for (int i=0; i<GRID_SIZE; i++) {
					for (int j=0; j<GRID_SIZE; j++) {
						aNumbers.get(getID(i,j)).setText(Integer.toString(solution[i][j]));
					}
				}
			}
			
		});
		
		nightOrDay.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent pEvent) {
				if (aLightMode == LightMode.DAY) {
					aLightMode=LightMode.NIGHT;
					grid.setStyle("-fx-background-color: DARKSLATEGREY");
					
				}else {//then switch to night mode
					aLightMode=LightMode.DAY;
					grid.setStyle("-fx-background-color: WHITE");
				}
				
				
				for (int i=0; i<GRID_SIZE; i++) {
					for (int j=0; j<GRID_SIZE; j++) {
						TextField cell = aNumbers.get(getID(i,j));
						setTextFieldColour(cell, i,j);
					}
				}
				
				
			}
			
		});
		//CLEAR BUTTON
		clearButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				for (TextField num : aNumbers.values()) {
					num.setText("");
				}
				
			}
			
		});
		
		
		
	}
	
	private String[] getModeColours() {
		if (aLightMode == LightMode.DAY) {
			return new String[] {"LIGHTBLUE","LIGHTCYAN"};
		}else {
			return new String[] {"NAVY","DARKSLATEBLUE"};
		}
	}

	private void setTextFieldColour(TextField field, int i, int j) {
		String[] colours = getModeColours();
		if (getBoxNum(i,j)%2==0) {
			field.setStyle("-fx-control-inner-background: "+colours[0]);
		}else {
			field.setStyle("-fx-control-inner-background: "+colours[1]);
		}
	}
	
	private int getID(int i, int j) {
		return GRID_SIZE*i + j;
	}
	private static boolean isValidNumber(String pNum) {
	    if (pNum == null) {
	        return false;}
	    try {
	        double aDigit = Double.parseDouble(pNum);
	        if ((int) aDigit > 0 && (int) aDigit <= GRID_SIZE) {
	        	return true;
	        }else {
	        	return false;
	        }
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	}
	
	private int getBoxNum(int width, int height) {
        int h, w;
        w = Math.floorDiv(height, BOX_SIZE);
        h = Math.floorDiv(width, BOX_SIZE);
        return (h * GRID_SIZE) + w;
    }
	
	public static void main(String[] args) {
		launch(args);
	}

}

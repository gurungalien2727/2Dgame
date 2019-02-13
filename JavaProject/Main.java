
import javax.swing.JFrame;

public class Main {
	
	
  public static void main(String[] args) {
	
	 
	 JFrame frame=new JFrame("Java Game");
	 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 
	 Game obj=new Game();
		
	 frame.getContentPane().add(obj);
	 
	 frame.pack();
	 frame.setVisible(true);
	 frame.setResizable(false);
	
	 
	}
  
  
}
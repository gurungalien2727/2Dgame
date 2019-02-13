
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;

import java.util.ArrayList;
import java.util.Random;

import java.io.File;
import javax.imageio.ImageIO;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;


public class Game extends JPanel  {

	public final int WIDTH = 800;                                     //Width of the frame
	public final int HEIGHT = 700;                                    //Height of the frame
	private final int DELAY = 20;                                      //Setting the timer
	
	private final int cloudCount = 5;
	private final int cloudDistance = 200;
	private final int easyScore = 300;                                //value used to change the level to medium
	private final int mediumScore = 600;                              //value used to change the level to hard
	private boolean start = false; 
	private boolean gameover = false;

	private Rectangle character;              
	private Rectangle groundChar;
	private Timer timer;
	private ArrayList<Rectangle> cloud;
	private ArrayList<Integer> direction;
	private Random rand;
	
	private BufferedImage emo;                                        // Image kept over character
	private BufferedImage devil;                                      //Image kept over cloud
	private BufferedImage mute ;                                      //Image for mute 
	private BufferedImage volume;                                    //Image for volume
	private BufferedImage ground;                                     //Ground Image
    
	private int buttonStart ;
	private int life;
	private int loss;
	private int score, tick;
	private int highScore;
	private int speed;

	private JButton button1;
	private JButton button2;
	private JButton button3;
	
	private Clip clip;

	
	public Game()                                                     //Constructor   
	{
		timer = new Timer(DELAY, new Action());                       //Creating a timer object with parameter of an action listener object which will execute actionPerformed method when timer starts
		addKeyListener(new Key());                                    // Adding  a listener
		this.setLayout(null);

		life=150;                                                     //total life in the beginning
		loss = 3;                                                     // decreases the life by 3 after intersecting the devil character
		speed = 5;                                                    // speed of the game in the easy level
		highScore = 0;                                               // stores the highest scores as long as the user exits from the game
		
		
		// Could throw an IOE exception so, wrote the code in the try block followed by a catch block 
			try{
			      emo = ImageIO.read(new File("happyFace.gif"));
			      devil = ImageIO.read(new File("devil.gif"));
			      mute = ImageIO.read(new File("mute.png"));
			      volume = ImageIO.read(new File("volume.png"));	
			      ground = ImageIO.read(new File("Groung.png"));
	          } 
			
		 catch (IOException e) {
				 System.out.println("Exception");	
		      }

		button1 = new JButton("Start Game");                         //Creating a button object
		button1.setBounds(WIDTH / 3 + 40, HEIGHT / 3, 150, 40);      // adjusting the position of a button
		add(button1);                                                // adding button to a panel
		button1.addActionListener(new Action());                     //passing an action listener object when button 1 is clicked
		               
		button2 = new JButton("Quit Game");                          //Creating another button object
		button2.setBounds(WIDTH / 3 + 40, HEIGHT / 3 + 45, 150, 40); //adjusting the position of a button
		add(button2);                                                // adding button to a panel
		button2.addActionListener(new Action());                     // passing an action listener object when button 2 is clicked
		
        button3 = new JButton ("MUTE");                             // Creating another button object
		button3.setBounds(WIDTH - 60 , HEIGHT - 70 , 50 , 50);      // adjusting the position of a button
		button3.addActionListener(new Action());                    // passing an action listener object when button 3 is clicked
		button3.setBorderPainted(false);
		button3.setContentAreaFilled(false);
		button3.setIcon(new ImageIcon(volume));                     //Setting the image
		add(button3);                                               //Adding button to a panel
		
		rand = new Random();                                       //Creating an object of a random class
		cloud = new ArrayList<Rectangle>();                        
		direction = new ArrayList<Integer>();
		character = new Rectangle(200, 220, 18, 18);
	    groundChar = new Rectangle(0, HEIGHT-140, WIDTH, 150);
	    
		for (int i = 0; i < cloudCount; i++) {
			cloud.add(new Rectangle(WIDTH + 100 + cloud.size() * cloudDistance, rand.nextInt(HEIGHT - 120), 80, 62));
			direction.add(rand.nextInt(2)-1);
		}
		
		
		File soundFile = new File("music.wav");   // Creating an object of a file
		//Could throw an exception so  wrote the code in the try block followed by catch block
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(soundFile);
		    DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
		    clip = (Clip) AudioSystem.getLine(info);
		    clip.open(sound);   
		}
		catch(Exception e)
		{
			
			 System.out.println(e);
			 
		} 
		 
	    clip.start();                                                                   // starts the sound

		setPreferredSize(new Dimension(WIDTH, HEIGHT));                                 // Setting the size of the panel as well as the frame
		setBackground(new Color(25, 191, 224));                                         //Setting the background color of the panel
		setFocusable(true);
        
		timer.start();                                                                 //Starts the timer
	}
	
	
  
	
	public void paintComponent(Graphics g) {
		
		
		super.paintComponent(g);                                                         // called to prevent visual artifacts                            
		requestFocus(true);                                                             //To make the component get input focus

		g.setColor(Color.cyan);                                                          
		g.fillRect(character.x, character.y, character.width, character.height);         //Drawing a rectangle box
		g.drawImage(emo, character.x - 8, character.y - 4, this);                        //Putting our main character over the box
		
		g.setColor(new Color(102, 51, 0));
		g.fillRect(groundChar.x, groundChar.y+20, groundChar.width, groundChar.height);
		g.drawImage(ground, groundChar.x - 8, groundChar.y - 4, this);                   //adding the image for the ground
 
		if (character.y >= HEIGHT - 100 || character.y < 20) {                           // when the character is too high or too low then the character dies
			gameover = true; 
			life = 0;                                                                     //Setting the life to zero
			buttonStart = 0;                                                              //setting the value to zero so that the game would not start with the space key
		}

		for (Rectangle rect : cloud) {
			g.setColor(new Color(25, 191, 224));
			g.setColor(Color.RED);
			g.fillOval(rect.x, rect.y, rect.width, rect.height);                          // Drawing oval shapes
			g.drawImage(devil, rect.x, rect.y, this);                                     // Putting the image of devil over the oval
		}

		g.setColor(Color.yellow);

		if (!start) {
			g.setFont(new Font("Arial", 1, 50));
			g.drawString("Press Space To Jump ", WIDTH - 680, HEIGHT / 3 + 230);          //Guiding the user

		}
		
		if (gameover) {
			add(button1);                                                                  //Button appears when the game is over
			add(button2);                                                                   //Button appears when the game is over
			g.setFont(new Font("Arial", 1, 40));
			g.drawString(" Game Over ", 260, HEIGHT / 4 -40 );
			g.drawString("Your Score: " + score, 260 , HEIGHT / 4 + 20);
			g.setFont(new Font("Arial", 1, 35));
		}

		g.setColor(Color.BLACK);                   
		g.fillRect(0, 0, 900, 30);                                                          //Creating a rectangle on the top of the frame
		
		g.setColor(Color.WHITE);

		g.setFont(new Font("Arial", 1, 14)); 
		g.drawString("Score: " + String.valueOf(score), 20, 20);                           //Shows the current score
		
//Change background color and speed following level
		if (score <= easyScore) {
			g.drawString("Level: Easy", 200, 20);
			setBackground(new Color(25, 191, 224));
		}
		else if (score > easyScore && score < mediumScore) {
			speed = 8;                                                                     //increasing the speed to 4 after passing the easy score
			g.drawString("Level: Medium", 200, 20);
			loss = 2;
		  setBackground(new Color(233, 13, 224));                                          // changing the color of the background in the medium level
		}
		else {
			speed = 10;                                                                    //increasing the speed to 5 after passing the medium score
			g.drawString("Level: Hard", 200, 20);
			loss = 2;
			setBackground(new Color(150, 20, 224));                                        // changing the color of the background in the difficult level
		}  
		
	
		g.drawString("High Score: " + highScore, 400, 20);                                  // Displays the highest score               
		g.drawString("Life ", 800, 20);                                 
		g.setColor(Color.GREEN);
		g.fillRect(628, 5, life, 20);                                                       //Draws the life bar
	} 

	

	
	public void flap()                                                                       // Causes the character to jump                                 
	{

		if (gameover) {
			
			character.y = 300;
			character.x = 200;

			for (int i = 0; i < cloudCount; i++) {
				Rectangle rect = cloud.get(i);
				rect.x += WIDTH + 100 + i * cloudDistance;
			}

			gameover = false;

			if (score > highScore) {
				highScore = score;
			}
			score = 0;
			speed = 5;
			life = 150;
			
               if(clip.isRunning()) {
				
			  
				           button3.setIcon(new ImageIcon(volume));
				
			           }
			   else {
				
				           button3.setIcon(new ImageIcon(mute));
				
					}
		
		}

		if (!start) {
			start = true;
		}
		else if (!gameover)                                                                // Sets the character up by 70           
		{
			character.y -= 70;
			tick = 0;
		}

		if (!gameover) {
			for (Rectangle rect : cloud) {
				if (character.x >= rect.x) {
					score += 10;
				}
			}
		}

		remove(button1);                                                                  // Removing button 1 after game starts
		remove(button2);                                                                   // Removing button 2 after game starts
		
	}
	
	
	
	

 private class Key implements KeyListener                                                  // Implementing  a Key listener interface 
 
 {
	 // Defining all three  methods of the KeyListener Interface
	 	 public void keyPressed(KeyEvent e) 
	 	 {
		 
		 
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				if (buttonStart == 1) {
					flap();
					
					
				}
			}
		}
	 
	 
		
	public void keyTyped(KeyEvent e) { }
    
	
	public void keyReleased(KeyEvent e) { }
	
 }
 
 
    
 
 private class Action implements ActionListener                                        //Implementing an ActionListener Interface
 {
	 
	public void actionPerformed(ActionEvent e)                                         // Defining  one and only method of ActionListener Interface
	
	{
		
		if (e.getSource() == button1) {
			
			flap();
			buttonStart = 1;
				
		}

		if (e.getSource() == button2) {
			
			System.exit(0);              
		}
		
    //Mute and volume button
		if (e.getSource() == button3) {
			
			if(clip.isRunning()) {
				
			    clip.stop();
				button3.setIcon(new ImageIcon(mute));
				
			}
			  else {
				  
			    clip.start();
				button3.setIcon(new ImageIcon(volume));
				
					}
		}
		
		if (start) {
			
			for(int i = 0; i < cloudCount; i++) {
				Rectangle rect = cloud.get(i);
				
	         //Cloud moving
	         //speed * 1(true) is moving up
	         //speed * -1(false) is moving down
				
				if(rect.y < 30) {			
					direction.set(i, -1);
				} 
				  
				else if (rect.y > 460) {
					direction.set(i, 1);	
				}

				rect.x -= speed;
				rect.y -= speed * (direction.get(i));

				if (rect.intersects(character)) {
					life -= loss;
					if (life <= 0) {
						gameover = true;
						  buttonStart = 0;
						character.x -= speed;
						}
					}
				
				if (rect.x + rect.width < 0) {
					rect.x += cloudCount * cloudDistance;
					rect.y = rand.nextInt(HEIGHT - 120);
				}
				
			}

			tick++;

			if (tick % 2 == 0 && character.y < HEIGHT - 100)
				character.y += tick;

			if (gameover && character.y >= HEIGHT - 100) {
				character.x -= speed;
			}
		}
		repaint();                                                                                  //Calls the paintComponent method
	}
}
}
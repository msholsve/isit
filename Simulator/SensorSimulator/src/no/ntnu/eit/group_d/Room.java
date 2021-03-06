package no.ntnu.eit.group_d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Room extends JComponent {
	private JButton startButton;
	private JButton refreshButton;
	private JPanel seatsPanel;
	private Seat[] seats;
	
	private Thread simulation;
	private SeatChangeSimulation simul;
	
	private String id;
	private String roomUrl;
	
	public Room(String roomUrl) {
		this.setLayout(new BorderLayout());
		this.seatsPanel = new JPanel();
		this.seatsPanel.setLayout(new FlowLayout());
        	
		loadSeats(roomUrl);
			        
        this.add(seatsPanel, BorderLayout.CENTER);
        
        this.refreshButton = new JButton("Refresh");
        this.startButton = new JButton("Start simulation");
        this.startButton.addActionListener(new StartButtonListener());
        this.startButton.setBackground(Color.GREEN);
        seatsPanel.add(startButton, BorderLayout.PAGE_END);
                      
	}
	
	private class CheckBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Seat seat = (Seat)e.getSource();
			
			if(seat.isSelected())
				seat.setBackground(Color.PINK);
			else
				seat.setBackground(null);
						
			String data = "{ \"free\": ";
			data += seat.isSelected() ? "false" : "true";
			data += " }";
			
			byte[] bytes = data.getBytes();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			for(int i = 0; i < bytes.length; i++) {
				baos.write(bytes[i]);
			}
						
			HttpHelper.patchHttp("http://isit.routable.org/api/seats/" + seat.getId(), baos);
			
			seatsPanel.repaint();
		}
	}
	
	private void loadSeats(String url) {
		try {
			JSONParser parser = new JSONParser();
			String roomData = HttpHelper.getHttp(url + "?projection={\"map\":0}");
			JSONObject roomsObj = (JSONObject)parser.parse(roomData);
			this.id = (String)roomsObj.get("_id");
			this.roomUrl = url;
			
			JSONArray seatJsons = (JSONArray)roomsObj.get("seats");
			
			if(seatJsons != null) {
				seats = new Seat[seatJsons.size()];	
		        for(int i = 0; i < seatJsons.size(); i++) {
			    	this.seats[i] = new Seat((JSONObject)seatJsons.get(i));
			    	this.seats[i].addActionListener(new CheckBoxListener());
			    	this.seatsPanel.add(this.seats[i]);	
		        }
			} else {
				this.seatsPanel.add(new JLabel("No seats in this room"));
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
       
	}

	
	private class StartButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(simulation == null || simulation.isAlive() == false) {
				
				simul = new SeatChangeSimulation(seats);
				simulation = new Thread(simul);
				startButton.setText("Stop simulation");
				startButton.setBackground(Color.PINK);
				simulation.start();
				
			} else {
				startButton.setText("Start simulation");
				startButton.setBackground(Color.GREEN);
				simul.stopAsync();
				try {
					simulation.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
	}
}

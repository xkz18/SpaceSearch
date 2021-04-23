package GUI;

import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

public class SimManager implements Serializable{

    private Integer numberOfDrones;
    
    private Drone[] drones;
    private SpaceRegion space;
    
    private HashMap<String, Integer> xDIR_MAP = new HashMap<>();
    private HashMap<String, Integer> yDIR_MAP = new HashMap<>();
    
    private int trackTurnsCompleted = 0;
    private int turnLimit;    
    private int curDrone = -1;

    private String droneStatus;
    private String trackAction;




	public Drone getCurDrone() {
		return this.drones[(this.curDrone == -1 ? (numberOfDrones - 1) : this.curDrone)];
	}

	public int getCurDroneIdForUi() {return (this.curDrone == -1 ? numberOfDrones - 1 : this.curDrone);}

    public int getCurDroneId() {return this.curDrone;}
    
	public int getTrackTurnsCompleted() {
		return trackTurnsCompleted;
	}

	public int getTurnLimit() {
		return turnLimit;
	}

	public void setTurnLimit(Integer turnLimit) {
		this.turnLimit = turnLimit;
	}

    public String getDroneStatus() {
        return this.droneStatus;
    }
    public String GetTrackAction() {return this.trackAction;}


    public SimManager() {    	
    	xDIR_MAP = Drone.xDIR_MAP;
    	yDIR_MAP = Drone.yDIR_MAP;
    }
    
    public void uploadStartingFile(File testFile) {
        final String DELIMITER = ",";
        trackTurnsCompleted = 0;
        curDrone = -1;

        try {
            Scanner takeCommand = new Scanner(testFile);
            String[] tokens;
            int k;

            // read in the region information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int regionWidth = Integer.parseInt(tokens[0]);
            
            tokens = takeCommand.nextLine().split(DELIMITER);
            int regionHeight = Integer.parseInt(tokens[0]);
            space = new SpaceRegion(regionWidth, regionHeight);
            
            // read in the drone starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            numberOfDrones = Integer.parseInt(tokens[0]);
            drones = new Drone[numberOfDrones];

            for (k = 0; k < numberOfDrones; k++) {
            	Drone drone = new Drone(space);
                tokens = takeCommand.nextLine().split(DELIMITER);
                drone.setDroneX(Integer.parseInt(tokens[0]));
                drone.setDroneY(Integer.parseInt(tokens[1]));
                drone.setDroneDirection(tokens[2]);
                drone.setDroneStrategy(Integer.parseInt(tokens[3]));
                drone.setDroneEnergyCapacity(Integer.parseInt(tokens[4]));
                drone.setDroneEnergyLevel(Integer.parseInt(tokens[4]));
                drone.setDroneRechargeRate(Integer.parseInt(tokens[5]));
                space.setDrone(drone.getDroneX(), drone.getDroneY(), k);
                drones[k] = drone;
            }
            //
            droneDirection= new String[numberOfDrones];
            for(int j = 0; j < numberOfDrones; j++) {
                droneDirection[j] = "0";
            }

            // read in the sun information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numSuns = Integer.parseInt(tokens[0]);
            
            for (k = 0; k < numSuns; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                // place a sun at the given location
                space.setSun(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
           }

            tokens = takeCommand.nextLine().split(DELIMITER);
            turnLimit = Integer.parseInt(tokens[0]);

            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }

        
    }

	public void renderSpaceRegion() {    	
        space.renderRegion();

    }

    // get region info for graphPanel
    public String[][] getRegionInfo() {
	    return space.getRegionInfo();
    }
    public int getWidth() {
	    return space.getWidth();
    }
    public int getHeight() {
        return space.getHeight();
    }

    // get drone direction for graphPanel

    private String[] droneDirection;

	public void setDroneDirection_() {
        for(int k = 0; k < numberOfDrones; k++) {
            if (!drones[k].isAlive()) {
                droneDirection[k] = "0";
            } else {
                droneDirection[k] = drones[k].getDroneDirection();
            }
        }
    }
    public String[] getDroneDirection_() {
        return this.droneDirection;
    }

    /*
    public String[] getDroneDirection_() {
        for(int k = 0; k < numberOfDrones; k++) {
             if (!drones[k].isAlive()) {
                 droneDirection[k] = "0";
             } else {
                 droneDirection[k] = drones[k].getDroneDirection();
            }
        }
	    return droneDirection;
	}
    */


    public void renderDroneStatus() {
        // display the column X-direction identifiers
        System.out.print(" ");
        for (int i = 0; i < space.getWidth() ; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the drone's directions
        StringBuilder sb = new StringBuilder();
        for(int k = 0; k < numberOfDrones; k++) {
            if (!drones[k].isAlive()) { continue; }
            String cur = String.format("d%d   direction:%-10s energy:%d", k, drones[k].getDroneDirection(),drones[k].getDroneEnergyLevel());
            sb.append(cur);
            sb.append("\n");
        }

        // get next drone
        int curID =  this.curDrone + 1;
        curID = curID ==  numberOfDrones ? numberOfDrones - 1 : curID;
        for(int k = curID; k < numberOfDrones; k++) {
            if (!drones[k].isAlive()) { continue; }
            sb.append(String.format("Next Drone Id: %d", k));
            break;
        }

        this.droneStatus = sb.toString();
        System.out.println(sb.toString());
    }
    
    public void renderSimStatus(int k) {
    	System.out.println("d" + String.valueOf(k) + " in action");
    	System.out.println("turn limit:" + turnLimit);
    	System.out.println("turn completed:" + trackTurnsCompleted);	
    }





    public Integer droneCount() {
        return numberOfDrones;
    }
    
    public Boolean executeDroneAction(int id) {
    	curDrone = id;
    	if(droneInactive(id)) {
        	if(id == numberOfDrones - 1) {
        		trackTurnsCompleted += 1;
        		curDrone = -1;
        	}
        	return false;
    	}
    	Drone drone = drones[id];
    	String[] proposedAction = drone.tick();
    	
    	String trackMoveCheck = "ok";
    	String trackScanResults = "";
    	
    	trackAction = proposedAction[0];
    	String actionDetail = proposedAction[1];
        
    	int x = drone.getDroneX();
        int y = drone.getDroneY();
    	
        int xOrientation, yOrientation;

        if (trackAction.equals("scan")) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a north bound orientation
        	trackScanResults = scanAroundSquare(x, y);
        	trackMoveCheck = "ok";

        } else if (trackAction.equals("pass")) {
            trackMoveCheck = "ok";
            trackScanResults = scanAroundSquare(x, y);
            if(trackScanResults.contains("sun"))
            	drone.chargeEnergy();
        } else if (trackAction.equals("steer")) {
            drone.setDroneDirection(actionDetail);
            trackMoveCheck = "ok";

        } else if (trackAction.equals("thrust")) {
            // in the case of a thrust, ensure that the move doesn't cross suns or barriers
            xOrientation = xDIR_MAP.get(drone.getDroneDirection());
            yOrientation = yDIR_MAP.get(drone.getDroneDirection());

            trackMoveCheck = "ok";
            int remainingThrust = Integer.parseInt(actionDetail);

            while (remainingThrust > 0) {

                int newSquareX = Math.floorMod(drone.getDroneX() + xOrientation, space.getWidth());
                int newSquareY = Math.floorMod(drone.getDroneY() + yOrientation, space.getHeight());
                
                // Update the old square where drone used to occupy to empty
                space.setEmpty(drone.getDroneX(), drone.getDroneY());

                if (space.isSun(newSquareX, newSquareY)) {
                    drone.kill();
                    trackMoveCheck = "crash";
                    break;

                } else if (space.isDrone(newSquareX, newSquareY)) {
                	// Kill both drones
                	drones[space.getDrone(newSquareX, newSquareY)].kill();
                	drone.kill();
                	space.setEmpty(newSquareX, newSquareY);
                    trackMoveCheck = "crash";
                    break;

                } else if (space.isEmpty(newSquareX, newSquareY) || space.isStars(newSquareX, newSquareY)) {
                	space.setDrone(newSquareX, newSquareY, id);
                	drone.setDroneX(newSquareX);
                	drone.setDroneY(newSquareY);
                }
                
                
                remainingThrust = remainingThrust - 1;
            }

        } else {
            // in the case of an unknown action, treat the action as a pass
            trackMoveCheck = "action_not_recognized";
        }
        
        displayAction(id, trackAction, actionDetail);
        displayResponse(trackAction, trackMoveCheck, trackScanResults);
        
    	if(id == numberOfDrones - 1) {
    		trackTurnsCompleted += 1;
    		curDrone = -1;
    	}
    	return true;
    }
    
    
	private String scanAroundSquare(int targetX, int targetY) {
        String nextSquare, resultString = "";
     
        for (int k = 0; k < Drone.ORIENT_LIST.length; k++) {
            String lookThisWay = Drone.ORIENT_LIST[k];
            int offsetX = xDIR_MAP.get(lookThisWay);
            int offsetY = yDIR_MAP.get(lookThisWay);

            int checkX = targetX + offsetX;
            int checkY = targetY + offsetY;
            nextSquare = space.getRegionInfo(Math.floorMod(checkX, space.getWidth()), Math.floorMod(checkY, space.getHeight()));

            if (resultString.isEmpty()) { resultString = nextSquare; }
            else { resultString = resultString + "," + nextSquare; }
        }

        return resultString;
    }
	
	public void displayAction(int id, String trackAction, String actionDetail) {
        // display the drone's actions
		System.out.print("d" + String.valueOf(id) + "," + trackAction);
        if (trackAction.equals("steer") || trackAction.equals("thrust")) {
            System.out.println("," + actionDetail);
        } else {
            System.out.println();
        }
	}

	
    public void displayResponse(String trackAction, String trackMoveCheck, String trackScanResults) {
        // display the simulation checks and/or responses
        if (trackAction.equals("thrust") || trackAction.equals("steer") || trackAction.equals("pass")) {
            System.out.println(trackMoveCheck);
        } else if (trackAction.equals("scan")) {
            System.out.println(trackScanResults);
        } else {
            System.out.println("action_not_recognized");
        }
    }
    
    private Boolean dronesAllStopped() {
        for(int k = 0; k < numberOfDrones; k++) {
            if (!droneInactive(k)) { return Boolean.FALSE; }
        }
        return Boolean.TRUE;
    }
       
    private Boolean regionAllExplored() {
    	int width = space.getWidth();
    	int height = space.getHeight();
    	
    	for(int i = 0; i < width; i++) {
    		for (int j = 0; j < height; j++) {
    			if(space.isStars(i, j))
    				return false;
    		}
    	}
    	return true;	
    }
    
    private Boolean turnLimitExceeded() {
    	return trackTurnsCompleted >= turnLimit;
    }
    
    public Boolean droneCrashed(int id) {
    	return !drones[id].isAlive();
    }
    
    public Boolean droneDrained(int id) {
    	return drones[id].isDrained();
    }
    
    public Boolean droneInactive(int id) {
    	return droneDrained(id) || droneCrashed(id);
    }

    

    public Boolean isTerminated() {
    	if(dronesAllStopped() || regionAllExplored() || turnLimitExceeded()) 
    		return true;
    	return false;
    }
       
    public String finalReport() {
        int regionSize = space.getWidth() * space.getHeight();
        int numSuns = 0;
        int numStars = 0;
        for (int i = 0; i < space.getWidth(); i++) {
            for (int j = 0; j < space.getHeight(); j++) {
                if (space.isSun(i, j)) { numSuns++; }
                if (space.isStars(i, j)) { numStars++; }
            }
        }
        int potentialCut = regionSize - numSuns;
        int actualCut = potentialCut - numStars;

        String result = "Region Size: " + String.valueOf(regionSize) + ", explorable squares:" + String.valueOf(potentialCut) + ", Explored Square:" + String.valueOf(actualCut) + ", Turn Completed:" + String.valueOf(trackTurnsCompleted);
        return result;
    }
}
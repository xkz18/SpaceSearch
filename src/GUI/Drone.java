package GUI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Drone implements Serializable{
    private Integer droneStrategy;
    private String droneDirection;
    private Integer droneRechargeRate;
    private Integer droneEnergyCapacity;
    private Integer droneEnergyLevel;
    private Integer droneStatus;
    private Integer droneX;
	private Integer droneY;
    private static Random randGenerator;
    private String trackAction;
    private SpaceRegion partialSpace;
    
    private final int OK_CODE = 1;
    private final int CRASH_CODE = -1;
    
    private final int RANDOM_STRATEGY = 0;
    private final int OPTIMIZED_STRATEGY = 1;
    private final int MANUAL_STRATEGY = 2;
    
    public static final String[] ORIENT_LIST = {"north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest"};
    private final String[] THRUST_DISTANCE = {"1", "2", "3"};
    
    public final static HashMap<String, Integer> xDIR_MAP;
    public final static HashMap<String, Integer> yDIR_MAP;
    
    static {
    	xDIR_MAP = new HashMap<>();
    	yDIR_MAP = new HashMap<>();
    	xDIR_MAP.put("north", 0);
    	xDIR_MAP.put("northeast", 1);
    	xDIR_MAP.put("east", 1);
    	xDIR_MAP.put("southeast", 1);
    	xDIR_MAP.put("south", 0);
    	xDIR_MAP.put("southwest", -1);
    	xDIR_MAP.put("west", -1);
    	xDIR_MAP.put("northwest", -1);

    	yDIR_MAP.put("north", 1);
    	yDIR_MAP.put("northeast", 1);
    	yDIR_MAP.put("east", 0);
    	yDIR_MAP.put("southeast", -1);
    	yDIR_MAP.put("south", -1);
    	yDIR_MAP.put("southwest", -1);
    	yDIR_MAP.put("west", 0);
    	yDIR_MAP.put("northwest", 1);
    }
    
	public Integer getDroneEnergyCapacity() {
		return droneEnergyCapacity;
	}

	public void setDroneEnergyCapacity(Integer droneEnergyCapacity) {
		this.droneEnergyCapacity = droneEnergyCapacity;
	}

    public Integer getDroneRechargeRate() {
		return droneRechargeRate;
	}

	public void setDroneRechargeRate(Integer droneRechargeRate) {
		this.droneRechargeRate = droneRechargeRate;
	}

	public Integer getDroneEnergyLevel() {
		return droneEnergyLevel;
	}
	
	public void setDroneEnergyLevel(Integer droneEnergyLevel) {
		this.droneEnergyLevel = droneEnergyLevel;
	}

	public String getDroneDirection() {
		return droneDirection;
	}

	public void setDroneDirection(String droneDirection) {
		this.droneDirection = droneDirection;
	}

	public Integer getDroneX() {
		return droneX;
	}

	public void setDroneX(Integer droneX) {
		this.droneX = droneX;
	}

	public Integer getDroneY() {
		return droneY;
	}

	public void setDroneY(Integer droneY) {
		this.droneY = droneY;
	}

    public Integer getDroneStrategy() {
		return droneStrategy;
	}

	public void setDroneStrategy(Integer droneStrategy) {
		this.droneStrategy = droneStrategy;
	}

	public Drone(SpaceRegion space) {
		randGenerator = new Random();    	
    	droneStatus = OK_CODE;
    	partialSpace = space;
    }

	public Integer chargeEnergy() {
		droneEnergyLevel = Math.min(droneEnergyCapacity, droneEnergyLevel + droneRechargeRate);
		return droneEnergyLevel;
	}
	
	public void kill() {
		droneStatus = CRASH_CODE;
	}
	
	public boolean isAlive() {
		return droneStatus.equals(OK_CODE);
	}
	
	public boolean isDrained() {
		return droneEnergyLevel == 0;
	}
	
	// Consume energy and return successful or not
	private boolean consumeEnergy(Integer energy) {
		Integer curEnergy = droneEnergyLevel - energy;
		if(curEnergy < 0) return false;
		else {
			droneEnergyLevel = curEnergy;
			return true;
		}
	}

	
	// Steer and scan consumes 1 energy, and thrust consumes energy equal to the thrusting distance 
	// pass does not consumes energy but re-charge the drone if it has suns in its neighboring blocks
	
    public String[] tick()  {
        int moveRandomChoice, thrustRandomChoice, steerRandomChoice;

        if (droneStrategy.equals(MANUAL_STRATEGY)) {
            Scanner askUser = new Scanner(System.in);
            // generate a move by asking the user - DIAGNOSTIC ONLY
            System.out.print("action?: ");
            trackAction = askUser.nextLine();

            if (trackAction.equals("steer")) {
                System.out.print("direction?: ");
                consumeEnergy(1);
                return new String[] {"steer", askUser.nextLine()};
            } 
            
            else if (trackAction.equals("thrust")) {
            	String[] res;
                do {
                	System.out.println("energy level: " + droneEnergyLevel);
                	System.out.print("distance?: ");
                	res = new String[] {"thrust", askUser.nextLine()};
                } while (!consumeEnergy(Integer.parseInt(res[1])));
                return res;
            } 
            
            else if (trackAction.equals("pass")) {
                return new String[] {"pass", ""};
            } 
            
            else if (trackAction.equals("scan")) {
                consumeEnergy(1);
                return new String[] {"scan", ""};
            }
                
            return new String[] {trackAction, ""};

        } else if (droneStrategy.equals(RANDOM_STRATEGY)){
            // generate a move randomly
            moveRandomChoice = randGenerator.nextInt(100);
            if (moveRandomChoice < 5) {
                trackAction = "pass";
                return new String[] {trackAction, ""};
            } else if (moveRandomChoice < 20) {
                trackAction = "scan";
                consumeEnergy(1);
                return new String[] {trackAction, ""};
            } else if (moveRandomChoice < 50) {
                trackAction = "steer";
                steerRandomChoice = randGenerator.nextInt(ORIENT_LIST.length);
                consumeEnergy(1);
                return new String[] {trackAction, ORIENT_LIST[steerRandomChoice]};
            } else {
                trackAction = "thrust";
                String[] res;
                // Retry until
                do {
                	thrustRandomChoice = randGenerator.nextInt(THRUST_DISTANCE.length);
                	res = new String[] {"thrust", THRUST_DISTANCE[thrustRandomChoice]};
                }
                while (!consumeEnergy(Integer.parseInt(res[1])));
                return res;
            }

        } else if (droneStrategy.equals(OPTIMIZED_STRATEGY)){
        	ArrayList<String> emptyOrientations = new ArrayList<String>();
	        int xOrientation;
	        int yOrientation;
			int newSquareX; 
			int newSquareY;
	        xOrientation = xDIR_MAP.get(droneDirection);
	        yOrientation = yDIR_MAP.get(droneDirection);
	        
	        
	        // If energy is not full and there is at least one sun in its proximity,pass to charge
	        for (int k = 0; k < ORIENT_LIST.length; k++) {
	            String lookThisWay = ORIENT_LIST[k];
	            int offsetX = xDIR_MAP.get(lookThisWay);
	            int offsetY = yDIR_MAP.get(lookThisWay);

	            int checkX = getDroneX() + offsetX;
	            int checkY = getDroneY() + offsetY;
	            if(
	            	partialSpace.isSun(
	            		Math.floorMod(checkX, partialSpace.getWidth()),
	            		Math.floorMod(checkY, partialSpace.getHeight())
	            	) && droneEnergyLevel < droneEnergyCapacity)
	            return new String[] {"pass", ""};
	        }
	        
	        
	        // If there are stars or empty in the current direction, thrust as far as possible without crashing or draining
			int thrustDistance = 0;
			int numberOfStars = 0;
			while (thrustDistance < 3 && droneEnergyLevel > 1) {
				newSquareX = Math.floorMod(getDroneX() + xOrientation * (thrustDistance + 1), partialSpace.getWidth());
				newSquareY = Math.floorMod(getDroneY() + yOrientation * (thrustDistance + 1), partialSpace.getHeight());
				if(partialSpace.isStars(newSquareX, newSquareY)) numberOfStars += 1;
				if(partialSpace.isStars(newSquareX, newSquareY) || partialSpace.isEmpty(newSquareX, newSquareY)) { 
					thrustDistance += 1;
				}
				else break;
			}
			
			if(thrustDistance > 0 && numberOfStars > 0) {
				consumeEnergy(thrustDistance);
				return new String[] {"thrust", String.valueOf(thrustDistance)};
			}
		
        	// Steer to a direction with star
			
			for (int i = 0; i < ORIENT_LIST.length; i++) {
    	        xOrientation = xDIR_MAP.get(ORIENT_LIST[i]);
    	        yOrientation = yDIR_MAP.get(ORIENT_LIST[i]);
    			newSquareX = Math.floorMod(getDroneX() + xOrientation, partialSpace.getWidth());
    			newSquareY = Math.floorMod(getDroneY() + yOrientation, partialSpace.getHeight());
    			thrustDistance = 0; 
    			if(partialSpace.isStars(newSquareX, newSquareY)) {
    				consumeEnergy(1);
    				return new String[] {"steer", ORIENT_LIST[i]};
    			}
    			else if(partialSpace.isEmpty(newSquareX, newSquareY)) {
    				emptyOrientations.add(ORIENT_LIST[i]);
    			}
    		}
    		
			// Steer to a random empty direction, prepare to thrust
    		if (emptyOrientations.size() != 0) {
    			String randomOrientation;
    			do {
    				randomOrientation = emptyOrientations.get(randGenerator.nextInt(emptyOrientations.size()));
    			}
    			while(randomOrientation.equals(droneDirection));
    			consumeEnergy(1);
    			return new String[] {"steer" , randomOrientation};
    		}
    		return new String[] {"pass", ""};
        }
        return new String[] {"", ""};
    }
}

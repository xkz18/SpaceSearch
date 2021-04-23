package GUI;

import java.io.Serializable;

public class SpaceRegion implements Serializable{
    private final String EMPTY_CODE = "EMPTY";
    private final String STARS_CODE = "STARS";
    private final String SUN_CODE = "SUN";
    // All numeric strings are reserved for drones
    private String[][] regionInfo;
    private int width;
    private int height;
    
    public SpaceRegion(int regionWidth, int regionHeight) {
    	width = regionWidth;
    	height = regionHeight;
        regionInfo = new String[width][height];
        for (int x = 0; x < regionWidth; x++) {
        	for (int y = 0; y < regionHeight; y++) {
				setStars(x, y);
        	}
        }
    }


    public String[][] getRegionInfo() { return regionInfo; }
    
    public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setEmpty(int x, int y) {
		regionInfo[x][y] = EMPTY_CODE;
    }
    
    public void setSun(int x, int y) {
    	regionInfo[x][y] = SUN_CODE;
    }
    
    public void setDrone(int x, int y, int droneIdx) {
    	regionInfo[x][y] = Integer.toString(droneIdx);
    }
    
    public void setStars(int x, int y) {
    	regionInfo[x][y] = STARS_CODE;
    }
    
    
	public boolean isEmpty(int x, int y) {
    	return regionInfo[x][y].equals(EMPTY_CODE);
    }
    
	public boolean isSun(int x, int y) {
    	return regionInfo[x][y].equals(SUN_CODE);
    }
	
	public boolean isStars(int x, int y) {
    	return regionInfo[x][y].equals(STARS_CODE);
    }
	
	// Get drone id at location x, y. Return -1 if no Drone found
	public int getDrone(int x, int y) {
		int droneId;
    	try {	
    		droneId = Integer.parseInt(regionInfo[x][y]);
    	}
    	catch (NumberFormatException e) {
    		return -1;
    	}
    	return droneId;	
    }
	
	public boolean isDrone(int x, int y) {
    	try {	
    		Integer.parseInt(regionInfo[x][y]);
    	}
    	catch (NumberFormatException e) {
    		return false;
    	}
    	return true;	
	}
	
    public String getRegionInfo(int x, int y) {
        switch (regionInfo[x][y]) {
        	case EMPTY_CODE:
        		return "empty";
        	case STARS_CODE:
        		return "stars";
        	case SUN_CODE:
        		return "sun";
        	default:
        		return "drone";
        }
    }
    
    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }


    public void renderRegion() {
        int i, j;
        int charWidth = 2 * width + 2;

        // display the rows of the region from top to bottom
        for (j = height - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < width; i++) {
                System.out.print("|");
                switch (regionInfo[i][j]) {
                	case EMPTY_CODE:
                    	System.out.print(" ");
                        break;
                    case STARS_CODE:
                        System.out.print(".");
                        break;
                    case SUN_CODE:
                        System.out.print("s");
                        break;
                    default:
                    	System.out.print(regionInfo[i][j]);
                        break;
                }
            }
            System.out.println("|");
        }
    
        renderHorizontalBar(charWidth);
    }
}
